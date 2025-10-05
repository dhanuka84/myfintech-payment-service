package org.myfintech.payment.exception.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.hibernate.LazyInitializationException
import org.myfintech.payment.api.v1.PaymentController
import org.myfintech.payment.exception.FileParsingException
import org.myfintech.payment.exception.FileProcessingException
import org.myfintech.payment.exception.Http400BadRequest
import org.myfintech.payment.exception.Http404NotFoundException
import org.myfintech.payment.util.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.*
import org.springframework.transaction.TransactionSystemException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.net.URI
import java.time.Instant
import java.util.*
import java.util.Map
import kotlin.collections.HashMap
import kotlin.collections.MutableMap

/**
 * Global exception handler for the payment service.
 * Handles exceptions with appropriate logging levels and user-friendly responses.
 */
@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = logger<GlobalExceptionHandler>()

    @Value("\${myfintech.error.include-stacktrace:false}")
    private val includeStackTrace = false

    @Value("\${myfintech.error.base-uri:https://myfintech.com/errors/}")
    private val errorBaseUri: String? = null

    // ================== Business/Domain Exceptions (LOG: WARN) ================== //
    @ExceptionHandler(Http404NotFoundException::class)
    fun handleNotFound(ex: Http404NotFoundException, request: HttpServletRequest): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()
        log.warn(
            ExceptionHandlerConstants.RESOURCE_NOT_FOUND_LOG,
            errorId,
            ex.message,
            request.requestURI
        )

        val detail = createProblemDetail(
            HttpStatus.NOT_FOUND,
            ExceptionHandlerConstants.RESOURCE_NOT_FOUND_TITLE,
            ex.message,
            errorId,
            request
        )
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.RESOURCE_NOT_FOUND_CODE)

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body<ProblemDetail?>(detail)
    }

    @ExceptionHandler(Http400BadRequest::class)
    fun handleCustomBadRequest(ex: Http400BadRequest, request: HttpServletRequest): ResponseEntity<ProblemDetail> {
        val errorId = generateErrorId()
        log.warn(ExceptionHandlerConstants.BAD_REQUEST_LOG, errorId, ex.message, ex.errors)

        val detail = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            ExceptionHandlerConstants.BAD_REQUEST_TITLE,
            ex.message,
            errorId,
            request
        )

        ex.errors?.let {
            if (it.isNotEmpty()) {
                detail.setProperty(ExceptionHandlerConstants.VALIDATION_ERRORS_KEY, ex.errors)
            }
        }
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.BAD_REQUEST_CODE)

        return ResponseEntity.badRequest().body(detail)
    }

    // ================== Validation Exceptions (LOG: INFO) ================== //
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()
        log.info(ExceptionHandlerConstants.INVALID_ARGUMENT_LOG, errorId, ex.message)

        val detail = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            ExceptionHandlerConstants.INVALID_ARGUMENT_TITLE,
            ex.message,
            errorId,
            request
        )
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.INVALID_ARGUMENT_CODE)

        return ResponseEntity.badRequest().body<ProblemDetail?>(detail)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()
        val message = String.format(
            ExceptionHandlerConstants.TYPE_MISMATCH_MESSAGE_TEMPLATE,
            ex.name,
            if (ex.requiredType != null) ex.requiredType
                ?.simpleName else ExceptionHandlerConstants.UNKNOWN_TYPE
        )

        log.info(ExceptionHandlerConstants.TYPE_MISMATCH_LOG, errorId, message)

        val detail = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            ExceptionHandlerConstants.TYPE_MISMATCH_TITLE,
            message,
            errorId,
            request
        )
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.TYPE_MISMATCH_CODE)
        detail.setProperty(ExceptionHandlerConstants.PARAMETER_KEY, ex.name)
        detail.setProperty(
            ExceptionHandlerConstants.EXPECTED_TYPE_KEY,
            if (ex.requiredType != null) ex.requiredType
                ?.simpleName else ExceptionHandlerConstants.UNKNOWN_TYPE
        )
        detail.setProperty(ExceptionHandlerConstants.ACTUAL_VALUE_KEY, ex.value)

        return ResponseEntity.badRequest().body<ProblemDetail?>(detail)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        ex: ConstraintViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()
        log.info(
            ExceptionHandlerConstants.CONSTRAINT_VIOLATIONS_LOG,
            errorId,
            ex.constraintViolations.size
        )

        val detail = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            ExceptionHandlerConstants.VALIDATION_FAILED_TITLE,
            ExceptionHandlerConstants.VALIDATION_CONSTRAINTS_VIOLATED,
            errorId,
            request
        )

        detail.setProperty(
            ExceptionHandlerConstants.VALIDATION_ERRORS_KEY, ex.constraintViolations.stream()
                .map<MutableMap<String, String>?> { cv: ConstraintViolation<*>? ->
                    Map.of<String?, String?>(
                        ExceptionHandlerConstants.FIELD_KEY,
                        cv!!.propertyPath.toString(),
                        ExceptionHandlerConstants.MESSAGE_KEY,
                        cv.message,
                        ExceptionHandlerConstants.INVALID_VALUE_KEY,
                        if (cv.invalidValue != null) cv.invalidValue
                            .toString() else ExceptionHandlerConstants.NULL_VALUE
                    )
                }
                .toList())
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.VALIDATION_FAILED_CODE)

        return ResponseEntity.badRequest().body<ProblemDetail?>(detail)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any?>? {
        val errorId = generateErrorId()
        log.info(
            ExceptionHandlerConstants.METHOD_ARGUMENT_VALIDATION_LOG,
            errorId,
            ex.bindingResult.errorCount
        )

        val detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        detail.title = ExceptionHandlerConstants.VALIDATION_FAILED_TITLE
        detail.detail = ExceptionHandlerConstants.REQUEST_VALIDATION_FAILED
        detail.setType(URI.create(errorBaseUri + ExceptionHandlerConstants.VALIDATION_FAILED_URI_SUFFIX))
        detail.setProperty(ExceptionHandlerConstants.ERROR_ID_KEY, errorId)
        detail.setProperty(ExceptionHandlerConstants.TIMESTAMP_KEY, Instant.now())
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.VALIDATION_FAILED_CODE)

        val validationErrors: MutableMap<String?, Any?> = HashMap<String?, Any?>()

        if (!ex.bindingResult.fieldErrors.isEmpty()) {
            validationErrors.put(
                ExceptionHandlerConstants.FIELD_ERRORS_KEY,
                ex.bindingResult.fieldErrors.stream()
                    .map<MutableMap<String, String>?> { e: FieldError? ->
                        Map.of<String?, String?>(
                            ExceptionHandlerConstants.FIELD_KEY,
                            e!!.field,
                            ExceptionHandlerConstants.MESSAGE_KEY,
                            if (e.defaultMessage != null) e.defaultMessage else ExceptionHandlerConstants.INVALID_VALUE_DEFAULT,
                            ExceptionHandlerConstants.REJECTED_VALUE_KEY,
                            if (e.rejectedValue != null) e.rejectedValue
                                .toString() else ExceptionHandlerConstants.NULL_VALUE
                        )
                    }
                    .toList())
        }

        if (!ex.bindingResult.globalErrors.isEmpty()) {
            validationErrors.put(
                ExceptionHandlerConstants.GLOBAL_ERRORS_KEY,
                ex.bindingResult.globalErrors.stream()
                    .map<MutableMap<String, String>?> { e: ObjectError? ->
                        Map.of<String?, String?>(
                            ExceptionHandlerConstants.OBJECT_KEY,
                            e!!.objectName,
                            ExceptionHandlerConstants.MESSAGE_KEY,
                            if (e.defaultMessage != null) e.defaultMessage else ExceptionHandlerConstants.INVALID_OBJECT_DEFAULT
                        )
                    }
                    .toList())
        }

        detail.setProperty(ExceptionHandlerConstants.VALIDATION_ERRORS_KEY, validationErrors)

        return ResponseEntity<Any?>(detail, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(FileProcessingException::class)
    fun handleFileProcessing(ex: FileProcessingException, request: HttpServletRequest): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()
        log.warn(
            ExceptionHandlerConstants.FILE_PROCESSING_ERROR_LOG,
            errorId,
            ex.message,
            if (ex.fileName != null) ex.fileName else ExceptionHandlerConstants.UNKNOWN_FILE
        )

        val detail = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            ExceptionHandlerConstants.FILE_PROCESSING_ERROR_TITLE,
            ex.message,
            errorId,
            request
        )
        detail.setProperty(
            ExceptionHandlerConstants.ERROR_CODE_KEY,
            ExceptionHandlerConstants.FILE_PROCESSING_ERROR_CODE
        )
        if (ex.fileName != null) {
            detail.setProperty(ExceptionHandlerConstants.FILE_NAME_KEY, ex.fileName)
        }

        return ResponseEntity.badRequest().body<ProblemDetail?>(detail)
    }

    @ExceptionHandler(FileParsingException::class)
    fun handleFileParsing(ex: FileParsingException, request: HttpServletRequest): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()
        log.warn(ExceptionHandlerConstants.FILE_PARSING_ERROR_LOG, errorId, ex.message)

        val detail = createProblemDetail(
            HttpStatus.BAD_REQUEST,
            ExceptionHandlerConstants.FILE_PARSING_ERROR_TITLE,
            ex.message,
            errorId,
            request
        )
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.FILE_PARSING_ERROR_CODE)

        return ResponseEntity.badRequest().body<ProblemDetail?>(detail)
    }

    @ExceptionHandler(LazyInitializationException::class)
    fun handleLazyInitializationException(
        ex: LazyInitializationException, request: HttpServletRequest
    ): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()
        log.error(
            "LazyInitializationException [{}]: {} - Path: {}",
            errorId,
            ex.message,
            request.requestURI,
            ex
        )

        val detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Data Loading Error",
            "A required data association could not be loaded. Please contact support or try again.",
            errorId,
            request
        )
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, "LAZY_INITIALIZATION_ERROR")

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<ProblemDetail?>(detail)
    }

    // ================== Data/Infrastructure Exceptions (LOG: ERROR) ================== //
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(
        ex: DataIntegrityViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()
        val rootCause = extractRootCauseMessage(ex)

        log.error(
            ExceptionHandlerConstants.DATA_INTEGRITY_VIOLATION_LOG,
            errorId,
            ex.message,
            rootCause,
            ex
        )

        var userMessage = ExceptionHandlerConstants.DATA_CONSTRAINTS_ERROR

        if (rootCause.lowercase(Locale.getDefault()).contains(ExceptionHandlerConstants.DUPLICATE_KEYWORD)) {
            userMessage = ExceptionHandlerConstants.DUPLICATE_RECORD_ERROR
        } else if (rootCause.lowercase(Locale.getDefault()).contains(ExceptionHandlerConstants.FOREIGN_KEY_KEYWORD)) {
            userMessage = ExceptionHandlerConstants.FOREIGN_KEY_ERROR
        }

        val detail = createProblemDetail(
            HttpStatus.CONFLICT,
            ExceptionHandlerConstants.DATA_CONSTRAINT_VIOLATION_TITLE,
            userMessage,
            errorId,
            request
        )
        detail.setProperty(
            ExceptionHandlerConstants.ERROR_CODE_KEY,
            ExceptionHandlerConstants.DATA_INTEGRITY_VIOLATION_CODE
        )

        return ResponseEntity.status(HttpStatus.CONFLICT).body<ProblemDetail?>(detail)
    }

    @ExceptionHandler(OptimisticLockingFailureException::class)
    fun handleOptimisticLocking(
        ex: OptimisticLockingFailureException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()
        log.warn(
            ExceptionHandlerConstants.OPTIMISTIC_LOCKING_LOG,
            errorId,
            ex.message,
            extractEntityInfo(ex)
        )

        val detail = createProblemDetail(
            HttpStatus.CONFLICT,
            ExceptionHandlerConstants.CONCURRENT_UPDATE_CONFLICT_TITLE,
            ExceptionHandlerConstants.OPTIMISTIC_LOCK_MESSAGE,
            errorId,
            request
        )
        detail.setProperty(
            ExceptionHandlerConstants.ERROR_CODE_KEY,
            ExceptionHandlerConstants.OPTIMISTIC_LOCK_CONFLICT_CODE
        )
        detail.setProperty(
            ExceptionHandlerConstants.SUGGESTION_KEY,
            ExceptionHandlerConstants.OPTIMISTIC_LOCK_SUGGESTION
        )

        return ResponseEntity.status(HttpStatus.CONFLICT).body<ProblemDetail?>(detail)
    }

    @ExceptionHandler(TransactionSystemException::class)
    fun handleTransactionError(
        ex: TransactionSystemException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()
        val rootCause = extractRootCauseMessage(ex)

        log.error(
            ExceptionHandlerConstants.TRANSACTION_ERROR_LOG,
            errorId,
            ex.message,
            rootCause,
            ex
        )

        if (ex.rootCause is ConstraintViolationException) {
            return handleConstraintViolation(
                (ex.rootCause as ConstraintViolationException?)!!,
                request
            )
        }

        val detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ExceptionHandlerConstants.TRANSACTION_FAILED_TITLE,
            ExceptionHandlerConstants.TRANSACTION_COMPLETION_ERROR,
            errorId,
            request
        )
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.TRANSACTION_ERROR_CODE)

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<ProblemDetail?>(detail)
    }

    // ================== Programming Errors (LOG: ERROR + ALERT) ================== //
    @ExceptionHandler(
        NullPointerException::class,
        IndexOutOfBoundsException::class,
        ClassCastException::class,
        IllegalStateException::class
    )
    fun handleProgrammingError(ex: RuntimeException, request: HttpServletRequest): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()

        log.error(
            ExceptionHandlerConstants.PROGRAMMING_ERROR_LOG,
            errorId,
            ex.javaClass.getSimpleName(),
            if (ex.stackTrace.size > 0) ex.stackTrace[0] else ExceptionHandlerConstants.UNKNOWN_LOCATION,
            ex
        )

        val detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ExceptionHandlerConstants.INTERNAL_SERVER_ERROR_TITLE,
            ExceptionHandlerConstants.UNEXPECTED_ERROR_WITH_NOTIFICATION,
            errorId,
            request
        )
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.INTERNAL_ERROR_CODE)

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<ProblemDetail?>(detail)
    }

    // ================== Generic Exception Handlers ================== //
    @ExceptionHandler(RuntimeException::class)
    fun handleGenericRuntimeException(
        ex: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()

        if (isFrameworkException(ex)) {
            log.warn(
                ExceptionHandlerConstants.FRAMEWORK_EXCEPTION_LOG,
                errorId,
                ex.message,
                ex.javaClass.getName()
            )
        } else {
            log.error(
                ExceptionHandlerConstants.UNEXPECTED_RUNTIME_LOG,
                errorId,
                ex.message,
                ex.javaClass.getName(),
                ex
            )
        }

        val detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ExceptionHandlerConstants.INTERNAL_SERVER_ERROR_TITLE,
            ExceptionHandlerConstants.UNEXPECTED_ERROR_MESSAGE,
            errorId,
            request
        )
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.UNEXPECTED_ERROR_CODE)

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<ProblemDetail?>(detail)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericCheckedException(ex: Exception, request: HttpServletRequest): ResponseEntity<ProblemDetail?> {
        val errorId = generateErrorId()

        log.error(
            ExceptionHandlerConstants.UNHANDLED_CHECKED_LOG,
            errorId,
            ex.javaClass.getName(),
            ex.message,
            ex
        )

        val detail = createProblemDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ExceptionHandlerConstants.INTERNAL_SERVER_ERROR_TITLE,
            ExceptionHandlerConstants.UNEXPECTED_ERROR_MESSAGE,
            errorId,
            request
        )
        detail.setProperty(ExceptionHandlerConstants.ERROR_CODE_KEY, ExceptionHandlerConstants.UNHANDLED_EXCEPTION_CODE)

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body<ProblemDetail?>(detail)
    }

    // ================== Utility Methods ================== //
    private fun createProblemDetail(
        status: HttpStatus, title: String?, detail: String?,
        errorId: String?, request: HttpServletRequest
    ): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(status, detail)
        problemDetail.title = title
        problemDetail.setType(URI.create(errorBaseUri + status.value()))
        problemDetail.instance = URI.create(request.requestURI)
        problemDetail.setProperty(ExceptionHandlerConstants.ERROR_ID_KEY, errorId)
        problemDetail.setProperty(ExceptionHandlerConstants.TIMESTAMP_KEY, Instant.now())
        problemDetail.setProperty(ExceptionHandlerConstants.PATH_KEY, request.requestURI)
        problemDetail.setProperty(ExceptionHandlerConstants.METHOD_KEY, request.method)

        if (includeStackTrace) {
            problemDetail.setProperty(
                ExceptionHandlerConstants.DEBUG_INFO_KEY,
                String.format(ExceptionHandlerConstants.DEBUG_INFO_TEMPLATE, errorId)
            )
        }

        return problemDetail
    }

    private fun generateErrorId(): String {
        return UUID.randomUUID().toString()
    }

    private fun isFrameworkException(ex: RuntimeException): Boolean {
        val className = ex.javaClass.getName()
        return className.startsWith("org.springframework") ||
                className.startsWith("jakarta.") ||
                className.startsWith("javax.") ||
                className.startsWith("org.hibernate")
    }

    private fun extractRootCauseMessage(ex: Exception): String {
        var rootCause: Throwable = ex
        while (rootCause.cause != null) {
            rootCause = rootCause.cause!!
        }
        return (if (rootCause.message != null) rootCause.message else ExceptionHandlerConstants.UNKNOWN_TYPE)!!
    }

    private fun extractEntityInfo(ex: OptimisticLockingFailureException): String {
        val message = ex.message
        if (message != null && message.contains(ExceptionHandlerConstants.ENTITY_KEYWORD)) {
            return message
        }
        return ExceptionHandlerConstants.UNKNOWN_ENTITY
    }
}