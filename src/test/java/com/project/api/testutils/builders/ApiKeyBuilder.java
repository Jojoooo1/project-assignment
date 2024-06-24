package com.project.api.testutils.builders;

import static com.project.api.testutils.TestUtil.random;

import com.project.api.entities.ApiKey;
import com.project.api.entities.Company;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiKeyBuilder {

  public static ApiKey apiKey(final Company company) {
    return ApiKey.builder().name(random()).companyId(company.getId()).build();
  }

}
