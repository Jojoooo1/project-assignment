package com.project.api.testutils.builders;

import static com.project.api.testutils.TestUtil.random;

import com.project.api.entities.Company;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CompanyBuilder {

  public static Company company() {
    return Company.builder().name(random()).slug(random()).build();
  }

  public static Company customer() {
    return Company.builder().name(random()).slug(random()).isCustomer(true).build();
  }

  public static Company management() {
    return Company.builder().name(random()).slug(random()).isManagement(true).build();
  }

  public static Company internal() {
    return Company.builder().name(random()).slug(random()).isInternal(true).build();
  }
}
