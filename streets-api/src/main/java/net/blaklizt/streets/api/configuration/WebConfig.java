package net.blaklizt.streets.api.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by photon on 2015/12/30.
 */
@Configuration
@EnableWebMvc
@EnableSwagger2
public class WebConfig {// extends WebMvcConfigurerAdapter {

//	//without boot
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("swagger-ui.html")
//			.addResourceLocations("classpath:/META-INF/resources/");
//
//		registry.addResourceHandler("/webjars/**")
//			.addResourceLocations("classpath:/META-INF/resources/webjars/");
//	}

}
