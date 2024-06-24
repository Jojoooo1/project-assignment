package com.project.api.infra.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserRoles {
  MANAGEMENT_API_USER("management_api_user"),
  INTERNAL_API_USER("internal_api_user"),
  CUSTOMER_API_USER("customer_api_user");

  private final String role;
}
