package com.project.api.requests.management.apikey;

import jakarta.validation.constraints.NotBlank;

public record UpdateApiKeyManagementRequest(@NotBlank String name) {}
