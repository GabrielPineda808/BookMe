package com.example.book.integration;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class IntegrationTestBase {

    @SuppressWarnings("resource") // managed by Testcontainers lifecycle
    @Container
    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("bookme")
                    .withUsername("bookme")
                    .withPassword("bookme");

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("security.jwt.secret-key", () -> "aGVsbG93b3JsZGhlbGxvd29ybGRoZWxsb3dvcmxkMTIzNDU2Nzg=");
        registry.add("security.jwt.expiration-time", () -> "3600000");
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> "1025");
        registry.add("spring.mail.username", () -> "test");
        registry.add("spring.mail.password", () -> "test");
    }
}
