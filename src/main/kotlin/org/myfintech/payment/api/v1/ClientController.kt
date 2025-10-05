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
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.ClientDTO
import org.myfintech.payment.exception.ProblemDetailSchema
import org.myfintech.payment.service.ClientService
import org.myfintech.payment.validator.CommonValidations.ValidId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/clients")
@Validated
class ClientController(private val clientService: ClientService) {
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
        summary = "Get all clients.",
        description = "Fetch all client records."
    )
    @get:GetMapping
    val allClients: ResponseEntity<List<ClientDTO>>
        // ===============================
        get() {
            val clients = clientService.findAll()
            return ResponseEntity.ok<List<ClientDTO>>(clients)
        }

    // ===============================
    // Get Client by ID
    // ===============================
    @GetMapping("/{id}")
    @Operation(summary = "Get a client by ID.", description = "Fetch a single client by ID.")
    @ApiResponse(responseCode = "200", description = "Successful retrieval.")
    @ApiResponse(
        responseCode = "404",
        description = "Client not found",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    fun getClientById(
        @Parameter(description = "The Id to fetch client for", required = true) @ValidId @PathVariable id: Long
    ): ResponseEntity<ClientDTO> {
        val client = clientService.findById(id)
        return ResponseEntity.ok<ClientDTO>(client)
    }

    // ===============================
    // Create New Client
    // ===============================
    @PostMapping
    @Operation(summary = "Create client entity.", description = "Create client entity.")
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
    fun createClient(@RequestBody client: @Valid ClientCreateDTO): ResponseEntity<ClientDTO> {
        val savedClient = clientService.save(client)
        return ResponseEntity.status(HttpStatus.CREATED).body<ClientDTO>(savedClient)
    }

    // ===============================
    // Update Existing Client
    // ===============================
    @PutMapping("/{id}")
    @Operation(summary = "Update a client.", description = "Update an existing client.")
    @ApiResponse(responseCode = "200", description = "Successfully updated.")
    @ApiResponse(
        responseCode = "400",
        description = "Bad Request.",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "404",
        description = "Client not found",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = [Content(schema = Schema(implementation = ProblemDetailSchema::class))]
    )
    fun updateClient(
        @Parameter(description = "The Id to fetch client for", required = true) @ValidId @PathVariable id: Long,
        @RequestBody client: @Valid ClientDTO
    ): ResponseEntity<ClientDTO> {
        clientService.validate(client)
        val updatedClient = clientService.update(id, client)
        return ResponseEntity.ok<ClientDTO>(updatedClient)
    }
}
