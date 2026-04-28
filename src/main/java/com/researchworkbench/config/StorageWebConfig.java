package com.researchworkbench.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StorageWebConfig implements WebMvcConfigurer {

    private final AppProperties appProperties;

    public StorageWebConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path storageRoot = Paths.get(appProperties.storage().root()).toAbsolutePath().normalize();
        registry.addResourceHandler("/storage/**")
            .addResourceLocations(storageRoot.toUri().toString());
    }
}
