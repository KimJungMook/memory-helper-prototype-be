package com.website.military.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
        .name(jwt)
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        );
        
        Info info = new Info()
        .title("memory-helper-API 명세서")
        .description("memory-helper 관련 api 만들기")
        .version("1.0.0");

        return new OpenAPI()
        .components(new Components())
        .info(info)
        .addSecurityItem(securityRequirement)
        .addServersItem(new Server().url("/")) // swagger상의 코스에러 문제 해결. 요청하는 주소를 바꿈.
        .components(components);
    }

}
