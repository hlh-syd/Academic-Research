package com.researchworkbench.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Jwt jwt, Storage storage, Cors cors) {

    public record Jwt(@NotBlank String secret, @NotNull Long expirationMillis) {
    }

    public record Storage(@NotBlank String root) {
    }

    public record Cors(@NotBlank String allowedOrigins) {
    }
}
