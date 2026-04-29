package com.qritiooo.translationagency.config;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

public class DatabaseUrlEnvironmentPostProcessor
        implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "databaseUrlProperties";
    private static final String DATABASE_URL_ENV = "DATABASE_URL";
    private static final String DATABASE_URL_FILE_ENV = "DATABASE_URL_FILE";
    private static final String DATASOURCE_URL_ENV = "SPRING_DATASOURCE_URL";
    private static final String DATASOURCE_USERNAME_ENV = "SPRING_DATASOURCE_USERNAME";
    private static final String DATASOURCE_PASSWORD_ENV = "SPRING_DATASOURCE_PASSWORD";
    private static final String DATASOURCE_URL_FILE_ENV = "SPRING_DATASOURCE_URL_FILE";
    private static final String DATASOURCE_USERNAME_FILE_ENV = "SPRING_DATASOURCE_USERNAME_FILE";
    private static final String DATASOURCE_PASSWORD_FILE_ENV = "SPRING_DATASOURCE_PASSWORD_FILE";
    private static final String DB_PASSWORD_FILE_ENV = "DB_PASSWORD_FILE";
    private static final String DATASOURCE_URL_PROPERTY = "spring.datasource.url";
    private static final String DATASOURCE_USERNAME_PROPERTY = "spring.datasource.username";
    private static final String DATASOURCE_PASSWORD_PROPERTY = "spring.datasource.password";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        Map<String, Object> properties = new LinkedHashMap<>();
        addFileBackedPropertyIfMissing(environment, properties,
                DATASOURCE_URL_FILE_ENV, DATASOURCE_URL_ENV, DATASOURCE_URL_PROPERTY);
        addFileBackedPropertyIfMissing(environment, properties,
                DATASOURCE_USERNAME_FILE_ENV, DATASOURCE_USERNAME_ENV, DATASOURCE_USERNAME_PROPERTY);
        addFileBackedPropertyIfMissing(environment, properties,
                DATASOURCE_PASSWORD_FILE_ENV, DATASOURCE_PASSWORD_ENV, DATASOURCE_PASSWORD_PROPERTY);
        addFileBackedPropertyIfMissing(environment, properties,
                DB_PASSWORD_FILE_ENV, DATASOURCE_PASSWORD_ENV, DATASOURCE_PASSWORD_PROPERTY);

        if (!StringUtils.hasText(environment.getProperty(DATASOURCE_URL_ENV))
                && !hasText(environment, properties, DATASOURCE_URL_PROPERTY)) {
            String databaseUrl = resolveDatabaseUrl(environment);
            if (StringUtils.hasText(databaseUrl)) {
                URI uri = URI.create(databaseUrl.trim());
                if (isSupportedScheme(uri.getScheme())
                        && StringUtils.hasText(uri.getHost())
                        && StringUtils.hasText(uri.getPath())
                        && !"/".equals(uri.getPath())) {
                    properties.put(DATASOURCE_URL_PROPERTY, buildJdbcUrl(uri));

                    DatabaseCredentials credentials = extractCredentials(uri);
                    if (!StringUtils.hasText(environment.getProperty(DATASOURCE_USERNAME_ENV))
                            && !hasText(environment, properties, DATASOURCE_USERNAME_PROPERTY)
                            && StringUtils.hasText(credentials.username())) {
                        properties.put(DATASOURCE_USERNAME_PROPERTY, credentials.username());
                    }

                    if (!StringUtils.hasText(environment.getProperty(DATASOURCE_PASSWORD_ENV))
                            && !hasText(environment, properties, DATASOURCE_PASSWORD_PROPERTY)
                            && StringUtils.hasText(credentials.password())) {
                        properties.put(DATASOURCE_PASSWORD_PROPERTY, credentials.password());
                    }
                }
            }
        }

        if (!properties.isEmpty()) {
            environment.getPropertySources()
                    .addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
        }
    }

    private boolean isSupportedScheme(String scheme) {
        return "postgres".equalsIgnoreCase(scheme)
                || "postgresql".equalsIgnoreCase(scheme);
    }

    private String buildJdbcUrl(URI uri) {
        StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                .append(uri.getHost());

        if (uri.getPort() > 0) {
            jdbcUrl.append(':').append(uri.getPort());
        }

        jdbcUrl.append(uri.getPath());

        if (StringUtils.hasText(uri.getRawQuery())) {
            jdbcUrl.append('?').append(uri.getRawQuery());
        }

        return jdbcUrl.toString();
    }

    private void addFileBackedPropertyIfMissing(ConfigurableEnvironment environment,
                                                Map<String, Object> properties,
                                                String fileEnvironmentKey,
                                                String directEnvironmentKey,
                                                String propertyKey) {
        if (StringUtils.hasText(environment.getProperty(directEnvironmentKey))
                || hasText(environment, properties, propertyKey)) {
            return;
        }

        String fileLocation = environment.getProperty(fileEnvironmentKey);
        if (!StringUtils.hasText(fileLocation)) {
            return;
        }

        properties.put(propertyKey, readSecretFile(fileEnvironmentKey, fileLocation));
    }

    private String resolveDatabaseUrl(ConfigurableEnvironment environment) {
        String databaseUrl = environment.getProperty(DATABASE_URL_ENV);
        if (StringUtils.hasText(databaseUrl)) {
            return databaseUrl;
        }

        String databaseUrlFile = environment.getProperty(DATABASE_URL_FILE_ENV);
        if (!StringUtils.hasText(databaseUrlFile)) {
            return null;
        }

        return readSecretFile(DATABASE_URL_FILE_ENV, databaseUrlFile);
    }

    private boolean hasText(ConfigurableEnvironment environment,
                            Map<String, Object> properties,
                            String propertyKey) {
        Object pendingValue = properties.get(propertyKey);
        if (pendingValue instanceof String pendingText && StringUtils.hasText(pendingText)) {
            return true;
        }

        return StringUtils.hasText(environment.getProperty(propertyKey));
    }

    private String readSecretFile(String environmentKey, String fileLocation) {
        try {
            Path secretPath = Path.of(fileLocation.trim());
            if (Files.isDirectory(secretPath)) {
                throw new IllegalStateException(
                        environmentKey + " points to a directory, but a secret file is required");
            }

            String value = Files.readString(secretPath, StandardCharsets.UTF_8).trim();
            if (!StringUtils.hasText(value)) {
                throw new IllegalStateException(environmentKey + " points to an empty file");
            }
            return value;
        } catch (InvalidPathException exception) {
            throw new IllegalStateException(environmentKey + " contains an invalid path", exception);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Failed to read secret file configured by " + environmentKey, exception);
        }
    }

    private DatabaseCredentials extractCredentials(URI uri) {
        String userInfo = uri.getRawUserInfo();
        if (!StringUtils.hasText(userInfo)) {
            return new DatabaseCredentials(null, null);
        }

        int separatorIndex = userInfo.indexOf(':');
        if (separatorIndex < 0) {
            return new DatabaseCredentials(decode(userInfo), null);
        }

        String username = decode(userInfo.substring(0, separatorIndex));
        String password = decode(userInfo.substring(separatorIndex + 1));
        return new DatabaseCredentials(username, password);
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private record DatabaseCredentials(String username, String password) {
    }
}
