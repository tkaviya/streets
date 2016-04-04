//package net.blaklizt.streets.api;

//
///**
// * Created by photon on 2015/12/30.
// */
//@Configuration
//@EnableSwagger2
//public class SpringSwaggerConfig extends WebMvcConfigurerAdapter {
//
//	@Autowired
//	private SpringSwaggerConfig swaggerConfig;
//
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler(WEB_JAR_RESOURCE_PATTERNS)
//			.addResourceLocations(WEB_JAR_RESOURCE_LOCATION).setCachePeriod(0);
//	}
//
//	@Bean
//	public InternalResourceViewResolver getInternalResourceViewResolver() {
//		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
//		resolver.setPrefix(WEB_JAR_VIEW_RESOLVER_PREFIX);
//		resolver.setSuffix(WEB_JAR_VIEW_RESOLVER_SUFFIX);
//		return resolver;
//	}
//
//	@Override
//	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//		configurer.enable();
//	}
//
//	@Bean
//	public SwaggerSpringMvcPlugin swaggerSpringMvcPlugin() {
//		return new SwaggerSpringMvcPlugin(swaggerConfig)
//			.directModelSubstitute(DateTime.class, String.class);
//	}
//}