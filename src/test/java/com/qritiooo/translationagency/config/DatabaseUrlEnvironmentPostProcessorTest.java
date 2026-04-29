package com.qritiooo.translationagency.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.SpringApplication;
import org.springframework.mock.env.MockEnvironment;

class DatabaseUrlEnvironmentPostProcessorTest {

    private final DatabaseUrlEnvironmentPostProcessor processor =
            new DatabaseUrlEnvironmentPostProcessor();

    @TempDir
    Path tempDir;

    @Test
    void readsDatasourcePasswordFromSecretFile() throws IOException {
        Path passwordFile = Files.writeString(tempDir.resolve("db-password.txt"), "super-secret\n");
        MockEnvironment environment = new MockEnvironment()
                .withProperty("SPRING_DATASOURCE_PASSWORD_FILE", passwordFile.toString());

        processor.postProcessEnvironment(environment, new SpringApplication(Object.class));

        assertEquals("super-secret", environment.getProperty("spring.datasource.password"));
    }

    @Test
    void derivesDatasourcePropertiesFromDatabaseUrl() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("DATABASE_URL", "postgres://demo-user:demo-pass@db.example.com:5432/demo_db");

        processor.postProcessEnvironment(environment, new SpringApplication(Object.class));

        assertEquals("jdbc:postgresql://db.example.com:5432/demo_db",
                environment.getProperty("spring.datasource.url"));
        assertEquals("demo-user", environment.getProperty("spring.datasource.username"));
        assertEquals("demo-pass", environment.getProperty("spring.datasource.password"));
    }

    @Test
    void keepsExplicitDatasourcePasswordOverSecretFile() throws IOException {
        Path passwordFile = Files.writeString(tempDir.resolve("db-password.txt"), "file-secret");
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.datasource.password", "env-secret")
                .withProperty("SPRING_DATASOURCE_PASSWORD_FILE", passwordFile.toString());

        processor.postProcessEnvironment(environment, new SpringApplication(Object.class));

        assertEquals("env-secret", environment.getProperty("spring.datasource.password"));
    }

    @Test
    void failsFastWhenSecretFileIsMissing() {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("SPRING_DATASOURCE_PASSWORD_FILE",
                        tempDir.resolve("missing-password.txt").toString());

        assertThrows(IllegalStateException.class,
                () -> processor.postProcessEnvironment(environment, new SpringApplication(Object.class)));
    }
}
