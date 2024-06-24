package com.project.api.repositories;

import com.project.api.entities.Company;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface CompanyRepository extends JpaRepository<Company, Long> {

  String CACHE_NAME = "company";

  @NonNull
  @Cacheable(value = CACHE_NAME, key = "{'byId', #id}")
  @Override
  Optional<Company> findById(@NonNull Long id);

  @Caching(
      evict = {
        @CacheEvict(value = CACHE_NAME, key = "{'byId', #entity.id}"),
        @CacheEvict(value = CACHE_NAME, key = "{'bySlug', #entity.slug}"),
      })
  @Override
  <S extends Company> @NonNull S save(@NonNull S entity);

  @Caching(
      evict = {
        @CacheEvict(value = CACHE_NAME, key = "{'byId', #entity.id}"),
        @CacheEvict(value = CACHE_NAME, key = "{'bySlug', #entity.slug}"),
      })
  @Override
  void delete(@NonNull Company entity);
}
