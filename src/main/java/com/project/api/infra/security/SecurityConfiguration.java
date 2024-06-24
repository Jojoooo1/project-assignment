package com.project.api.infra.security;

import com.project.api.constants.AppUrls;
import com.project.api.infra.auth.UserRoles;
import com.project.api.infra.auth.providers.ApiKeyAuthenticationFilter;
import com.project.api.infra.auth.providers.ApiKeyAuthenticationProvider;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

@Slf4j
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  public AuthenticationProvider companyApiKeyAuthenticationProvider() {
    return new ApiKeyAuthenticationProvider();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    return new ProviderManager(
        Collections.singletonList(this.companyApiKeyAuthenticationProvider()));
  }

  @Bean
  public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {

    http.addFilterBefore(
            new ApiKeyAuthenticationFilter(
                AppUrls.MANAGEMENT_API + "/**", this.authenticationManager()),
            AnonymousAuthenticationFilter.class)
        .addFilterBefore(
            new ApiKeyAuthenticationFilter(
                AppUrls.INTERNAL_API + "/**", this.authenticationManager()),
            AnonymousAuthenticationFilter.class)
        .addFilterBefore(
            new ApiKeyAuthenticationFilter(
                AppUrls.CUSTOMER_API + "/**", this.authenticationManager()),
            AnonymousAuthenticationFilter.class)
        .authorizeHttpRequests(
            authorize ->
                authorize
                    // Management API
                    .requestMatchers(AppUrls.MANAGEMENT_API + "/**")
                    .hasAnyRole(UserRoles.MANAGEMENT_API_USER.getRole())

                    // Internal API
                    .requestMatchers(AppUrls.INTERNAL_API + "/**")
                    .hasAnyRole(UserRoles.INTERNAL_API_USER.getRole())

                    // Customer API
                    .requestMatchers(AppUrls.CUSTOMER_API + "/**")
                    .hasAnyRole(UserRoles.CUSTOMER_API_USER.getRole())

                    // Actuator API
                    .requestMatchers("/actuator/**")
                    .permitAll()

                    // Public API
                    .requestMatchers(AppUrls.PUBLIC + "/**")
                    .permitAll()

                    // Authorize global error page.
                    .requestMatchers("/error")
                    .permitAll()

                    // Else deny all request by default.
                    .anyRequest()
                    .denyAll())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable);

    return http.build();
  }
}
