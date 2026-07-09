package com.Harshit.note_app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  public static final String BEARER_AUTH = "bearerAuth";

  @Bean
  public OpenAPI notesOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Note API")
            .description("""
                REST API for managing personal notes with JWT authentication.

                ## Getting started
                1. Register a new account via **POST /api/auth/register**
                2. Log in via **POST /api/auth/login** to receive a JWT token
                3. Click **Authorize** and enter: `Bearer <your-token>`
                4. Call the Note endpoints — each user only sees their own notes
                """)
            .version("1.0.0")
            .contact(new Contact().name("Note API")))
        .components(new Components()
            .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                .name(BEARER_AUTH)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token obtained from POST /api/auth/login. Enter the token prefixed with 'Bearer '.")));
  }
}
