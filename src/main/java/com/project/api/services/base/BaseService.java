package com.project.api.services.base;

import com.project.api.entities.base.BaseEntity;
import com.project.api.exceptions.types.ResourceNotFoundException;
import com.project.api.listeners.EntityTransactionLogListener.TransactionEvent;
import com.project.api.listeners.EntityTransactionLogListener.TransactionEvent.TransactionType;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class BaseService<E extends BaseEntity<ID>, ID extends Serializable> {

  @Autowired private ApplicationEventPublisher applicationEventPublisher;

  public abstract JpaRepository<E, ID> getRepository();

  @Transactional(readOnly = true)
  public E findById(final ID id) {
    log.debug("[retrieving] {} {}", this.getEntityName(), id);
    return this.getRepository().findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
  }

  @Transactional(readOnly = true)
  public Optional<E> findByIdOptional(final ID id) {
    log.debug("[retrieving] {} {}", this.getEntityName(), id);
    return this.getRepository().findById(id);
  }

  @Transactional(readOnly = true)
  public Page<E> findAll(final Pageable pageable) {
    log.debug("[retrieving] all {}", this.getEntityName());
    return this.getRepository().findAll(pageable);
  }

  /*
   * If you need more flexibility you can create a beforeSaveFunction/AfterSaveFunction
   * */
  @Transactional
  public E create(final E entity) {
    log.info("[creating] {} {}", this.getEntityName(), entity);
    if (entity.getId() != null) {
      throw new IllegalStateException("You are trying to create an entity that already exist.");
    }
    this.getRepository().save(entity);
    this.publishTxCreateLog(entity.getId());
    return entity;
  }

  /*
   * If you need for more flexibility you can create a beforeSaveFunction/AfterSaveFunction
   * */
  @Transactional
  public E update(final E entity) {
    log.info("[updating] {} {}", this.getEntityName(), entity);
    if (entity.getId() == null) {
      throw new IllegalStateException("You are trying to update an entity that does not exist.");
    }
    this.getRepository().save(entity);
    this.publishTxUpdateLog(entity.getId());
    return entity;
  }

  /*
   * If you need more flexibility you can create a beforeSaveFunction/AfterSaveFunction
   * */
  @Transactional
  public void delete(final ID id) {
    log.info("[deleting] {} {}", this.getEntityName(), id);
    E entity =
        this.getRepository().findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
    this.getRepository().delete(entity);
    this.publishTxDeleteLog(id);
  }

  /*
   * Can be improved by creating a @publishTxLogEvent annotations.
   * */
  protected void publishTxLogEvent(TransactionType type, ID entityId) {
    this.applicationEventPublisher.publishEvent(
        new TransactionEvent(type, this.getEntityName(), entityId.toString()));
  }

  protected void publishTxCreateLog(ID entityId) {
    this.publishTxLogEvent(TransactionType.CREATE, entityId);
  }

  protected void publishTxUpdateLog(ID entityId) {
    this.publishTxLogEvent(TransactionType.UPDATE, entityId);
  }

  protected void publishTxDeleteLog(ID entityId) {
    this.publishTxLogEvent(TransactionType.DELETE, entityId);
  }

  protected String getEntityName() {
    final Class<E> entityModelClass =
        (Class<E>)
            ((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    final Table annotation = entityModelClass.getAnnotation(Table.class);
    return annotation.name();
  }
}
