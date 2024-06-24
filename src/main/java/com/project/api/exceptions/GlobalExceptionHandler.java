package com.project.api.exceptions;

import static com.project.api.constants.AppConstants.API_DEFAULT_ERROR_MESSAGE;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.project.api.clients.slack.SlackAlertClient;
import com.project.api.exceptions.types.RootException;
import com.project.api.responses.shared.ApiErrorDetails;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.LazyInitializationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.postgresql.util.PSQLException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * GlobalExceptionHandler handles exceptions thrown across the whole application in a centralized
 * manner. All the controllers will use this global exception handler to manage exceptions in a
 * consistent way as per RFC 9457.
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final SlackAlertClient slack;

  /**
   * Annotation: @Valid
   *
   * <p>This method is invoked when a method argument validation fails. It processes the exception,
   * logs the error message, extracts details of the validation errors, and returns a structured
   * {@link ResponseEntity} containing a {@link ProblemDetail} response.
   *
   * @param ex the {@link MethodArgumentNotValidException}.
   * @param headers the HTTP headers of the request.
   * @param status the HTTP status code.
   * @param request the current web request.
   * @return a {@link ResponseEntity}.
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      @NonNull final MethodArgumentNotValidException ex,
      @NonNull final HttpHeaders headers,
      @NonNull final HttpStatusCode status,
      @NonNull final WebRequest request) {
    log.info(ex.getMessage(), ex);

    final List<ApiErrorDetails> errors = new ArrayList<>();

    for (final ObjectError err : ex.getBindingResult().getAllErrors()) {
      errors.add(
          ApiErrorDetails.builder()
              .pointer(((FieldError) err).getField())
              .reason(err.getDefaultMessage())
              .build());
    }

    return ResponseEntity.status(BAD_REQUEST)
        .body(this.buildProblemDetail(BAD_REQUEST, "Validation failed.", errors));
  }

  /**
   * Annotation: @RequestParam, @PathVariable
   *
   * <p>This method is invoked when a controller method validation fails. It processes the
   * exception, logs the error message, extracts details of the validation errors, and returns a
   * structured {@link ResponseEntity} containing a {@link ProblemDetail} response.
   *
   * @param ex the {@link HandlerMethodValidationException}.
   * @param headers the HTTP headers of the request.
   * @param status the HTTP status code.
   * @param request the current web request.
   * @return a {@link ResponseEntity}.
   */
  @Override
  protected ResponseEntity<Object> handleHandlerMethodValidationException(
      final @NonNull HandlerMethodValidationException ex,
      final @NonNull HttpHeaders headers,
      final @NonNull HttpStatusCode status,
      final @NonNull WebRequest request) {
    log.info(ex.getMessage(), ex);

    final List<ApiErrorDetails> errors = new ArrayList<>();
    for (final var validation : ex.getAllValidationResults()) {
      final String parameterName = validation.getMethodParameter().getParameterName();
      validation
          .getResolvableErrors()
          .forEach(
              error ->
                  errors.add(
                      ApiErrorDetails.builder()
                          .pointer(parameterName)
                          .reason(error.getDefaultMessage())
                          .build()));
    }

    return ResponseEntity.status(BAD_REQUEST)
        .body(this.buildProblemDetail(BAD_REQUEST, "Validation failed.", errors));
  }

  /**
   * Annotation: @Validated
   *
   * <p>This method is invoked when a validation constraint is violated. It processes the exception,
   * logs the error message, extracts details of the constraint violations, and returns a structured
   * {@link ProblemDetail} response.
   *
   * @param ex the {@link jakarta.validation.ConstraintViolationException}.
   * @return a {@link ProblemDetail}.
   */
  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
  public ProblemDetail handleJakartaConstraintViolationException(
      final jakarta.validation.ConstraintViolationException ex) {
    log.info(ex.getMessage(), ex);

    final List<ApiErrorDetails> errors = new ArrayList<>();
    for (final var violation : ex.getConstraintViolations()) {
      errors.add(
          ApiErrorDetails.builder()
              .pointer(((PathImpl) violation.getPropertyPath()).getLeafNode().getName())
              .reason(violation.getMessage())
              .build());
    }

    return this.buildProblemDetail(BAD_REQUEST, "Validation failed.", errors);
  }

  /**
   * This method is invoked when a persistence exception occurs. It processes the exception, logs
   * the error message, extracts the most specific cause of the exception, and returns a structured
   * {@link ProblemDetail} response.
   *
   * @param ex the exception thrown during persistence operations. Can be one of {@link
   *     org.hibernate.exception.ConstraintViolationException}, {@link
   *     DataIntegrityViolationException}, {@link BatchUpdateException}, {@link
   *     jakarta.persistence.PersistenceException}, or {@link PSQLException}.
   * @return a {@link ProblemDetail}.
   */
  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler({
    org.hibernate.exception.ConstraintViolationException.class,
    DataIntegrityViolationException.class,
    BatchUpdateException.class,
    jakarta.persistence.PersistenceException.class,
    PSQLException.class
  })
  public ProblemDetail handlePersistenceException(final Exception ex) {
    log.info(ex.getMessage(), ex);
    final String cause = NestedExceptionUtils.getMostSpecificCause(ex).getLocalizedMessage();
    final String errorDetail = this.extractPersistenceDetails(cause);
    return this.buildProblemDetail(BAD_REQUEST, errorDetail);
  }

  /**
   * Annotation: @PreAuthorize, @PostAuthorize
   *
   * <p>This method is invoked when access is denied to a resource. It processes the exception, logs
   * the error message, and returns a structured {@link ProblemDetail} response.
   *
   * @param ex the {@link AccessDeniedException}.
   * @return a {@link ProblemDetail}.
   */
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(AccessDeniedException.class)
  public ProblemDetail handleAccessDeniedException(final Exception ex) {
    log.info(ex.getMessage(), ex);
    return this.buildProblemDetail(HttpStatus.FORBIDDEN, null);
  }

  /**
   * This method is invoked when a {@link LazyInitializationException} occurs, typically due to
   * attempting to access a lazily initialized Entity outside an active Hibernate session. It
   * processes the exception, logs a warning message, optionally notifies via Slack, and returns a
   * structured {@link ProblemDetail} response.
   *
   * @param ex the {@link LazyInitializationException} thrown when lazy initialization fails.
   * @return a {@link ProblemDetail} object containing the HTTP status and a default error message.
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(LazyInitializationException.class)
  public ProblemDetail handleLazyInitialization(final LazyInitializationException ex) {
    log.warn(ex.getMessage(), ex);
    this.slack.notify(format("LazyInitializationException: %s", ex.getMessage()));
    return this.buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, API_DEFAULT_ERROR_MESSAGE);
  }

  /**
   * This method is invoked when a {@link RootException} occurs. It processes the exception, logs
   * the error message, optionally notifies via Slack if the status is a server error, and returns a
   * structured {@link ProblemDetail} response.
   *
   * @param ex the {@link RootException}.
   * @return a {@link ResponseEntity}.
   */
  @ExceptionHandler(RootException.class)
  public ResponseEntity<ProblemDetail> rootException(final RootException ex) {
    log.info(ex.getMessage(), ex);

    if (ex.getHttpStatus().is5xxServerError()) {
      this.slack.notify(format("[API] InternalServerError: %s", ex.getMessage()));
    }

    // message can be removed if necessary for security purpose.
    final ProblemDetail problemDetail =
        this.buildProblemDetail(ex.getHttpStatus(), ex.getMessage(), ex.getErrors());
    return ResponseEntity.status(ex.getHttpStatus()).body(problemDetail);
  }

  /**
   * This method is a fallback handler for all exceptions that are not specifically handled by other
   * methods. It processes the exception, logs a warning message, optionally notifies via Slack, and
   * returns a structured {@link ProblemDetail} response.
   *
   * @param ex the {@link Throwable}.
   * @return a {@link ProblemDetail}.
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Throwable.class)
  public ProblemDetail handleAllExceptions(final Throwable ex) {
    log.warn(format("%s", ex.getMessage()), ex);
    this.slack.notify(format("[API] InternalServerError: %s", ex.getMessage()));
    return this.buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, API_DEFAULT_ERROR_MESSAGE);
  }

  private ProblemDetail buildProblemDetail(final HttpStatus status, final String detail) {
    return this.buildProblemDetail(status, detail, emptyList());
  }

  private ProblemDetail buildProblemDetail(
      final HttpStatus status, final String detail, final List<ApiErrorDetails> errors) {

    final ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(status, StringUtils.normalizeSpace(detail));
    if (CollectionUtils.isNotEmpty(errors)) {
      problemDetail.setProperty("errors", errors);
    }

    return problemDetail;
  }

  /*
   * Extract PSQL persistence error message:
   * ex: duplicate key value violates unique constraint "company_slug_key"  Detail: Key (slug)= (bl8lo0d) already exists.
   * */
  private String extractPersistenceDetails(final String cause) {

    String details = API_DEFAULT_ERROR_MESSAGE;

    final List<String> matchList = new ArrayList<>();
    // find KEY values between "()"
    final Pattern pattern = Pattern.compile("\\((.*?)\\)");
    final Matcher matcher = pattern.matcher(cause);

    // Creates list ["slug", "bl8lo0d"]
    while (matcher.find()) {
      matchList.add(matcher.group(1));
    }

    if (matchList.size() == 2) {
      final String key = matchList.get(0);
      final String value = matchList.get(1);
      // Gets the message after the last ")"
      final String message = cause.substring(cause.lastIndexOf(")") + 1);

      // return errorMessage: slug 'bl8lo0d'  already exists.
      details = format("%s '%s' %s", key, value, message);
    }

    return details;
  }
}
