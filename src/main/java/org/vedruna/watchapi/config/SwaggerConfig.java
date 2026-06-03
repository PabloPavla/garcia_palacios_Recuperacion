package org.vedruna.watchapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI y Swagger para la documentación de la API.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Define el bean OpenAPI personalizado para incluir información del proyecto 
     * y soporte global para la autenticación basada en JSON Web Tokens (JWT).
     * 
     * @return Una instancia de OpenAPI.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("WatchAPI")
                        .version("1.0.0")
                        .description("API REST para gestionar una base de datos de películas y series con autenticación por JWT, favoritos y reseñas."))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
