/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.api.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.myfintech.payment.domain.PaymentCreateDTO
import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.exception.ProblemDetailSchema
import org.myfintech.payment.service.PaymentFileUploadService
import org.myfintech.payment.service.PaymentServiceFacade
import org.myfintech.payment.util.logger
import org.myfintech.payment.validator.CommonValidations.ValidContractNumber
import org.myfintech.payment.validator.CommonValidations.ValidId
import org.myfintech.payment.validator.PaymentValidator
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.SortDefault
import org.springframework.data.web.SortDefault.SortDefaults
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.List
import java.util.Map

@RestController
@RequestMapping("/api/v1/payments")
@Validated
class PaymentController(
    private val paymentService: PaymentServiceFacade, private val uploadService: PaymentFileUploadService,
    validator: PaymentValidator
) {
    private val log = logger<PaymentController>()

    // ====================================
    // Upload Payments File (.csv or .xml)
    // ====================================
    @PostMapping(
        value = ["/upload/{trackingNumber}"],
        consumes = ["multipart/form-data"],
        produces = ["application/json"]
    )
    @Operation(
        summary = "Upload payments via CSV",
        description = "Upload and process a file to create payment entries."
    )
    @ApiResponse(responseCode = "200", description = "File processed successfully.")
    @ApiResponse(
        responseCode = "400",
        description = "Invalid format.",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    fun uploadFile(
        @Parameter(
            description = "The trackingNumber to upload payments for",
            required = true
        ) @ValidContractNumber @PathVariable trackingNumber: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<MutableMap<String, Any>> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest()
                .body<MutableMap<String, Any>>(Map.of<String, Any>("error", "Empty file."))
        }

        val filename = file.originalFilename
        if (filename == null || (!filename.endsWith(".csv") && !filename.endsWith(".xml"))) {
            return ResponseEntity.badRequest()
                .body<MutableMap<String, Any>>(
                    Map.of<String, Any>(
                        "error",
                        "Unsupported file type. Only .csv and .xml are allowed."
                    )
                )
        }

        val payments = uploadService.processFile(file)
        paymentService.validatePaymentsOrFail(payments)
        paymentService.saveAsynch(trackingNumber, payments)

        return ResponseEntity.ok<MutableMap<String, Any>>(
            Map.of<String, Any>(
                "message",
                "Successfully processed payments",
                "count",
                payments.size
            )
        )
    }

    // ===============================
    // Get All Payments (Paginated)
    // ===============================
    @GetMapping
    @PageableAsQueryParam // Enables page, size, sort as query params in Swagger
    @Operation(
        summary = "Get all payments (paginated).",
        description = "Fetch all payment records with pagination support. Use query parameters to control pagination and sorting."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successful retrieval.",
        content = [Content(schema = Schema(implementation = Page::class))]
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    fun getAllPayments(
        @PageableDefault(size = 20, sort = ["id"]) @SortDefaults(
            SortDefault(
                sort = arrayOf("paymentDate"),
                direction = Sort.Direction.DESC
            ), SortDefault(sort = arrayOf("id"), direction = Sort.Direction.DESC)
        ) @Parameter(description = "Pagination and sorting parameters", hidden = true) pageable: Pageable
    ): ResponseEntity<Page<PaymentDTO>> {
        log.debug(
            "Fetching payments with pagination: page={}, size={}, sort={}", pageable.pageNumber,
            pageable.pageSize, pageable.sort
        )

        val payments = paymentService.findAll(pageable)
        return ResponseEntity.ok<Page<PaymentDTO>>(payments)
    }

    // ========================================
    // Get Payments by Contract Number
    // ========================================
    @GetMapping("/by-contract/{contractNumber}")
    @Operation(summary = "Get payments by contract.", description = "Fetch payment records for a given contract.")
    @ApiResponse(responseCode = "200", description = "Successful retrieval.")
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    fun findPaymentsByContractNumber(
        @Parameter(
            description = "The contract number to fetch payments for",
            required = true
        ) @ValidContractNumber @PathVariable contractNumber: String
    ): ResponseEntity<kotlin.collections.List<PaymentDTO>> {
        val payments = paymentService.findPaymentsByContractNumber(contractNumber)
        return ResponseEntity.status(HttpStatus.OK).body<kotlin.collections.List<PaymentDTO>>(payments)
    }

    // ===============================
    // Get Payment by ID
    // ===============================
    @GetMapping(path = ["/{id}"], produces = ["application/json"])
    @Operation(summary = "Get a payment by ID.", description = "Fetch a single payment by ID.")
    @ApiResponse(responseCode = "200", description = "Successful retrieval.")
    @ApiResponse(
        responseCode = "404",
        description = "Payment not found",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    fun getPaymentById(
        @Parameter(description = "The Id to fetch payments for", required = true) @ValidId @PathVariable id: Long
    ): ResponseEntity<PaymentDTO> {
        val payment = paymentService.findById(id)
        return ResponseEntity.status(HttpStatus.OK).body<PaymentDTO>(payment)
    }

    // ===============================
    // Create Payment
    // ===============================
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    @Operation(summary = "Create payment entity.", description = "Create a new payment.")
    @ApiResponse(responseCode = "201", description = "Successfully created.")
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request.",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    fun createPayment(@RequestBody payment: @Valid PaymentCreateDTO): ResponseEntity<PaymentDTO> {
        val created = paymentService.save(payment)
        return ResponseEntity.status(HttpStatus.CREATED).body<PaymentDTO>(created)
    }

    // ===============================
    // Update Payment by ID
    // ===============================
    @PutMapping(path = ["/{id}"], consumes = ["application/json"], produces = ["application/json"])
    @Operation(summary = "Update a payment.", description = "Update an existing payment.")
    @ApiResponse(responseCode = "200", description = "Successfully updated.")
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request.",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "404",
        description = "Payment not found",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    fun updatePayment(
        @Parameter(description = "The Id to update payment for", required = true) @ValidId @PathVariable id: Long,
        @RequestBody @Valid  payment: PaymentDTO
    ): ResponseEntity<PaymentDTO> {
        paymentService.validatePaymentsOrFail(listOf(payment))
        val updated = paymentService.update(id, payment)
        return ResponseEntity.ok<PaymentDTO>(updated)
    }
}
