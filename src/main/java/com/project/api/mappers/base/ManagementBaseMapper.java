package com.project.api.mappers.base;

import com.project.api.mappers.annotations.ToEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * The interface Base mapper management.
 *
 * @param <E> the type parameter Entity
 * @param <C> the type parameter CreateRequest
 * @param <U> the type parameter UpdateRequest
 * @param <P> the type parameter PatchRequest
 * @param <R> the type parameter Response
 */
public interface ManagementBaseMapper<E, C, U, P, R> {

  @ToEntity
  E toEntity(C request);

  @ToEntity
  E updateWithManagementRequest(U request, @MappingTarget E entity);

  @ToEntity
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  E patchWithManagementRequest(P request, @MappingTarget E entity);

  R toManagementResponse(E entity);
}
