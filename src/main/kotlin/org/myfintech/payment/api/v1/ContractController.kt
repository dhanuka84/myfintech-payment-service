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
import org.myfintech.payment.domain.ContractCreateDTO
import org.myfintech.payment.domain.ContractDTO
import org.myfintech.payment.exception.ProblemDetailSchema
import org.myfintech.payment.service.ContractService
import org.myfintech.payment.validator.CommonValidations.ValidId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/contracts")
@Validated
class ContractController(private val contractService: ContractService) {
    @get:ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(
            schema = Schema(implementation = ProblemDetailSchema::class)
        )]
    )
    @get:ApiResponse(
        responseCode = "200",
        description = "Successful retrieval."
    )
    @get:Operation(
        summary = "Get all contracts.",
        description = "Fetch all contract records."
    )
    @get:GetMapping
    val allContracts: ResponseEntity<List<ContractDTO>>
        // ===============================
        get() {
            val contracts = contractService.findAll()
            return ResponseEntity.ok<List<ContractDTO>>(contracts)
        }

    // ===============================
    // Get Contract by ID
    // ===============================
    @GetMapping("/{id}")
    @Operation(summary = "Get a contract by ID.", description = "Fetch a single contract by ID.")
    @ApiResponse(responseCode = "200", description = "Successful retrieval.")
    @ApiResponse(
        responseCode = "404",
        description = "Contract not found",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    fun getContractById(
        @Parameter(description = "The Id to fetch contract for", required = true) @ValidId @PathVariable id: Long
    ): ResponseEntity<ContractDTO> {
        val contract = contractService.findById(id)
        return ResponseEntity.ok<ContractDTO>(contract)
    }

    // ===============================
    // Create New Contract
    // ===============================
    @PostMapping
    @Operation(summary = "Create contract entity.", description = "Create a new contract.")
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
    fun createContract(@RequestBody contract: @Valid ContractCreateDTO): ResponseEntity<ContractDTO> {
        contractService.validateCreate(contract)
        val saved = contractService.save(contract)
        return ResponseEntity.status(HttpStatus.CREATED).body<ContractDTO>(saved)
    }

    // ===============================
    // Update Existing Contract
    // ===============================
    @PutMapping("/{id}")
    @Operation(summary = "Update a contract.", description = "Update an existing contract.")
    @ApiResponse(responseCode = "200", description = "Successfully updated.")
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request.",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "404",
        description = "Contract not found",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    fun updateContract(
        @Parameter(description = "The Id to fetch contract for", required = true) @ValidId @PathVariable id: Long,
        @RequestBody contract: @Valid ContractDTO
    ): ResponseEntity<ContractDTO> {
        contractService.validateUpdateRequest(id, contract)
        val updated = contractService.update(id, contract)
        return ResponseEntity.ok<ContractDTO>(updated)
    }
}
