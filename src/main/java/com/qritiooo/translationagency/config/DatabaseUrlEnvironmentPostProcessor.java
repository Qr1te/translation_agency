package com.qritiooo.translationagency.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
    private static final String DATASOURCE_URL_ENV = "SPRING_DATASOURCE_URL";
    private static final String DATASOURCE_USERNAME_ENV = "SPRING_DATASOURCE_USERNAME";
    private static final String DATASOURCE_PASSWORD_ENV = "SPRING_DATASOURCE_PASSWORD";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {
        if (StringUtils.hasText(environment.getProperty(DATASOURCE_URL_ENV))
                || StringUtils.hasText(environment.getProperty("spring.datasource.url"))) {
            return;
        }

        String databaseUrl = environment.getProperty(DATABASE_URL_ENV);
        if (!StringUtils.hasText(databaseUrl)) {
            return;
        }

        URI uri = URI.create(databaseUrl.trim());
        if (!isSupportedScheme(uri.getScheme())
                || !StringUtils.hasText(uri.getHost())
                || !StringUtils.hasText(uri.getPath())
                || "/".equals(uri.getPath())) {
            return;
        }

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("spring.datasource.url", buildJdbcUrl(uri));

        DatabaseCredentials credentials = extractCredentials(uri);
        if (!StringUtils.hasText(environment.getProperty(DATASOURCE_USERNAME_ENV))
                && StringUtils.hasText(credentials.username())) {
            properties.put("spring.datasource.username", credentials.username());
        }

        if (!StringUtils.hasText(environment.getProperty(DATASOURCE_PASSWORD_ENV))
                && StringUtils.hasText(credentials.password())) {
            properties.put("spring.datasource.password", credentials.password());
        }

        environment.getPropertySources()
                .addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, properties));
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
