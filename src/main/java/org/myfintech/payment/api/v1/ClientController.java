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

import org.myfintech.payment.domain.ClientCreateDTO;
import org.myfintech.payment.domain.ClientDTO;
import org.myfintech.payment.exception.ProblemDetailSchema;
import org.myfintech.payment.service.ClientService;
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
@RequestMapping("/api/v1/clients")
@Validated
public class ClientController {

	private final ClientService clientService;

	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}

	// ===============================
	// Get All Clients
	// ===============================
	@GetMapping
	@Operation(summary = "Get all clients.", description = "Fetch all client records.")
	@ApiResponse(responseCode = "200", description = "Successful retrieval.")
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<List<ClientDTO>> getAllClients() {
		List<ClientDTO> clients = clientService.findAll();
		return ResponseEntity.ok(clients);
	}

	// ===============================
	// Get Client by ID
	// ===============================
	@GetMapping("/{id}")
	@Operation(summary = "Get a client by ID.", description = "Fetch a single client by ID.")
	@ApiResponse(responseCode = "200", description = "Successful retrieval.")
	@ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<ClientDTO> getClientById(
			@Parameter(description = "The Id to fetch client for", required = true) @ValidId @PathVariable Long id) {

		ClientDTO client = clientService.findById(id);
		return ResponseEntity.ok(client);
	}

	// ===============================
	// Create New Client
	// ===============================
	@PostMapping
	@Operation(summary = "Create client entity.", description = "Create client entity.")
	@ApiResponse(responseCode = "201", description = "Successfully created.")
	@ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientCreateDTO client) {

		ClientDTO savedClient = clientService.save(client);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
	}

	// ===============================
	// Update Existing Client
	// ===============================
	@PutMapping("/{id}")
	@Operation(summary = "Update a client.", description = "Update an existing client.")
	@ApiResponse(responseCode = "200", description = "Successfully updated.")
	@ApiResponse(responseCode = "400", description = "Bad Request.", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ProblemDetailSchema.class)))
	public ResponseEntity<ClientDTO> updateClient(
			@Parameter(description = "The Id to fetch client for", required = true) @ValidId @PathVariable Long id,
			@Valid @RequestBody ClientDTO client) {
		clientService.validate(client);
		ClientDTO updatedClient = clientService.update(id, client);
		return ResponseEntity.ok(updatedClient);
	}
}
