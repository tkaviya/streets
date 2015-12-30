package net.blaklizt.streets.api;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackageClasses = {
	LogResource.class
})
public class Swagger2SpringBoot {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.any())
			.paths(PathSelectors.any())
			.build();
	}
//	public static void main(String[] args) {
//		ApplicationContext ctx = SpringApplication.run(Swagger2SpringBoot.class, args);
//	}
//
//
//	@Bean
//	public Docket petApi() {
//		return new Docket(DocumentationType.SWAGGER_2)
//			.select()
//			.apis(RequestHandlerSelectors.any())
//			.paths(PathSelectors.any())
//			.build()
//			.pathMapping("/")
//			.directModelSubstitute(LocalDate.class,
//				String.class)
//			.genericModelSubstitutes(ResponseEntity.class)
//			.alternateTypeRules(
//				newRule(typeResolver.resolve(DeferredResult.class,
//						typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
//					typeResolver.resolve(WildcardType.class)))
//			.useDefaultResponseMessages(false)
//			.globalResponseMessage(RequestMethod.GET,
//				newArrayList(new ResponseMessageBuilder()
//					.code(500)
//					.message("500 message")
//					.responseModel(new ModelRef("Error"))
//					.build()))
//			.securitySchemes(newArrayList(apiKey()))
//			.securityContexts(newArrayList(securityContext()))
//			.enableUrlTemplating(true)
//			.globalOperationParameters(
//				newArrayList(new ParameterBuilder()
//					.name("someGlobalParameter")
//					.description("Description of someGlobalParameter")
//					.modelRef(new ModelRef("string"))
//					.parameterType("query")
//					.required(true)
//					.build()))
//			;
//	}
//
//	@Autowired
//	private TypeResolver typeResolver;
//
//	private ApiKey apiKey() {
//		return new ApiKey("mykey", "api_key", "header");
//	}
//
//	private SecurityContext securityContext() {
//		return SecurityContext.builder()
//			.securityReferences(defaultAuth())
//			.forPaths(PathSelectors.regex("/anyPath.*"))
//			.build();
//	}
//
//	List<SecurityReference> defaultAuth() {
//		AuthorizationScope authorizationScope
//			= new AuthorizationScope("global", "accessEverything");
//		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//		authorizationScopes[0] = authorizationScope;
//		return newArrayList(
//			new SecurityReference("mykey", authorizationScopes));
//	}
//
//	@Bean
//	SecurityConfiguration security() {
//		return new SecurityConfiguration(
//			"test-app-client-id",
//			"test-app-client-secret",
//			"test-app-realm",
//			"test-app",
//			"apiKey",
//			ApiKeyVehicle.HEADER,
//			"," /*scope separator*/);
//	}
//
//	@Bean
//	UiConfiguration uiConfig() {
//		return new UiConfiguration(
//			"validatorUrl");
//	}
}