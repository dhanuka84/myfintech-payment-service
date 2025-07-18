/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.api.v1;

import java.util.List;
import java.util.Map;

import org.myfintech.payment.domain.PaymentCreateDTO;
import org.myfintech.payment.domain.PaymentDTO;
import org.myfintech.payment.exception.ProblemDetailSchema;
import org.myfintech.payment.service.PaymentFileUploadService;
import org.myfintech.payment.service.PaymentServiceFacade;
import org.myfintech.payment.validator.CommonValidations.ValidContractNumber;
import org.myfintech.payment.validator.CommonValidations.ValidId;
import org.myfintech.payment.validator.PaymentValidator;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@Validated
public class PaymentController {

	private final PaymentServiceFacade paymentService;
	private final PaymentFileUploadService uploadService;

	public PaymentController(PaymentServiceFacade paymentService, PaymentFileUploadService uploadService,
			PaymentValidator validator) {
		this.paymentService = paymentService;
		this.uploadService = uploadService;
	}

	// ====================================
	// Upload Payments File (.csv or .xml)
	// ====================================
	@PostMapping(value = "/upload/{trackingNumber}", consumes = {
			"multipart/form-data" }, produces = "application/json")
	@Operation(summary = "Upload payments via CSV", description = "Upload and process a file to create payment entries.")
	@ApiResponse(responseCode = "200", description = "File processed successfully.")
	@ApiResponse(responseCode = "400", description = "Invalid format.", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<Map<String, Object>> uploadFile(
			@Parameter(description = "The trackingNumber to upload payments for", required = true) @ValidContractNumber @PathVariable String trackingNumber,
			@RequestParam("file") MultipartFile file) {

		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("error", "Empty file."));
		}

		String filename = file.getOriginalFilename();
		if (filename == null || (!filename.endsWith(".csv") && !filename.endsWith(".xml"))) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "Unsupported file type. Only .csv and .xml are allowed."));
		}

		List<PaymentDTO> payments = uploadService.processFile(file);
		paymentService.validatePaymentsOrFail(payments);
		paymentService.saveAsynch(trackingNumber, payments);

		return ResponseEntity.ok(Map.of("message", "Successfully processed payments", "count", payments.size()));
	}

	// ===============================
	// Get All Payments (Paginated)
	// ===============================
	@GetMapping
	@PageableAsQueryParam // Enables page, size, sort as query params in Swagger
	@Operation(summary = "Get all payments (paginated).", description = "Fetch all payment records with pagination support. Use query parameters to control pagination and sorting.")
	@ApiResponse(responseCode = "200", description = "Successful retrieval.", content = @Content(schema = @Schema(implementation = Page.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<Page<PaymentDTO>> getAllPayments(
			@PageableDefault(size = 20, sort = "id") @SortDefault.SortDefaults({
					@SortDefault(sort = "paymentDate", direction = Sort.Direction.DESC),
					@SortDefault(sort = "id", direction = Sort.Direction.DESC) }) @Parameter(description = "Pagination and sorting parameters", hidden = true) Pageable pageable) {

		log.debug("Fetching payments with pagination: page={}, size={}, sort={}", pageable.getPageNumber(),
				pageable.getPageSize(), pageable.getSort());

		Page<PaymentDTO> payments = paymentService.findAll(pageable);
		return ResponseEntity.ok(payments);
	}

	// ========================================
	// Get Payments by Contract Number
	// ========================================
	@GetMapping("/by-contract/{contractNumber}")
	@Operation(summary = "Get payments by contract.", description = "Fetch payment records for a given contract.")
	@ApiResponse(responseCode = "200", description = "Successful retrieval.")
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<List<PaymentDTO>> findPaymentsByContractNumber(
			@Parameter(description = "The contract number to fetch payments for", required = true) @ValidContractNumber @PathVariable String contractNumber) {

		List<PaymentDTO> payments = paymentService.findPaymentsByContractNumber(contractNumber);
		return ResponseEntity.status(HttpStatus.OK).body(payments);
	}

	// ===============================
	// Get Payment by ID
	// ===============================
	@GetMapping(path = "/{id}", produces = "application/json")
	@Operation(summary = "Get a payment by ID.", description = "Fetch a single payment by ID.")
	@ApiResponse(responseCode = "200", description = "Successful retrieval.")
	@ApiResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<PaymentDTO> getPaymentById(
			@Parameter(description = "The Id to fetch payments for", required = true) @ValidId @PathVariable Long id) {

		PaymentDTO payment = paymentService.findById(id);
		return ResponseEntity.status(HttpStatus.OK).body(payment);
	}

	// ===============================
	// Create Payment
	// ===============================
	@PostMapping(consumes = "application/json", produces = "application/json")
	@Operation(summary = "Create payment entity.", description = "Create a new payment.")
	@ApiResponse(responseCode = "201", description = "Successfully created.")
	@ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentCreateDTO payment) {
		PaymentDTO created = paymentService.save(payment);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	// ===============================
	// Update Payment by ID
	// ===============================
	@PutMapping(path = "/{id}", consumes = "application/json", produces = "application/json")
	@Operation(summary = "Update a payment.", description = "Update an existing payment.")
	@ApiResponse(responseCode = "200", description = "Successfully updated.")
	@ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "404", description = "Payment not found", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<PaymentDTO> updatePayment(
			@Parameter(description = "The Id to update payment for", required = true) @ValidId @PathVariable Long id,
			@Valid @RequestBody PaymentDTO payment) {

		paymentService.validatePaymentsOrFail(List.of(payment));
		PaymentDTO updated = paymentService.update(id, payment);
		return ResponseEntity.ok(updated);
	}
}
