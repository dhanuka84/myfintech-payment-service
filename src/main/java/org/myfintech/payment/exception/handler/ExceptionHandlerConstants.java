package org.myfintech.payment.exception.handler;

/**
 * Constants used in exception handling for consistent error responses.
 * Centralizes all string literals to avoid duplication and improve maintainability.
 */
public final class ExceptionHandlerConstants {
    
    private ExceptionHandlerConstants() {
        // Prevent instantiation
    }
    
    // ================== Property Keys ================== //
    
    /**
     * Standard error response property keys
     */
    public static final String ERROR_ID_KEY = "errorId";
    public static final String TIMESTAMP_KEY = "timestamp";
    public static final String PATH_KEY = "path";
    public static final String ERROR_CODE_KEY = "errorCode";
    public static final String METHOD_KEY = "method";
    public static final String DEBUG_INFO_KEY = "debugInfo";
    
    /**
     * Validation-specific property keys
     */
    public static final String VALIDATION_ERRORS_KEY = "validationErrors";
    public static final String FIELD_ERRORS_KEY = "fieldErrors";
    public static final String GLOBAL_ERRORS_KEY = "globalErrors";
    public static final String FIELD_KEY = "field";
    public static final String MESSAGE_KEY = "message";
    public static final String REJECTED_VALUE_KEY = "rejectedValue";
    public static final String INVALID_VALUE_KEY = "invalidValue";
    public static final String OBJECT_KEY = "object";
    
    /**
     * Type mismatch property keys
     */
    public static final String PARAMETER_KEY = "parameter";
    public static final String EXPECTED_TYPE_KEY = "expectedType";
    public static final String ACTUAL_VALUE_KEY = "actualValue";
    
    /**
     * File processing property keys
     */
    public static final String FILE_NAME_KEY = "fileName";
    
    /**
     * Suggestion property key
     */
    public static final String SUGGESTION_KEY = "suggestion";
    
    // ================== Error Titles ================== //
    
    public static final String RESOURCE_NOT_FOUND_TITLE = "Resource Not Found";
    public static final String BAD_REQUEST_TITLE = "Bad Request";
    public static final String INVALID_ARGUMENT_TITLE = "Invalid Argument";
    public static final String TYPE_MISMATCH_TITLE = "Type Mismatch";
    public static final String VALIDATION_FAILED_TITLE = "Validation Failed";
    public static final String FILE_PROCESSING_ERROR_TITLE = "File Processing Error";
    public static final String FILE_PARSING_ERROR_TITLE = "File Parsing Error";
    public static final String DATA_CONSTRAINT_VIOLATION_TITLE = "Data Constraint Violation";
    public static final String CONCURRENT_UPDATE_CONFLICT_TITLE = "Concurrent Update Conflict";
    public static final String TRANSACTION_FAILED_TITLE = "Transaction Failed";
    public static final String INTERNAL_SERVER_ERROR_TITLE = "Internal Server Error";
    
    // ================== Error Messages ================== //
    
    /**
     * Generic error messages
     */
    public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";
    public static final String UNEXPECTED_ERROR_WITH_NOTIFICATION = "An unexpected error occurred. Our team has been notified.";
    public static final String INVALID_VALUE_DEFAULT = "Invalid value";
    public static final String INVALID_OBJECT_DEFAULT = "Invalid object";
    public static final String UNKNOWN_TYPE = "unknown";
    public static final String UNKNOWN_ENTITY = "Unknown entity";
    public static final String UNKNOWN_LOCATION = "unknown location";
    public static final String UNKNOWN_FILE = "unknown";
    public static final String NULL_VALUE = "null";
    
    /**
     * Validation error messages
     */
    public static final String VALIDATION_CONSTRAINTS_VIOLATED = "One or more validation constraints were violated";
    public static final String REQUEST_VALIDATION_FAILED = "Request validation failed. Please check the errors and try again.";
    public static final String CLIENT_NOT_FOUND = "client not found: ";
    public static final String CONTRACT_NOT_FOUND = "contract not found: ";
    
    /**
     * Data integrity messages
     */
    public static final String DATA_CONSTRAINTS_ERROR = "Unable to process request due to data constraints";
    public static final String DUPLICATE_RECORD_ERROR = "A record with the same information already exists";
    public static final String FOREIGN_KEY_ERROR = "Referenced data not found or cannot be deleted due to existing dependencies";
    
