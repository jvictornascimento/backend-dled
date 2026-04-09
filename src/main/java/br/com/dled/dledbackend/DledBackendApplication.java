package br.com.dled.dledbackend;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication

public class DledBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(DledBackendApplication.class, args);
	}
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components().addSecuritySchemes(
						"apiKeyAuth",
						new SecurityScheme()
								.type(SecurityScheme.Type.APIKEY)
								.in(SecurityScheme.In.HEADER)
								.name("X-API-Key")
				))
				.addSecurityItem(new SecurityRequirement().addList("apiKeyAuth"))
				.info(new Info()
						.title("3D Led Api")
						.version("1.0")
						.description("Documentação da API"));
	}

}
