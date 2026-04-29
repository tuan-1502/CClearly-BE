package com.swp391.cclearly.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("CClearly API")
                .description("API documentation cho hệ thống cửa hàng kính mắt CClearly")
                .version("1.0.0")
                .contact(new Contact()
                    .name("CClearly Team")
                    .email("contact@cclearly.com")
                    .url("https://cclearly.com"))
                .license(new License()
                    .name("Private License")
                    .url("https://cclearly.com/license")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Development Server")))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", createBearerScheme()));
    }

    private SecurityScheme createBearerScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .description("Nhập JWT token. Ví dụ: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
    }
}
