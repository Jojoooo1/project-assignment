package com.project.api.exceptions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Configuration class for customizing the global error attributes. It is necessary to provide full
 * compatibility with RFC 9457 as exception can be thrown outside Spring MVC context.
 */
@Configuration
public class GlobalErrorAttributeConfig {

  /**
   * Bean for customizing the error attributes.
   *
   * @return An instance of {@link ErrorAttributes} that customizes the error response.
   */
  @Bean
  public ErrorAttributes errorAttributes() {
    return new DefaultErrorAttributes() {
      @Override
      public Map<String, Object> getErrorAttributes(
          WebRequest request, ErrorAttributeOptions options) {

        Map<String, Object> problemDetailErrorResponse = new LinkedHashMap<>();
        Map<String, Object> defaultErrorResponse = super.getErrorAttributes(request, options);

        problemDetailErrorResponse.put("type", "about:blank");

        if (defaultErrorResponse.containsKey("status")) {
          HttpStatus status = HttpStatus.valueOf((Integer) defaultErrorResponse.get("status"));
          problemDetailErrorResponse.put("title", status.getReasonPhrase());
          problemDetailErrorResponse.put("status", status.value());
        } else {
          problemDetailErrorResponse.put("title", "Request Failed.");
          problemDetailErrorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        // Can be removed for security purpose.
        problemDetailErrorResponse.put(
            "detail", defaultErrorResponse.getOrDefault("message", StringUtils.EMPTY));

        problemDetailErrorResponse.put(
            "instance", defaultErrorResponse.getOrDefault("path", StringUtils.EMPTY));

        problemDetailErrorResponse.put("errors", new ArrayList<>());

        return problemDetailErrorResponse;
      }
    };
  }
}
