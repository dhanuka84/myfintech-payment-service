package org.myfintech.payment.domain

import org.myfintech.payment.entity.Client
import org.myfintech.payment.entity.Contract
import org.myfintech.payment.entity.Payment
import org.myfintech.payment.entity.projection.ContractWithClientProjection
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

// --- Client Mappers ---

fun Client.toDTO(): ClientDTO = ClientDTO(
    clientId = this.id!!,
    clientName = this.clientName
)

fun ClientCreateDTO.toEntity(): Client = Client(
    clientName = this.clientName
)

// --- Contract Mappers ---

fun Contract.toDTO(): ContractDTO = ContractDTO(
    contractId = this.id!!,
    clientId = this.client.id!!,
    contractNumber = this.contractNumber
)

fun ContractCreateDTO.toEntity(client: Client): Contract = Contract(
    client = client,
    contractNumber = this.contractNumber
)

// --- Payment Mappers ---

fun Payment.toDTO(): PaymentDTO = PaymentDTO(
    paymentDate = this.paymentDate.format(DATE_FORMATTER),
    amount = this.amount.toDouble(),
    type = this.type,
    contractNumber = this.contract.contractNumber // Assumes contract is eagerly fetched or joined
)

fun PaymentCreateDTO.toEntity(contract: Contract): Payment = Payment(
    paymentDate = LocalDate.parse(this.paymentDate, DATE_FORMATTER),
    amount = BigDecimal.valueOf(this.amount),
    type = this.type,
    contractId = contract.id!!,
    trackingId = 0L // Placeholder, will be set by the service
).apply {
    this.contract = contract
}

fun PaymentDTO.toEntity(contract: Contract): Payment = Payment(
    paymentDate = LocalDate.parse(this.paymentDate, DATE_FORMATTER),
    amount = BigDecimal.valueOf(this.amount),
    type = this.type,
    contractId = contract.id!!,
    trackingId = 0L // Placeholder, will be set by the service
).apply {
    this.contract = contract
}


// --- Projection Mapper ---

fun mapProjectionToContractEntity(projection: ContractWithClientProjection): Contract {
    val client = Client(id = projection.clientId, clientName = projection.clientName)
    return Contract(
        id = projection.id,
        contractNumber = projection.contractNumber,
        client = client
    )
}

// --- Updater Extension Function ---

fun Payment.updateFrom(dto: PaymentDTO) {
    this.paymentDate = LocalDate.parse(dto.paymentDate, DATE_FORMATTER)
    this.amount = BigDecimal.valueOf(dto.amount)
    this.type = dto.type
}