    /**
     * Concurrency messages
     */
    public static final String OPTIMISTIC_LOCK_MESSAGE = "The record was modified by another user. Please refresh and try again.";
    public static final String OPTIMISTIC_LOCK_SUGGESTION = "Refresh the data and retry your changes";
    
    /**
     * Transaction messages
     */
    public static final String TRANSACTION_COMPLETION_ERROR = "Unable to complete the transaction. Please try again.";
    
    // ================== Error Codes ================== //
    
    public static final String RESOURCE_NOT_FOUND_CODE = "RESOURCE_NOT_FOUND";
    public static final String BAD_REQUEST_CODE = "BAD_REQUEST";
    public static final String INVALID_ARGUMENT_CODE = "INVALID_ARGUMENT";
    public static final String TYPE_MISMATCH_CODE = "TYPE_MISMATCH";
    public static final String VALIDATION_FAILED_CODE = "VALIDATION_FAILED";
    public static final String FILE_PROCESSING_ERROR_CODE = "FILE_PROCESSING_ERROR";
    public static final String FILE_PARSING_ERROR_CODE = "FILE_PARSING_ERROR";
    public static final String DATA_INTEGRITY_VIOLATION_CODE = "DATA_INTEGRITY_VIOLATION";
    public static final String OPTIMISTIC_LOCK_CONFLICT_CODE = "OPTIMISTIC_LOCK_CONFLICT";
    public static final String TRANSACTION_ERROR_CODE = "TRANSACTION_ERROR";
    public static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";
    public static final String UNEXPECTED_ERROR_CODE = "UNEXPECTED_ERROR";
    public static final String UNHANDLED_EXCEPTION_CODE = "UNHANDLED_EXCEPTION";
    
    // ================== Log Messages ================== //
    
    /**
     * Log message templates
     */
    public static final String RESOURCE_NOT_FOUND_LOG = "Resource not found [{}]: {} - Path: {}";
    public static final String BAD_REQUEST_LOG = "Bad request [{}]: {} - Errors: {}";
    public static final String INVALID_ARGUMENT_LOG = "Invalid argument [{}]: {}";
    public static final String TYPE_MISMATCH_LOG = "Type mismatch [{}]: {}";
    public static final String CONSTRAINT_VIOLATIONS_LOG = "Constraint violations [{}]: {}";
    public static final String METHOD_ARGUMENT_VALIDATION_LOG = "Method argument validation failed [{}]: {} errors";
    public static final String FILE_PROCESSING_ERROR_LOG = "File processing error [{}]: {} - File: {}";
    public static final String FILE_PARSING_ERROR_LOG = "File parsing error [{}]: {}";
    public static final String DATA_INTEGRITY_VIOLATION_LOG = "Data integrity violation [{}]: {} - Root cause: {}";
    public static final String OPTIMISTIC_LOCKING_LOG = "Optimistic locking conflict [{}]: {} - Entity: {}";
    public static final String TRANSACTION_ERROR_LOG = "Transaction system error [{}]: {} - Root cause: {}";
    public static final String PROGRAMMING_ERROR_LOG = "CRITICAL: Programming error detected [{}] - THIS IS A BUG! Exception: {} at {}";
    public static final String FRAMEWORK_EXCEPTION_LOG = "Framework runtime exception [{}]: {} - Type: {}";
    public static final String UNEXPECTED_RUNTIME_LOG = "Unexpected runtime exception [{}]: {} - Type: {}";
    public static final String UNHANDLED_CHECKED_LOG = "Unhandled checked exception [{}] - Missing specific handler for: {} - Message: {}";
    
    /**
     * Type mismatch message template
     */
    public static final String TYPE_MISMATCH_MESSAGE_TEMPLATE = "Parameter '%s' must be of type %s";
    
    /**
     * Debug info message template
     */
    public static final String DEBUG_INFO_TEMPLATE = "Check logs with errorId: %s";
    
    // ================== Content Types ================== //
    
    public static final String DUPLICATE_KEYWORD = "duplicate";
    public static final String FOREIGN_KEY_KEYWORD = "foreign key";
    public static final String ENTITY_KEYWORD = "entity";
    
    // ================== URL Templates ================== //
    
    public static final String URI_PREFIX = "uri=";
    public static final String VALIDATION_FAILED_URI_SUFFIX = "validation-failed";
}