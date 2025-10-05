package org.myfintech.payment.exception.handler

/**
 * Constants used in exception handling for consistent error responses.
 * Centralizes all string literals to avoid duplication and improve maintainability.
 */
object ExceptionHandlerConstants {
    // ================== Property Keys ================== //
    /**
     * Standard error response property keys
     */
    const val ERROR_ID_KEY: String = "errorId"
    const val TIMESTAMP_KEY: String = "timestamp"
    const val PATH_KEY: String = "path"
    const val ERROR_CODE_KEY: String = "errorCode"
    const val METHOD_KEY: String = "method"
    const val DEBUG_INFO_KEY: String = "debugInfo"

    /**
     * Validation-specific property keys
     */
    const val VALIDATION_ERRORS_KEY: String = "validationErrors"
    const val FIELD_ERRORS_KEY: String = "fieldErrors"
    const val GLOBAL_ERRORS_KEY: String = "globalErrors"
    const val FIELD_KEY: String = "field"
    const val MESSAGE_KEY: String = "message"
    const val REJECTED_VALUE_KEY: String = "rejectedValue"
    const val INVALID_VALUE_KEY: String = "invalidValue"
    const val OBJECT_KEY: String = "object"

    /**
     * Type mismatch property keys
     */
    const val PARAMETER_KEY: String = "parameter"
    const val EXPECTED_TYPE_KEY: String = "expectedType"
    const val ACTUAL_VALUE_KEY: String = "actualValue"

    /**
     * File processing property keys
     */
    const val FILE_NAME_KEY: String = "fileName"

    /**
     * Suggestion property key
     */
    const val SUGGESTION_KEY: String = "suggestion"

    // ================== Error Titles ================== //
    const val RESOURCE_NOT_FOUND_TITLE: String = "Resource Not Found"
    const val BAD_REQUEST_TITLE: String = "Bad Request"
    const val INVALID_ARGUMENT_TITLE: String = "Invalid Argument"
    const val TYPE_MISMATCH_TITLE: String = "Type Mismatch"
    const val VALIDATION_FAILED_TITLE: String = "Validation Failed"
    const val FILE_PROCESSING_ERROR_TITLE: String = "File Processing Error"
    const val FILE_PARSING_ERROR_TITLE: String = "File Parsing Error"
    const val DATA_CONSTRAINT_VIOLATION_TITLE: String = "Data Constraint Violation"
    const val CONCURRENT_UPDATE_CONFLICT_TITLE: String = "Concurrent Update Conflict"
    const val TRANSACTION_FAILED_TITLE: String = "Transaction Failed"
    const val INTERNAL_SERVER_ERROR_TITLE: String = "Internal Server Error"

    // ================== Error Messages ================== //
    /**
     * Generic error messages
     */
    const val UNEXPECTED_ERROR_MESSAGE: String = "An unexpected error occurred"
    const val UNEXPECTED_ERROR_WITH_NOTIFICATION: String = "An unexpected error occurred. Our team has been notified."
    const val INVALID_VALUE_DEFAULT: String = "Invalid value"
    const val INVALID_OBJECT_DEFAULT: String = "Invalid object"
    const val UNKNOWN_TYPE: String = "unknown"
    const val UNKNOWN_ENTITY: String = "Unknown entity"
    const val UNKNOWN_LOCATION: String = "unknown location"
    const val UNKNOWN_FILE: String = "unknown"
    const val NULL_VALUE: String = "null"

    /**
     * Validation error messages
     */
    const val VALIDATION_CONSTRAINTS_VIOLATED: String = "One or more validation constraints were violated"
    const val REQUEST_VALIDATION_FAILED: String = "Request validation failed. Please check the errors and try again."
    const val CLIENT_NOT_FOUND: String = "client not found: "
    const val CONTRACT_NOT_FOUND: String = "contract not found: "

    /**
     * Data integrity messages
     */
    const val DATA_CONSTRAINTS_ERROR: String = "Unable to process request due to data constraints"
    const val DUPLICATE_RECORD_ERROR: String = "A record with the same information already exists"
    const val FOREIGN_KEY_ERROR: String = "Referenced data not found or cannot be deleted due to existing dependencies"

