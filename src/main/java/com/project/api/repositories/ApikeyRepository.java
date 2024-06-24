package com.project.api.repositories;

import com.project.api.entities.ApiKey;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface ApikeyRepository extends JpaRepository<ApiKey, Long> {

  String CACHE_NAME = "apiKey";

  @Cacheable(value = CACHE_NAME, key = "{'findByKeyAndIsActive', #key}")
  Optional<ApiKey> findByKeyAndIsActive(String key, boolean isActive);

  @Caching(evict = {@CacheEvict(value = CACHE_NAME, key = "{'findByKeyAndIsActive', #entity.key}")})
  @Override
  <S extends ApiKey> @NonNull S save(@NonNull S entity);
}
