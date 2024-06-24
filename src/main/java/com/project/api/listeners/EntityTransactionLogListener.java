package com.project.api.listeners;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/*
 * Listener class for logging entity transaction events asynchronously.
 * ex: [created] company 16
 *
 */
@Slf4j
@Component
public class EntityTransactionLogListener {

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onCommitEvent(final TransactionEvent event) {
    log.info("[{}] {} {}", event.operation().getName(), event.entityName, event.entityId());
  }

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
  public void onRollbackEvent(final TransactionEvent event) {
    log.info(
        "[{}] {} {} rollback.", event.operation().getName(), event.entityName, event.entityId());
  }

  public record TransactionEvent(TransactionType operation, String entityName, String entityId) {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum TransactionType {
      CREATE("created"),
      UPDATE("updated"),
      DELETE("deleted");

      private final String name;
    }
  }
}
