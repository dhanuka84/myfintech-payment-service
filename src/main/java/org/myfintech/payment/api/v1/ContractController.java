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

import org.myfintech.payment.domain.ContractCreateDTO;
import org.myfintech.payment.domain.ContractDTO;
import org.myfintech.payment.exception.ProblemDetailSchema;
import org.myfintech.payment.service.ContractService;
import org.myfintech.payment.validator.CommonValidations.ValidId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/contracts")
@Validated
public class ContractController {

	private final ContractService contractService;

	public ContractController(ContractService contractService) {
		this.contractService = contractService;
	}

	// ===============================
	// Get All Contracts
	// ===============================
	@GetMapping
	@Operation(summary = "Get all contracts.", description = "Fetch all contract records.")
	@ApiResponse(responseCode = "200", description = "Successful retrieval.")
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<List<ContractDTO>> getAllContracts() {
		List<ContractDTO> contracts = contractService.findAll();
		return ResponseEntity.ok(contracts);
	}

	// ===============================
	// Get Contract by ID
	// ===============================
	@GetMapping("/{id}")
	@Operation(summary = "Get a contract by ID.", description = "Fetch a single contract by ID.")
	@ApiResponse(responseCode = "200", description = "Successful retrieval.")
	@ApiResponse(responseCode = "404", description = "Contract not found", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))

	public ResponseEntity<ContractDTO> getContractById(
			@Parameter(description = "The Id to fetch contract for", required = true) @ValidId @PathVariable Long id) {

		ContractDTO contract = contractService.findById(id);
		return ResponseEntity.ok(contract);
	}

	// ===============================
	// Create New Contract
	// ===============================
	@PostMapping
	@Operation(summary = "Create contract entity.", description = "Create a new contract.")
	@ApiResponse(responseCode = "201", description = "Successfully created.")
	@ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))

	public ResponseEntity<ContractDTO> createContract(@Valid @RequestBody ContractCreateDTO contract) {
		contractService.validateCreate(contract);
		ContractDTO saved = contractService.save(contract);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	// ===============================
	// Update Existing Contract
	// ===============================
	@PutMapping("/{id}")
	@Operation(summary = "Update a contract.", description = "Update an existing contract.")
	@ApiResponse(responseCode = "200", description = "Successfully updated.")
	@ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "404", description = "Contract not found", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))

	public ResponseEntity<ContractDTO> updateContract(
			@Parameter(description = "The Id to fetch contract for", required = true) @ValidId @PathVariable Long id,
			@Valid @RequestBody ContractDTO contract) {

		contractService.validateUpdateRequest(id, contract);
		ContractDTO updated = contractService.update(id, contract);
		return ResponseEntity.ok(updated);
	}
}
