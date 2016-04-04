package net.blaklizt.streets.restapi.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan("net.blaklizt.streets.restapi.test")
public class ConfigTest extends WebMvcConfigurerAdapter {

    public ConfigTest() {
        super();
    }

    // API

}