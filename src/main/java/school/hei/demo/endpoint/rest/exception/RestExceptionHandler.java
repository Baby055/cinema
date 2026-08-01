package school.hei.demo.endpoint.rest.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.hei.demo.PojaGenerated;

@PojaGenerated
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {
  public record ApiError(Instant timestamp, int status, String error, String message) {}

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException e) {
    return toResponse(HttpStatus.FORBIDDEN, e.getMessage());
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiError> handleApiException(ApiException e) {
    return toResponse(e.getStatus(), e.getMessage());
  }

  @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
  public ResponseEntity<ApiError> handleValidation(Exception e) {
    return toResponse(HttpStatus.BAD_REQUEST, e.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnexpected(Exception e) {
    log.error("Unexpected error", e);
    return toResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
  }

  private ResponseEntity<ApiError> toResponse(HttpStatus status, String message) {
    return ResponseEntity.status(status)
        .body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message));
  }
}
