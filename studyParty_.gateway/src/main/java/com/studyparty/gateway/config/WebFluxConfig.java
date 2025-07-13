package com.studyparty.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.core.io.ResourceLoader;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    private final ResourceLoader resourceLoader;
    // 新增静态资源配置
    @Value("${gateway.static-resource.image-path:/images}")
    private String staticPath;

    @Value("${gateway.static-resource.local-dir:D:/studyParty/}")
    private String localDir;

    public WebFluxConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void addResourceHandlers(org.springframework.web.reactive.config.ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:D:/studyParty/")
                .resourceChain(true);
    }
}