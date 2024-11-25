package com.openframe.openframe.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title = "Open-Frame API 명세서",
                description = "Open-Frame BE API 명세서입니다.",
                version = "v1")
)
@RequiredArgsConstructor
@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi stOpenApi() {

        return GroupedOpenApi.builder()
                .group("Open-Frame API v1")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication",createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme(){
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .in(SecurityScheme.In.HEADER).name("Authorization");
    }
}

