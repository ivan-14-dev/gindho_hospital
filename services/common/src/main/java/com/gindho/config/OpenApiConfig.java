package com.gindho.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("GinDHO Hospital Management API")
                .version("1.0.0")
                .description("""
                    API REST pour la gestion hospitalière.
                    
                    ## Architecture
                    - 30 microservices indépendants
                    - Authentification via Keycloak (OAuth2 / OpenID Connect)
                    - Communication asynchrone via Apache Kafka
                    - Base de données dédiée par service
                    
                    ## Authentification
                    - Utiliser `/api/auth/login` pour obtenir un token JWT
                    - Ajouter le token dans le header `Authorization: Bearer <token>`
                    - Les permissions sont vérifiées via `/api/authorization/check`
                    """)
                .contact(new Contact()
                    .name("GinDHO Team")
                    .email("contact@gindho.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://gindho.com")))
            .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
            .components(new Components()
                .addSecuritySchemes("BearerAuth", new SecurityScheme()
                    .name("BearerAuth")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
