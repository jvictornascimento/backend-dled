package br.com.dled.dledbackend;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
				.info(new Info()
						.title("3D Led Api")
						.version("1.0")
						.description("Documentação da API"));
	}

}
