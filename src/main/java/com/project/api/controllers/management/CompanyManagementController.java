package com.project.api.controllers.management;

import com.project.api.constants.AppUrls;
import com.project.api.controllers.management.base.BaseManagementController;
import com.project.api.entities.Company;
import com.project.api.mappers.CompanyManagementMapper;
import com.project.api.requests.management.company.CreateCompanyManagementRequest;
import com.project.api.requests.management.company.PatchCompanyManagementRequest;
import com.project.api.requests.management.company.UpdateCompanyManagementRequest;
import com.project.api.responses.management.CompanyManagementResponse;
import com.project.api.services.CompanyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Getter
@Slf4j
@RestController
@RequestMapping(CompanyManagementController.BASE_URL)
@RequiredArgsConstructor
public class CompanyManagementController
    extends BaseManagementController<
        Company,
        Long,
        CreateCompanyManagementRequest,
        UpdateCompanyManagementRequest,
        PatchCompanyManagementRequest,
        CompanyManagementResponse> {

  public static final String BASE_URL = AppUrls.MANAGEMENT_API + "/companies";

  private final CompanyService service;
  private final CompanyManagementMapper mapper;
}
