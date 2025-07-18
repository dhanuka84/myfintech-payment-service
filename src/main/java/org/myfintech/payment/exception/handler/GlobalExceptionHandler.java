package org.myfintech.payment.exception.handler;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.myfintech.payment.exception.FileParsingException;
import org.myfintech.payment.exception.FileProcessingException;
import org.myfintech.payment.exception.Http400BadRequest;
import org.myfintech.payment.exception.Http404NotFoundException;
import org.springframework.beans.factory.annotation.Value;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the payment service.
 * Handles exceptions with appropriate logging levels and user-friendly responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR_ID_KEY = "errorId";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String PATH_KEY = "path";
    private static final String ERROR_CODE_KEY = "errorCode";
    
    @Value("${myfintech.error.include-stacktrace:false}")
    private boolean includeStackTrace;
    
    @Value("${myfintech.error.base-uri:https://myfintech.com/errors/}")
    private String errorBaseUri;

    // ================== Business/Domain Exceptions (LOG: WARN) ================== //
    
    @ExceptionHandler(Http404NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(Http404NotFoundException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.warn("Resource not found [{}]: {} - Path: {}", errorId, ex.getMessage(), request.getRequestURI());
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.NOT_FOUND, 
            "Resource Not Found", 
            ex.getMessage(),
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "RESOURCE_NOT_FOUND");
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    @ExceptionHandler(Http400BadRequest.class)
    public ResponseEntity<ProblemDetail> handleCustomBadRequest(Http400BadRequest ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.warn("Bad request [{}]: {} - Errors: {}", errorId, ex.getMessage(), ex.getErrors());
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST, 
            "Bad Request", 
            ex.getMessage(),
            errorId,
            request
        );
        
        if (!ex.getErrors().isEmpty()) {
            detail.setProperty("validationErrors", ex.getErrors());
        }
        detail.setProperty(ERROR_CODE_KEY, "BAD_REQUEST");
        
        return ResponseEntity.badRequest().body(detail);
    }

    // ================== Validation Exceptions (LOG: INFO) ================== //
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        // INFO level - this is a client error, not a system issue
        log.info("Invalid argument [{}]: {}", errorId, ex.getMessage());
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST, 
            "Invalid Argument", 
            ex.getMessage(),
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "INVALID_ARGUMENT");
        
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        String message = String.format("Parameter '%s' must be of type %s", 
            ex.getName(), 
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        
        // INFO level - client provided wrong type
        log.info("Type mismatch [{}]: {}", errorId, message);
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST, 
            "Type Mismatch", 
            message,
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "TYPE_MISMATCH");
        detail.setProperty("parameter", ex.getName());
        detail.setProperty("expectedType", ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        detail.setProperty("actualValue", ex.getValue());
        
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        // INFO level - validation errors are client issues
        log.info("Constraint violations [{}]: {}", errorId, ex.getConstraintViolations().size());
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST, 
            "Validation Failed", 
            "One or more validation constraints were violated",
            errorId,
            request
        );
        
        detail.setProperty("validationErrors", ex.getConstraintViolations().stream()
            .map(cv -> Map.of(
                "field", cv.getPropertyPath().toString(), 
                "message", cv.getMessage(),
                "invalidValue", cv.getInvalidValue() != null ? cv.getInvalidValue().toString() : "null"
            ))
            .collect(Collectors.toList()));
        detail.setProperty(ERROR_CODE_KEY, "VALIDATION_FAILED");
        
        return ResponseEntity.badRequest().body(detail);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        String errorId = generateErrorId();
        // INFO level - validation errors
        log.info("Method argument validation failed [{}]: {} errors", 
            errorId, ex.getBindingResult().getErrorCount());

        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Validation Failed");
        detail.setDetail("Request validation failed. Please check the errors and try again.");
        detail.setType(URI.create(errorBaseUri + "validation-failed"));
        detail.setProperty(ERROR_ID_KEY, errorId);
        detail.setProperty(TIMESTAMP_KEY, Instant.now());
        detail.setProperty(ERROR_CODE_KEY, "VALIDATION_FAILED");
        
        Map<String, Object> validationErrors = new HashMap<>();
        
        // Field errors
        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            validationErrors.put("fieldErrors", ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of(
                    "field", e.getField(),
                    "message", e.getDefaultMessage() != null ? e.getDefaultMessage() : "Invalid value",
                    "rejectedValue", e.getRejectedValue() != null ? e.getRejectedValue().toString() : "null"
                ))
                .collect(Collectors.toList()));
        }
        
        // Global errors
        if (!ex.getBindingResult().getGlobalErrors().isEmpty()) {
            validationErrors.put("globalErrors", ex.getBindingResult().getGlobalErrors().stream()
                .map(e -> Map.of(
                    "object", e.getObjectName(),
                    "message", e.getDefaultMessage() != null ? e.getDefaultMessage() : "Invalid object"
                ))
                .collect(Collectors.toList()));
        }
        
        detail.setProperty("validationErrors", validationErrors);

        return new ResponseEntity<>(detail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ProblemDetail> handleFileProcessing(FileProcessingException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        // WARN level - file processing might be a business logic issue
        log.warn("File processing error [{}]: {} - File: {}", 
            errorId, ex.getMessage(), ex.getFileName() != null ? ex.getFileName() : "unknown");
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST, 
            "File Processing Error", 
            ex.getMessage(),
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "FILE_PROCESSING_ERROR");
        if (ex.getFileName() != null) {
            detail.setProperty("fileName", ex.getFileName());
        }
        
        return ResponseEntity.badRequest().body(detail);
    }
    
    @ExceptionHandler(FileParsingException.class)
    public ResponseEntity<ProblemDetail> handleFileParsing(FileParsingException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.warn("File parsing error [{}]: {}", errorId, ex.getMessage());

        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            "File Parsing Error",
            ex.getMessage(),
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "FILE_PARSING_ERROR");

        return ResponseEntity.badRequest().body(detail);
    }


    // ================== Data/Infrastructure Exceptions (LOG: ERROR) ================== //
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        String rootCause = extractRootCauseMessage(ex);
        
        // Log at ERROR level with full details for debugging
        log.error("Data integrity violation [{}]: {} - Root cause: {}", errorId, ex.getMessage(), rootCause, ex);
        
        // Don't expose internal database details to client
        String userMessage = "Unable to process request due to data constraints";
        
        // Check for common constraint violations
        if (rootCause.toLowerCase().contains("duplicate")) {
            userMessage = "A record with the same information already exists";
        } else if (rootCause.toLowerCase().contains("foreign key")) {
            userMessage = "Referenced data not found or cannot be deleted due to existing dependencies";
        }
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.CONFLICT, 
            "Data Constraint Violation", 
            userMessage,
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "DATA_INTEGRITY_VIOLATION");
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(detail);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLocking(OptimisticLockingFailureException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        // WARN level - this is expected in concurrent systems
        log.warn("Optimistic locking conflict [{}]: {} - Entity: {}", 
            errorId, ex.getMessage(), extractEntityInfo(ex));
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.CONFLICT, 
            "Concurrent Update Conflict", 
            "The record was modified by another user. Please refresh and try again.",
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "OPTIMISTIC_LOCK_CONFLICT");
        detail.setProperty("suggestion", "Refresh the data and retry your changes");
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(detail);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ProblemDetail> handleTransactionError(TransactionSystemException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        String rootCause = extractRootCauseMessage(ex);
        
        // ERROR level - transaction failures need investigation
        log.error("Transaction system error [{}]: {} - Root cause: {}", errorId, ex.getMessage(), rootCause, ex);
        
        // Check if it's actually a validation error wrapped in transaction exception
        if (ex.getRootCause() instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) ex.getRootCause(), request);
        }
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "Transaction Failed", 
            "Unable to complete the transaction. Please try again.",
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "TRANSACTION_ERROR");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    // ================== Programming Errors (LOG: ERROR + ALERT) ================== //
    
    @ExceptionHandler({
        NullPointerException.class,
        IndexOutOfBoundsException.class,
        ClassCastException.class,
        IllegalStateException.class
    })
    public ResponseEntity<ProblemDetail> handleProgrammingError(RuntimeException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        
        // CRITICAL ERROR - This is a bug!
        log.error("CRITICAL: Programming error detected [{}] - THIS IS A BUG! Exception: {} at {}", 
            errorId, 
            ex.getClass().getSimpleName(), 
            ex.getStackTrace().length > 0 ? ex.getStackTrace()[0] : "unknown location", 
            ex);
        
        // TODO: Add alerting service call here
        // alertingService.notifyCriticalError(errorId, ex);
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "Internal Server Error", 
            "An unexpected error occurred. Our team has been notified.",
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "INTERNAL_ERROR");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    // ================== Generic Exception Handlers ================== //
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleGenericRuntimeException(RuntimeException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        
        if (isFrameworkException(ex)) {
            // Framework exceptions are usually less critical
            log.warn("Framework runtime exception [{}]: {} - Type: {}", 
                errorId, ex.getMessage(), ex.getClass().getName());
        } else {
            // Unknown runtime exceptions could be bugs
            log.error("Unexpected runtime exception [{}]: {} - Type: {}", 
                errorId, ex.getMessage(), ex.getClass().getName(), ex);
        }
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred",
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "UNEXPECTED_ERROR");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericCheckedException(Exception ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        
        // Checked exceptions reaching here indicate missing specific handlers
        log.error("Unhandled checked exception [{}] - Missing specific handler for: {} - Message: {}", 
            errorId, ex.getClass().getName(), ex.getMessage(), ex);
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            "An unexpected error occurred",
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "UNHANDLED_EXCEPTION");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    // ================== Utility Methods ================== //
    
    private ProblemDetail createProblemDetail(HttpStatus status, String title, String detail, 
                                              String errorId, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(URI.create(errorBaseUri + status.value()));
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty(ERROR_ID_KEY, errorId);
        problemDetail.setProperty(TIMESTAMP_KEY, Instant.now());
        problemDetail.setProperty(PATH_KEY, request.getRequestURI());
        
        // Add request method for better debugging
        problemDetail.setProperty("method", request.getMethod());
        
        // Optionally include stack trace in non-production environments
        if (includeStackTrace) {
            problemDetail.setProperty("debugInfo", "Check logs with errorId: " + errorId);
        }
        
        return problemDetail;
    }
    
    private String generateErrorId() {
        return UUID.randomUUID().toString();
    }
    
    private boolean isFrameworkException(RuntimeException ex) {
        String className = ex.getClass().getName();
        return className.startsWith("org.springframework") ||
               className.startsWith("jakarta.") ||
               className.startsWith("javax.") ||
               className.startsWith("org.hibernate");
    }
    
    private String extractRootCauseMessage(Exception ex) {
        Throwable rootCause = ex;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage() != null ? rootCause.getMessage() : "Unknown";
    }
    
    private String extractEntityInfo(OptimisticLockingFailureException ex) {
        // Try to extract entity information from the exception message
        String message = ex.getMessage();
        if (message != null && message.contains("entity")) {
            return message;
        }
        return "Unknown entity";
    }
}