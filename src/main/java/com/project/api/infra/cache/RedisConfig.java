package com.project.api.infra.cache;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Setter
@Getter
@Validated
@ConfigurationProperties(prefix = "redis")
public class RedisConfig {
  @NotBlank private String host;
  @NotNull private int port;
  @NotBlank private String password;
  @NotNull private int timeoutInMs;
  @NotNull private int defaultTTL;
}
