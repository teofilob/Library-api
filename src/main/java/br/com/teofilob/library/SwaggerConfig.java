package br.com.teofilob.library;

import org.springframework.context.annotation.Bean;

import io.swagger.v3.core.model.ApiDescription;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.RequestHandlerSelectors.*;

@EnableSwagger2
public class SwaggerConfig {
	
	private static final String BASE_PACKAGE = "br.com.teofilob.library.*";
    private static final String API_TITLE = "Book Stock API";
    private static final String API_DESCRIPTION = "REST API for library management";
    private static final String CONTACT_NAME = "Teofilo Beloti";
    private static final String CONTACT_GITHUB = "https://github.com/teofilob";
    private static final String CONTACT_EMAIL = "teofilob@gmail.com";
	
	
	@Bean
    public Docket api() { 

        return new Docket(DocumentationType.SWAGGER_2)  
        		.select()
                .apis(basePackage(BASE_PACKAGE))
                .paths(PathSelectors.any())
                .build()
                .apiInfo( buildApiInfo() );
    }
	
	 private ApiInfo buildApiInfo() {
	        return new ApiInfoBuilder()
	                .title(API_TITLE)
	                .description(API_DESCRIPTION)
	                .version("1")
	                .contact(new Contact(CONTACT_NAME, CONTACT_GITHUB, CONTACT_EMAIL))
	                .build();
	    }
}
