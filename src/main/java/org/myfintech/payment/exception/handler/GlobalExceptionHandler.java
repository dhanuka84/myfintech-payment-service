package org.myfintech.payment.exception.handler;

import java.util.Map;

import org.myfintech.payment.exception.FileProcessingException;
import org.myfintech.payment.exception.Http400BadRequest;
import org.myfintech.payment.exception.Http404NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Http404NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(Http404NotFoundException ex) {
        log.warn("Http404NotFoundException caught: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    @ExceptionHandler(Http400BadRequest.class)
    public ResponseEntity<ProblemDetail> handleCustomBadRequest(Http400BadRequest ex) {
        log.warn("Http400BadRequest caught: {}", ex.getMessage(), ex);
        ProblemDetail detail = createProblemDetail(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
        if (!ex.getErrors().isEmpty()) {
            detail.setProperty("errors", ex.getErrors());
        }
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException caught: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_REQUEST, "Illegal argument", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Parameter '" + ex.getName() + "' must be of type " +
                (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        log.warn("MethodArgumentTypeMismatchException: {}", message, ex);
        return buildResponse(HttpStatus.BAD_REQUEST, "Type mismatch", message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("ConstraintViolationException caught: {}", ex.getMessage(), ex);
        ProblemDetail detail = createProblemDetail(HttpStatus.BAD_REQUEST, "Constraint violation", "Validation failed");
        detail.setProperty("errors", ex.getConstraintViolations().stream()
                .map(cv -> Map.of("path", cv.getPropertyPath().toString(), "message", cv.getMessage()))
                .toList());
        return ResponseEntity.badRequest().body(detail);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatusCode status,
        WebRequest request) {

        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Validation failed");
        detail.setDetail("One or more fields are invalid");
        detail.setProperty("errors", ex.getBindingResult().getFieldErrors().stream()
            .map(e -> Map.of("field", e.getField(), "message", e.getDefaultMessage()))
            .toList());

        return new ResponseEntity<>(detail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String causeMsg = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        log.error("DataIntegrityViolationException caught: {}", causeMsg, ex);
        return buildResponse(HttpStatus.CONFLICT, "Database constraint error", causeMsg);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLocking(OptimisticLockingFailureException ex) {
        log.warn("OptimisticLockingFailureException caught: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.CONFLICT, "Concurrency conflict", "Concurrent modification detected");
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ProblemDetail> handleFileProcessing(FileProcessingException ex) {
        log.warn("FileProcessingException caught: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_REQUEST, "File processing error", ex.getMessage());
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ProblemDetail> handleTransactionError(TransactionSystemException ex) {
        log.error("TransactionSystemException caught: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Transaction error", "Transaction failed");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleGenericRuntimeException(RuntimeException ex) {
        log.error("Unhandled RuntimeException caught: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected server error");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(Exception ex) {
        log.error("Unhandled Exception caught: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected server error");
    }

    // ------------------ Utility Methods ------------------ //

    private ResponseEntity<ProblemDetail> buildResponse(HttpStatus status, String title, String detailMessage) {
        return ResponseEntity.status(status).body(createProblemDetail(status, title, detailMessage));
    }

    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detailMessage) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, detailMessage);
        detail.setTitle(title);
        return detail;
    }
}
