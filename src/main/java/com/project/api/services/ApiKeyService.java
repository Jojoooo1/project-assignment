package com.project.api.services;

import static com.project.api.utils.CryptoUtils.createSecureRandom;
import static java.lang.String.format;

import com.project.api.entities.ApiKey;
import com.project.api.exceptions.types.ResourceNotFoundException;
import com.project.api.repositories.ApikeyRepository;
import com.project.api.services.base.BaseService;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true) // works only on public methods
@Service
@RequiredArgsConstructor
public class ApiKeyService extends BaseService<ApiKey, Long> {

  @Getter private final ApikeyRepository repository;

  public Optional<ApiKey> findByKeyAndIsActiveOptional(final String key) {
    log.debug("[retrieving] apiKey");
    return this.repository.findByKeyAndIsActive(key, true);
  }

  @Override
  @Transactional
  public ApiKey create(final ApiKey entity) {
    log.info("[creating] {} {}", this.getEntityName(), entity);

    entity.setIsActive(true);
    entity.setKey(createSecureRandom(ApiKey.KEY_DEFAULT_SIZE));
    this.repository.save(entity);

    this.publishTxCreateLog(entity.getId());
    return entity;
  }

  @Override
  @Transactional
  public void delete(final Long id) {
    log.info("[inactivating] {} with id '{}'", this.getEntityName(), id);
    final ApiKey entity =
        this.repository
            .findById(id)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        format("%s '%s' not found", this.getEntityName(), id)));
    entity.setIsActive(false);
    this.repository.save(entity);
    this.publishTxUpdateLog(id);
  }
}
