package com.project.api.mappers;

import static com.project.api.testutils.TestUtil.random;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.project.api.entities.ApiKey;
import com.project.api.requests.management.apikey.CreateApiKeyManagementRequest;
import com.project.api.requests.management.apikey.PatchApiKeyManagementRequest;
import com.project.api.requests.management.apikey.UpdateApiKeyManagementRequest;
import com.project.api.responses.management.ApikeyManagementResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ApiKeyMapperTest {

  private final ApiKeyMapper mapper = Mappers.getMapper(ApiKeyMapper.class);

  @Test
  void mapper_verifyCreateManagementRequest() {
    CreateApiKeyManagementRequest request = new CreateApiKeyManagementRequest(1L, "test");
    ApiKey entity = this.mapper.toEntity(request);

    assertEquals(request.companyId(), entity.getCompanyId());
    assertEquals(request.name(), entity.getName());
  }

  @Test
  void mapper_verifyUpdateManagementRequest() {
    UpdateApiKeyManagementRequest request = new UpdateApiKeyManagementRequest(random());

    ApiKey entity = new ApiKey();
    entity.setName("to-be-updated");
    ApiKey updatedEntity = this.mapper.updateWithManagementRequest(request, entity);

    assertEquals(request.name(), updatedEntity.getName());
  }

  @Test
  void mapper_verifyPatchManagementRequest() {
    PatchApiKeyManagementRequest request = new PatchApiKeyManagementRequest(null);

    ApiKey entity = new ApiKey();
    entity.setName("should-not-be-updated");
    ApiKey patchedEntity = this.mapper.patchWithManagementRequest(request, entity);

    assertEquals(entity.getName(), patchedEntity.getName());
  }

  @Test
  void mapper_verifyToManagementResponse() {
    ApiKey entity = new ApiKey();

    entity.setId(1L);
    entity.setCompanyId(1L);
    entity.setName(random());
    entity.setKey(random());
    entity.setCreatedBy(random());
    entity.setUpdatedBy(random());
    entity.setCreatedAt(LocalDateTime.now());
    entity.setUpdatedAt(LocalDateTime.now());

    ApikeyManagementResponse response = this.mapper.toManagementResponse(entity);

    assertEquals(entity.getId(), response.id());
    assertEquals(entity.getCompanyId(), response.companyId());
    assertEquals(entity.getName(), response.name());
    assertEquals(entity.getKey(), response.key());
    assertEquals(entity.getCreatedBy(), response.createdBy());
    assertEquals(entity.getUpdatedBy(), response.updatedBy());
    assertEquals(entity.getCreatedAt(), response.createdAt());
    assertEquals(entity.getUpdatedAt(), response.updatedAt());
  }
}
