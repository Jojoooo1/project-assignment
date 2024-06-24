package com.project.api.controllers.management.base;

import com.project.api.entities.base.BaseEntity;
import com.project.api.mappers.base.BaseManagementMapper;
import com.project.api.responses.shared.ApiListPaginationSuccess;
import com.project.api.services.base.BaseService;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The type Base management controller.
 *
 * @param <E> the type parameter Entity
 * @param <I> the type parameter ID
 * @param <C> the type parameter CreateRequest
 * @param <U> the type parameter UpdateRequest
 * @param <P> the type parameter PatchRequest
 * @param <R> the type parameter Response
 */
@Slf4j
public abstract class BaseManagementController<
    E extends BaseEntity<I>, I extends Serializable, C, U, P, R> {

  public abstract BaseManagementMapper<E, C, U, P, R> getMapper();

  public abstract BaseService<E, I> getService();

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public R findById(@PathVariable("id") final I id) {
    log.debug("[request] retrieve {} with id {}", this.getName(), id);
    final E entity = this.getService().findById(id);
    return this.getMapper().toManagementResponse(entity);
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiListPaginationSuccess<R> findAll(final Pageable pageable) {
    log.debug("[request] retrieve all {}", this.getName());
    final Page<E> entities = this.getService().findAll(pageable);
    final Page<R> response = entities.map(this.getMapper()::toManagementResponse);
    return ApiListPaginationSuccess.of(response);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public R create(@Valid @RequestBody final C request) {
    log.info("[request] create {}", request);
    final E entity = this.getService().create(this.getMapper().toEntity(request));
    return this.getMapper().toManagementResponse(entity);
  }

  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public R update(@PathVariable("id") final I id, @Valid @RequestBody final U request) {
    log.info("[request] update {} {}", id, request);

    final E original = this.getService().findById(id);
    final E merged = this.getMapper().updateWithManagementRequest(request, original);
    final E entity = this.getService().update(merged);

    return this.getMapper().toManagementResponse(entity);
  }

  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}")
  public R patch(@PathVariable("id") final I id, @Valid @RequestBody final P request) {
    log.info("[request] patch {} {}", id, request);

    final E original = this.getService().findById(id);
    final E patched = this.getMapper().patchWithManagementRequest(request, original);
    final E entity = this.getService().update(patched);

    return this.getMapper().toManagementResponse(entity);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void delete(@PathVariable("id") final I id) {
    log.info("[request] delete {} {}", this.getName(), id);
    this.getService().delete(id);
  }

  protected String getName() {
    final Class<E> entityModelClass =
        (Class<E>)
            ((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    final Table annotation = entityModelClass.getAnnotation(Table.class);
    return annotation.name();
  }
}
