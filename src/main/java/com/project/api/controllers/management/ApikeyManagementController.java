package com.project.api.controllers.management;

import com.project.api.constants.AppUrls;
import com.project.api.controllers.management.base.BaseManagementController;
import com.project.api.entities.ApiKey;
import com.project.api.mappers.ApiKeyMapper;
import com.project.api.requests.management.apikey.CreateApiKeyManagementRequest;
import com.project.api.requests.management.apikey.PatchApiKeyManagementRequest;
import com.project.api.requests.management.apikey.UpdateApiKeyManagementRequest;
import com.project.api.responses.management.ApikeyManagementResponse;
import com.project.api.services.ApiKeyService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Getter
@Slf4j
@RestController
@RequestMapping(ApikeyManagementController.BASE_URL)
@RequiredArgsConstructor
public class ApikeyManagementController
    extends BaseManagementController<
        ApiKey,
        Long,
        CreateApiKeyManagementRequest,
        UpdateApiKeyManagementRequest,
        PatchApiKeyManagementRequest,
        ApikeyManagementResponse> {

  public static final String BASE_URL = AppUrls.MANAGEMENT_API + "/api-keys";

  private final ApiKeyService service;
  private final ApiKeyMapper mapper;
}
