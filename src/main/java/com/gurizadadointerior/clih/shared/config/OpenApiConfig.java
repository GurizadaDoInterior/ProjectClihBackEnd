package com.gurizadadointerior.clih.shared.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI projectClihOpenApi() {
		var bearerScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT");

		return new OpenAPI()
			.info(new Info()
				.title("Clih API")
				.description("API do aplicativo ProjectClih")
				.version("v1"))
			.components(new Components().addSecuritySchemes("bearerAuth", bearerScheme))
			.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
	}
}
