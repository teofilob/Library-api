package br.com.teofilob.library;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(new Info()
                .title("Book Stock API")
                .description("REST API for library management")
                .version("1")
                .contact(new Contact()
                        .name("Teofilo Beloti")
                        .url("https://github.com/teofilob")
                        .email("teofilob@gmail.com")));
    }
}