    /**
     * Concurrency messages
     */
    const val OPTIMISTIC_LOCK_MESSAGE: String = "The record was modified by another user. Please refresh and try again."
    const val OPTIMISTIC_LOCK_SUGGESTION: String = "Refresh the data and retry your changes"

    /**
     * Transaction messages
     */
    const val TRANSACTION_COMPLETION_ERROR: String = "Unable to complete the transaction. Please try again."

    // ================== Error Codes ================== //
    const val RESOURCE_NOT_FOUND_CODE: String = "RESOURCE_NOT_FOUND"
    const val BAD_REQUEST_CODE: String = "BAD_REQUEST"
    const val INVALID_ARGUMENT_CODE: String = "INVALID_ARGUMENT"
    const val TYPE_MISMATCH_CODE: String = "TYPE_MISMATCH"
    const val VALIDATION_FAILED_CODE: String = "VALIDATION_FAILED"
    const val FILE_PROCESSING_ERROR_CODE: String = "FILE_PROCESSING_ERROR"
    const val FILE_PARSING_ERROR_CODE: String = "FILE_PARSING_ERROR"
    const val DATA_INTEGRITY_VIOLATION_CODE: String = "DATA_INTEGRITY_VIOLATION"
    const val OPTIMISTIC_LOCK_CONFLICT_CODE: String = "OPTIMISTIC_LOCK_CONFLICT"
    const val TRANSACTION_ERROR_CODE: String = "TRANSACTION_ERROR"
    const val INTERNAL_ERROR_CODE: String = "INTERNAL_ERROR"
    const val UNEXPECTED_ERROR_CODE: String = "UNEXPECTED_ERROR"
    const val UNHANDLED_EXCEPTION_CODE: String = "UNHANDLED_EXCEPTION"

    // ================== Log Messages ================== //
    /**
     * Log message templates
     */
    const val RESOURCE_NOT_FOUND_LOG: String = "Resource not found [{}]: {} - Path: {}"
    const val BAD_REQUEST_LOG: String = "Bad request [{}]: {} - Errors: {}"
    const val INVALID_ARGUMENT_LOG: String = "Invalid argument [{}]: {}"
    const val TYPE_MISMATCH_LOG: String = "Type mismatch [{}]: {}"
    const val CONSTRAINT_VIOLATIONS_LOG: String = "Constraint violations [{}]: {}"
    const val METHOD_ARGUMENT_VALIDATION_LOG: String = "Method argument validation failed [{}]: {} errors"
    const val FILE_PROCESSING_ERROR_LOG: String = "File processing error [{}]: {} - File: {}"
    const val FILE_PARSING_ERROR_LOG: String = "File parsing error [{}]: {}"
    const val DATA_INTEGRITY_VIOLATION_LOG: String = "Data integrity violation [{}]: {} - Root cause: {}"
    const val OPTIMISTIC_LOCKING_LOG: String = "Optimistic locking conflict [{}]: {} - Entity: {}"
    const val TRANSACTION_ERROR_LOG: String = "Transaction system error [{}]: {} - Root cause: {}"
    const val PROGRAMMING_ERROR_LOG: String =
        "CRITICAL: Programming error detected [{}] - THIS IS A BUG! Exception: {} at {}"
    const val FRAMEWORK_EXCEPTION_LOG: String = "Framework runtime exception [{}]: {} - Type: {}"
    const val UNEXPECTED_RUNTIME_LOG: String = "Unexpected runtime exception [{}]: {} - Type: {}"
    const val UNHANDLED_CHECKED_LOG: String =
        "Unhandled checked exception [{}] - Missing specific handler for: {} - Message: {}"

    /**
     * Type mismatch message template
     */
    const val TYPE_MISMATCH_MESSAGE_TEMPLATE: String = "Parameter '%s' must be of type %s"

    /**
     * Debug info message template
     */
    const val DEBUG_INFO_TEMPLATE: String = "Check logs with errorId: %s"

    // ================== Content Types ================== //
    const val DUPLICATE_KEYWORD: String = "duplicate"
    const val FOREIGN_KEY_KEYWORD: String = "foreign key"
    const val ENTITY_KEYWORD: String = "entity"

    // ================== URL Templates ================== //
    const val URI_PREFIX: String = "uri="
    const val VALIDATION_FAILED_URI_SUFFIX: String = "validation-failed"
}