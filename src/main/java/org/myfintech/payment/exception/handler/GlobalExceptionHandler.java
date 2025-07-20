package org.myfintech.payment.exception.handler;

import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.ACTUAL_VALUE_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.BAD_REQUEST_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.BAD_REQUEST_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.BAD_REQUEST_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.CONCURRENT_UPDATE_CONFLICT_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.CONSTRAINT_VIOLATIONS_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.DATA_CONSTRAINTS_ERROR;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.DATA_CONSTRAINT_VIOLATION_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.DATA_INTEGRITY_VIOLATION_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.DATA_INTEGRITY_VIOLATION_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.DEBUG_INFO_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.DEBUG_INFO_TEMPLATE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.DUPLICATE_KEYWORD;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.DUPLICATE_RECORD_ERROR;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.ENTITY_KEYWORD;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.ERROR_CODE_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.ERROR_ID_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.EXPECTED_TYPE_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FIELD_ERRORS_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FIELD_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FILE_NAME_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FILE_PARSING_ERROR_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FILE_PARSING_ERROR_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FILE_PARSING_ERROR_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FILE_PROCESSING_ERROR_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FILE_PROCESSING_ERROR_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FILE_PROCESSING_ERROR_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FOREIGN_KEY_ERROR;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FOREIGN_KEY_KEYWORD;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.FRAMEWORK_EXCEPTION_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.GLOBAL_ERRORS_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.INTERNAL_ERROR_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.INTERNAL_SERVER_ERROR_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.INVALID_ARGUMENT_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.INVALID_ARGUMENT_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.INVALID_ARGUMENT_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.INVALID_OBJECT_DEFAULT;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.INVALID_VALUE_DEFAULT;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.INVALID_VALUE_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.MESSAGE_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.METHOD_ARGUMENT_VALIDATION_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.METHOD_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.NULL_VALUE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.OBJECT_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.OPTIMISTIC_LOCKING_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.OPTIMISTIC_LOCK_CONFLICT_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.OPTIMISTIC_LOCK_MESSAGE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.OPTIMISTIC_LOCK_SUGGESTION;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.PARAMETER_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.PATH_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.PROGRAMMING_ERROR_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.REJECTED_VALUE_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.REQUEST_VALIDATION_FAILED;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.RESOURCE_NOT_FOUND_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.RESOURCE_NOT_FOUND_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.RESOURCE_NOT_FOUND_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.SUGGESTION_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.TIMESTAMP_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.TRANSACTION_COMPLETION_ERROR;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.TRANSACTION_ERROR_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.TRANSACTION_ERROR_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.TRANSACTION_FAILED_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.TYPE_MISMATCH_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.TYPE_MISMATCH_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.TYPE_MISMATCH_MESSAGE_TEMPLATE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.TYPE_MISMATCH_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.UNEXPECTED_ERROR_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.UNEXPECTED_ERROR_MESSAGE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.UNEXPECTED_ERROR_WITH_NOTIFICATION;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.UNEXPECTED_RUNTIME_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.UNHANDLED_CHECKED_LOG;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.UNHANDLED_EXCEPTION_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.UNKNOWN_ENTITY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.UNKNOWN_FILE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.UNKNOWN_LOCATION;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.UNKNOWN_TYPE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.VALIDATION_CONSTRAINTS_VIOLATED;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.VALIDATION_ERRORS_KEY;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.VALIDATION_FAILED_CODE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.VALIDATION_FAILED_TITLE;
import static org.myfintech.payment.exception.handler.ExceptionHandlerConstants.VALIDATION_FAILED_URI_SUFFIX;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.hibernate.LazyInitializationException;
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
    
    @Value("${myfintech.error.include-stacktrace:false}")
    private boolean includeStackTrace;
    
    @Value("${myfintech.error.base-uri:https://myfintech.com/errors/}")
    private String errorBaseUri;

    // ================== Business/Domain Exceptions (LOG: WARN) ================== //
    
    @ExceptionHandler(Http404NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(Http404NotFoundException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.warn(RESOURCE_NOT_FOUND_LOG, errorId, ex.getMessage(), request.getRequestURI());
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.NOT_FOUND, 
            RESOURCE_NOT_FOUND_TITLE, 
            ex.getMessage(),
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, RESOURCE_NOT_FOUND_CODE);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detail);
    }

    @ExceptionHandler(Http400BadRequest.class)
    public ResponseEntity<ProblemDetail> handleCustomBadRequest(Http400BadRequest ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.warn(BAD_REQUEST_LOG, errorId, ex.getMessage(), ex.getErrors());
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST, 
            BAD_REQUEST_TITLE, 
            ex.getMessage(),
            errorId,
            request
        );
        
        if (!ex.getErrors().isEmpty()) {
            detail.setProperty(VALIDATION_ERRORS_KEY, ex.getErrors());
        }
        detail.setProperty(ERROR_CODE_KEY, BAD_REQUEST_CODE);
        
        return ResponseEntity.badRequest().body(detail);
    }

    // ================== Validation Exceptions (LOG: INFO) ================== //
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.info(INVALID_ARGUMENT_LOG, errorId, ex.getMessage());
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST, 
            INVALID_ARGUMENT_TITLE, 
            ex.getMessage(),
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, INVALID_ARGUMENT_CODE);
        
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        String message = String.format(TYPE_MISMATCH_MESSAGE_TEMPLATE, 
            ex.getName(), 
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : UNKNOWN_TYPE);
        
        log.info(TYPE_MISMATCH_LOG, errorId, message);
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST, 
            TYPE_MISMATCH_TITLE, 
            message,
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, TYPE_MISMATCH_CODE);
        detail.setProperty(PARAMETER_KEY, ex.getName());
        detail.setProperty(EXPECTED_TYPE_KEY, ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : UNKNOWN_TYPE);
        detail.setProperty(ACTUAL_VALUE_KEY, ex.getValue());
        
        return ResponseEntity.badRequest().body(detail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.info(CONSTRAINT_VIOLATIONS_LOG, errorId, ex.getConstraintViolations().size());
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST, 
            VALIDATION_FAILED_TITLE, 
            VALIDATION_CONSTRAINTS_VIOLATED,
            errorId,
            request
        );
        
        detail.setProperty(VALIDATION_ERRORS_KEY, ex.getConstraintViolations().stream()
            .map(cv -> Map.of(
                FIELD_KEY, cv.getPropertyPath().toString(), 
                MESSAGE_KEY, cv.getMessage(),
                INVALID_VALUE_KEY, cv.getInvalidValue() != null ? cv.getInvalidValue().toString() : NULL_VALUE
            ))
            .toList());
        detail.setProperty(ERROR_CODE_KEY, VALIDATION_FAILED_CODE);
        
        return ResponseEntity.badRequest().body(detail);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        String errorId = generateErrorId();
        log.info(METHOD_ARGUMENT_VALIDATION_LOG, errorId, ex.getBindingResult().getErrorCount());

        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle(VALIDATION_FAILED_TITLE);
        detail.setDetail(REQUEST_VALIDATION_FAILED);
        detail.setType(URI.create(errorBaseUri + VALIDATION_FAILED_URI_SUFFIX));
        detail.setProperty(ERROR_ID_KEY, errorId);
        detail.setProperty(TIMESTAMP_KEY, Instant.now());
        detail.setProperty(ERROR_CODE_KEY, VALIDATION_FAILED_CODE);
        
        Map<String, Object> validationErrors = new HashMap<>();
        
        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            validationErrors.put(FIELD_ERRORS_KEY, ex.getBindingResult().getFieldErrors().stream()
                .map(e -> Map.of(
                    FIELD_KEY, e.getField(),
                    MESSAGE_KEY, e.getDefaultMessage() != null ? e.getDefaultMessage() : INVALID_VALUE_DEFAULT,
                    REJECTED_VALUE_KEY, e.getRejectedValue() != null ? e.getRejectedValue().toString() : NULL_VALUE
                ))
                .toList());
        }
        
        if (!ex.getBindingResult().getGlobalErrors().isEmpty()) {
            validationErrors.put(GLOBAL_ERRORS_KEY, ex.getBindingResult().getGlobalErrors().stream()
                .map(e -> Map.of(
                    OBJECT_KEY, e.getObjectName(),
                    MESSAGE_KEY, e.getDefaultMessage() != null ? e.getDefaultMessage() : INVALID_OBJECT_DEFAULT
                ))
                .toList());
        }
        
        detail.setProperty(VALIDATION_ERRORS_KEY, validationErrors);

        return new ResponseEntity<>(detail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ProblemDetail> handleFileProcessing(FileProcessingException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.warn(FILE_PROCESSING_ERROR_LOG, 
            errorId, ex.getMessage(), ex.getFileName() != null ? ex.getFileName() : UNKNOWN_FILE);
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST, 
            FILE_PROCESSING_ERROR_TITLE, 
            ex.getMessage(),
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, FILE_PROCESSING_ERROR_CODE);
        if (ex.getFileName() != null) {
            detail.setProperty(FILE_NAME_KEY, ex.getFileName());
        }
        
        return ResponseEntity.badRequest().body(detail);
    }
    
    @ExceptionHandler(FileParsingException.class)
    public ResponseEntity<ProblemDetail> handleFileParsing(FileParsingException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.warn(FILE_PARSING_ERROR_LOG, errorId, ex.getMessage());

        ProblemDetail detail = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            FILE_PARSING_ERROR_TITLE,
            ex.getMessage(),
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, FILE_PARSING_ERROR_CODE);

        return ResponseEntity.badRequest().body(detail);
    }
    
    @ExceptionHandler(org.hibernate.LazyInitializationException.class)
    public ResponseEntity<ProblemDetail> handleLazyInitializationException(
            LazyInitializationException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.error("LazyInitializationException [{}]: {} - Path: {}", errorId, ex.getMessage(), request.getRequestURI(), ex);

        ProblemDetail detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Data Loading Error",
            "A required data association could not be loaded. Please contact support or try again.",
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, "LAZY_INITIALIZATION_ERROR");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    // ================== Data/Infrastructure Exceptions (LOG: ERROR) ================== //
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        String rootCause = extractRootCauseMessage(ex);
        
        log.error(DATA_INTEGRITY_VIOLATION_LOG, errorId, ex.getMessage(), rootCause, ex);
        
        String userMessage = DATA_CONSTRAINTS_ERROR;
        
        if (rootCause.toLowerCase().contains(DUPLICATE_KEYWORD)) {
            userMessage = DUPLICATE_RECORD_ERROR;
        } else if (rootCause.toLowerCase().contains(FOREIGN_KEY_KEYWORD)) {
            userMessage = FOREIGN_KEY_ERROR;
        }
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.CONFLICT, 
            DATA_CONSTRAINT_VIOLATION_TITLE, 
            userMessage,
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, DATA_INTEGRITY_VIOLATION_CODE);
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(detail);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLocking(OptimisticLockingFailureException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        log.warn(OPTIMISTIC_LOCKING_LOG, errorId, ex.getMessage(), extractEntityInfo(ex));
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.CONFLICT, 
            CONCURRENT_UPDATE_CONFLICT_TITLE, 
            OPTIMISTIC_LOCK_MESSAGE,
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, OPTIMISTIC_LOCK_CONFLICT_CODE);
        detail.setProperty(SUGGESTION_KEY, OPTIMISTIC_LOCK_SUGGESTION);
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(detail);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ProblemDetail> handleTransactionError(TransactionSystemException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        String rootCause = extractRootCauseMessage(ex);
        
        log.error(TRANSACTION_ERROR_LOG, errorId, ex.getMessage(), rootCause, ex);
        
        if (ex.getRootCause() instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) ex.getRootCause(), request);
        }
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            TRANSACTION_FAILED_TITLE, 
            TRANSACTION_COMPLETION_ERROR,
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, TRANSACTION_ERROR_CODE);
        
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
        
        log.error(PROGRAMMING_ERROR_LOG, 
            errorId, 
            ex.getClass().getSimpleName(), 
            ex.getStackTrace().length > 0 ? ex.getStackTrace()[0] : UNKNOWN_LOCATION, 
            ex);
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            INTERNAL_SERVER_ERROR_TITLE, 
            UNEXPECTED_ERROR_WITH_NOTIFICATION,
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, INTERNAL_ERROR_CODE);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }

    // ================== Generic Exception Handlers ================== //
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ProblemDetail> handleGenericRuntimeException(RuntimeException ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        
        if (isFrameworkException(ex)) {
            log.warn(FRAMEWORK_EXCEPTION_LOG, errorId, ex.getMessage(), ex.getClass().getName());
        } else {
            log.error(UNEXPECTED_RUNTIME_LOG, errorId, ex.getMessage(), ex.getClass().getName(), ex);
        }
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            INTERNAL_SERVER_ERROR_TITLE,
            UNEXPECTED_ERROR_MESSAGE,
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, UNEXPECTED_ERROR_CODE);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(detail);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericCheckedException(Exception ex, HttpServletRequest request) {
        String errorId = generateErrorId();
        
        log.error(UNHANDLED_CHECKED_LOG, errorId, ex.getClass().getName(), ex.getMessage(), ex);
        
        ProblemDetail detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            INTERNAL_SERVER_ERROR_TITLE,
            UNEXPECTED_ERROR_MESSAGE,
            errorId,
            request
        );
        detail.setProperty(ERROR_CODE_KEY, UNHANDLED_EXCEPTION_CODE);
        
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
        problemDetail.setProperty(METHOD_KEY, request.getMethod());
        
        if (includeStackTrace) {
            problemDetail.setProperty(DEBUG_INFO_KEY, String.format(DEBUG_INFO_TEMPLATE, errorId));
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
        return rootCause.getMessage() != null ? rootCause.getMessage() : UNKNOWN_TYPE;
    }
    
    private String extractEntityInfo(OptimisticLockingFailureException ex) {
        String message = ex.getMessage();
        if (message != null && message.contains(ENTITY_KEYWORD)) {
            return message;
        }
        return UNKNOWN_ENTITY;
    }
}