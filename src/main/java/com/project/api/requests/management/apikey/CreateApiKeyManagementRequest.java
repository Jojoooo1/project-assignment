package com.project.api.requests.management.apikey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateApiKeyManagementRequest(@NotNull Long companyId, @NotBlank String name) {}
