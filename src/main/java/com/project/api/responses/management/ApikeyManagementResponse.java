package com.project.api.responses.management;

import java.time.LocalDateTime;

public record ApikeyManagementResponse(
    Long id,
    Long companyId,
    String name,
    String key,
    Boolean isActive,
    String createdBy,
    String updatedBy,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
