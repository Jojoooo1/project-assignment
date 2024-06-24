package com.project.api.mappers;

import com.project.api.entities.Company;
import com.project.api.mappers.base.ManagementBaseMapper;
import com.project.api.requests.management.company.CreateCompanyManagementRequest;
import com.project.api.requests.management.company.PatchCompanyManagementRequest;
import com.project.api.requests.management.company.UpdateCompanyManagementRequest;
import com.project.api.responses.management.CompanyManagementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMapper
    extends ManagementBaseMapper<
        Company,
        CreateCompanyManagementRequest,
        UpdateCompanyManagementRequest,
        PatchCompanyManagementRequest,
        CompanyManagementResponse> {}
