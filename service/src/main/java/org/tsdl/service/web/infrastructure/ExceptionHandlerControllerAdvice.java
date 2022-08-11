package org.tsdl.service.web.infrastructure;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.tsdl.implementation.evaluation.TsdlEvaluationException;
import org.tsdl.service.web.infrastructure.ExceptionHandlerControllerAdvice.ValidationErrorsHolder.ValidationError;
import org.tsdl.storage.TsdlStorageException;

/**
 * Advice for REST controllers that registers exception handlers for when a validation performed by Spring or Hibernate fails or when query evaluation
 * fails. More specifically, this type registers handlers for when an {@link MethodArgumentNotValidException}, {@link ConstraintViolationException},
 * or {@link TsdlEvaluationException} is propagated beyond the scope of a controller. It transforms the validation error data (e.g. what fields caused
 * the violation for which reason(s)) into a {@link ValidationErrorsHolder} object which is then sent as response body to the causing request,
 * with status code {@link HttpStatus#BAD_REQUEST}. In the case of a query evaluation error, a Spring error dictionary created by
 * {@link ExtendedControllerErrorCollector#getErrorAttributes(WebRequest, ErrorAttributeOptions)} containing a compact trace of errors is returned.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {
  private final MessageSource messageSource;
  private final ExtendedControllerErrorCollector errorCollector;

  @Autowired
  public ExceptionHandlerControllerAdvice(MessageSource messageSource, ExtendedControllerErrorCollector errorCollector) {
    this.messageSource = messageSource;
    this.errorCollector = errorCollector;
  }

  /**
   * Validation errors with a {@link Valid} annotation as root cause result in {@link MethodArgumentNotValidException} objects.
   *
   * @param ex      information about the validation failure
   * @param request the request that triggered the validation advice
   * @return a {@link ValidationErrorsHolder} instance representing information about the validation failure that are relevant to the requester
   */
  @NotNull
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException ex, @NotNull HttpHeaders headers,
                                                                @NotNull HttpStatus status, @NotNull WebRequest request) {
    var errorHolder = buildValidationFailureResponse(() -> ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> {
              var errorMessage = messageSource.getMessage(fieldError, Locale.ROOT);
              return new ValidationError(
                  fieldError.getObjectName(),
                  fieldError.getField(),
                  fieldError.getRejectedValue(),
                  !StringUtils.hasText(errorMessage) ? fieldError.getDefaultMessage() : errorMessage);
            })
            .toList(),
        getPath(request));

    return handleExceptionInternal(ex, errorHolder, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Validation errors with a {@link Validated} annotation or a JPA provider (e.g. Hibernate) as root cause result in
   * {@link ConstraintViolationException} objects.
   *
   * @param ex      information about the validation failure
   * @param request the request that triggered the validation advice
   * @return a {@link ValidationErrorsHolder} instance representing information about the validation failure that are relevant to the requester
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(ConstraintViolationException.class)
  public ValidationErrorsHolder constraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
    return buildValidationFailureResponse(() -> ex.getConstraintViolations().stream()
            .map(violation -> new ValidationError(
                violation.getRootBeanClass().getSimpleName(),
                violation.getPropertyPath().toString(),
                violation.getInvalidValue(),
                violation.getMessage()))
            .toList(),
        request.getRequestURI());
  }

  /**
   * Transforms a {@link TsdlEvaluationException} into an error map to be returned by Spring.
   *
   * @param request the request which resulted in a query evaluation error
   * @return a map containing information about the error
   */
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  @ExceptionHandler({TsdlEvaluationException.class, TsdlStorageException.class})
  public Map<String, Object> evaluationException(WebRequest request) {
    var errorResponse = errorCollector.getErrorAttributes(request, ErrorAttributeOptions.defaults());
    errorResponse.put("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    errorResponse.putIfAbsent("path", getPath(request));
    return errorResponse;
  }

  /**
   * Handles a validation error by providing a {@link ValidationErrorsHolder} object with message and timestamp. The error collection is delegated
   * to the {@code errorCollector} parameter.
   *
   * @param errorCollector a supplier that returns a collection of {@link ValidationError} instances which constitute the errors represented by
   *                       the {@link ValidationErrorsHolder} instance being created
   * @param path           endpoint method path from web application's root name
   * @return a {@link ValidationErrorsHolder} with the current timestamp, a message indicating a validation error and the errors provided
   *     by {@code errorCollector}
   */
  private ValidationErrorsHolder buildValidationFailureResponse(Supplier<Collection<ValidationError>> errorCollector, String path) {
    var errors = new ValidationErrorsHolder(Instant.now().atZone(ZoneOffset.UTC), path);
    errors.setErrors(errorCollector.get());

    log.error("Input validation failed with %d errors: %s".formatted(errors.getValidationErrors().size(), errors));
    return errors;
  }

  private String getPath(WebRequest webRequest) {
    return webRequest instanceof ServletWebRequest servletWebRequest
        ? servletWebRequest.getRequest().getRequestURI()
        : "<unknown>";
  }

  @Data
  @Schema(description = "Bundles one or more validation errors.")
  static class ValidationErrorsHolder {
    @Schema(description = "Endpoint method path from web application's root name.", example = "/query")
    private final String path;

    @Schema(description = "Number of the HTTP status code induced by the validation errors.", example = "400")
    private final int status;

    @Schema(description = "Phrase describing the HTTP status code induced by the validation errors.", example = "Bad request")
    private final String error;

    @Schema(description = "Compact summary of validation errors.",
        example = "There were validation errors: [durationMinutes: must be greater than 0 (value='0')]")
    private String message;

    @Schema(description = "Timezone-aware date and time of the response informing about the validation errors.",
        example = "2021-11-12T22:08:58.1590085+01:00")
    private final ZonedDateTime timestamp;

    @Schema(description = "Detailed representation of encountered validation errors.")
    private final List<ValidationError> validationErrors;

    ValidationErrorsHolder(ZonedDateTime timestamp, String path) {
      this.timestamp = timestamp;
      this.path = path;
      this.status = HttpStatus.BAD_REQUEST.value();
      this.error = HttpStatus.BAD_REQUEST.getReasonPhrase();
      this.validationErrors = new ArrayList<>();
    }

    public void setErrors(Collection<ValidationError> errors) {
      validationErrors.clear();
      validationErrors.addAll(errors);
      message = "There were validation errors: " + getValidationErrorsRepresentation();
    }

    @Override
    public String toString() {
      return "{%s}".formatted(getValidationErrorsRepresentation());
    }

    private String getValidationErrorsRepresentation() {
      var errorRepresentation = new StringBuilder();
      for (var fieldError : this.validationErrors) {
        errorRepresentation.append("[");
        errorRepresentation.append(fieldError.propertyPath()).append(": ").append(fieldError.message());
        errorRepresentation.append(" (value='").append(fieldError.invalidValue()).append("')");
        errorRepresentation.append("], ");
      }
      var errorString = errorRepresentation.toString();
      return errorString.substring(0, errorString.length() - 2);
    }

    @Schema(description = "Encapsulates information about a validation error.")
    record ValidationError(
        @Schema(description = "Representation of the object causing the validation error.", example = "storageDto")
        String rootBean,
        @Schema(description = "Location of the violating property in the member hierarchy of rootBean.", example = "name")
        String propertyPath,
        @Schema(description = "Property value that caused the validation error.", example = "0")
        Object invalidValue,
        @Schema(description = "Description of the validation error.", example = "must be greater than 0")
        String message
    ) {
    }
  }
}

