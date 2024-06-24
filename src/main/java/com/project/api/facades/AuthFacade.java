package com.project.api.facades;

import com.project.api.exceptions.types.InternalServerErrorException;
import com.project.api.infra.auth.providers.ApiKeyAuthentication;
import com.project.api.infra.auth.providers.ApiKeyAuthentication.ApiKeyDetails;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@UtilityClass
public class AuthFacade {

  public static Long getCompanyId() {
    return getCompanyIdOptional().orElse(null);
  }

  public static String getCompanySlug() {
    return getCompanySlugOptional().orElse(StringUtils.EMPTY);
  }

  public static String getUserEmail() {
    return getUserEmailOptional().orElse(StringUtils.EMPTY);
  }

  public static Optional<Long> getCompanyIdOptional() {
    try {
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (isApiKey(authentication)) {
        return getCompanyIdFromApikey(authentication);
      }
      return Optional.empty();
    } catch (final Exception ex) {
      log.error("error getting company_id from AuthFacade", ex);
      throw new InternalServerErrorException();
    }
  }

  public static Optional<String> getCompanySlugOptional() {
    try {
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (isApiKey(authentication)) {
        return getCompanySlugFromApikey(authentication);
      }
      return Optional.empty();
    } catch (final Exception ex) {
      log.error("error getting company_slug from AuthFacade", ex);
      throw new InternalServerErrorException();
    }
  }

  public static Optional<String> getUserEmailOptional() {
    try {
      final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (isApiKey(authentication)) {
        final ApiKeyAuthentication apiKeyAuthentication = (ApiKeyAuthentication) authentication;
        return Optional.ofNullable(apiKeyAuthentication.getApiKeyDetails().getEmail());
      }
      return Optional.empty();
    } catch (final Exception ex) {
      log.error("error getting user_email from AuthFacade", ex);
      throw new InternalServerErrorException();
    }
  }

  private boolean isApiKey(final Authentication authentication) {
    return authentication instanceof ApiKeyAuthentication;
  }

  private Optional<Long> getCompanyIdFromApikey(final Authentication authentication) {

    final ApiKeyAuthentication apiKeyAuthentication = (ApiKeyAuthentication) authentication;
    final ApiKeyDetails apiKeyDetails = apiKeyAuthentication.getApiKeyDetails();

    if (apiKeyDetails.getCompanyId() == null) {
      log.warn("api-key '{}' does not have a company_id", apiKeyDetails.getId());
    }

    return Optional.ofNullable(apiKeyDetails.getCompanyId());
  }

  private Optional<String> getCompanySlugFromApikey(final Authentication authentication) {

    final ApiKeyAuthentication apiKeyAuthentication = (ApiKeyAuthentication) authentication;
    final ApiKeyDetails apiKeyDetails = apiKeyAuthentication.getApiKeyDetails();

    if (StringUtils.isBlank(apiKeyDetails.getCompanySlug())) {
      log.warn("api-key '{}' does not have a company_slug", apiKeyDetails.getId());
    }

    return Optional.ofNullable(apiKeyDetails.getCompanySlug());
  }
}
