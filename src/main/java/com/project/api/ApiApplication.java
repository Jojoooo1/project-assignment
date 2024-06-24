package com.project.api;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@EnableCaching
@ConfigurationPropertiesScan
@SpringBootApplication
public class ApiApplication {

  public static void main(String[] args) {

    final Runtime r = Runtime.getRuntime();

    log.info("[APP] Active processors: {}", r.availableProcessors());
    log.info("[APP] Total memory: {}", r.totalMemory());
    log.info("[APP] Free memory: {}", r.freeMemory());
    log.info("[APP] Max memory: {}", r.maxMemory());

    SpringApplication.run(ApiApplication.class, args);
  }

  @PostConstruct
  void started() {
    TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
  }
}
