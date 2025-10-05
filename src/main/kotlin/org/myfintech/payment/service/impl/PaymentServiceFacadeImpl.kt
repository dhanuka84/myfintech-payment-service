package org.myfintech.payment.service.impl

import org.myfintech.payment.domain.PaymentCreateDTO
import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.entity.Client
import org.myfintech.payment.entity.Contract
import org.myfintech.payment.entity.Payment
import org.myfintech.payment.entity.projection.ContractWithClientProjection
import org.myfintech.payment.mapper.PaymentMapper
import org.myfintech.payment.service.ContractService
import org.myfintech.payment.service.PaymentService
import org.myfintech.payment.service.PaymentServiceFacade
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class PaymentServiceFacadeImpl(
    private val paymentService: PaymentService,
    private val contractService: ContractService,
    private val mapper: PaymentMapper
) : PaymentServiceFacade {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(PaymentServiceFacadeImpl::class.java)
        private val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    }

    @Async
    override fun saveAsynch(trackingNumber: String, validPayments: List<PaymentDTO>) {
        val contractNumbers = validPayments.map { it.contractNumber }.toSet()

        val contractsByNumber = contractService.findAllByContractNumbers(contractNumbers)
            .map(::mapToContractEntity)
            .associateBy { it.contractNumber }

        val paymentEntities = filterOutValidEntities(validPayments, contractsByNumber)

        paymentService.saveTrackedPayments(trackingNumber, paymentEntities)
    }

    private fun mapToContractEntity(projection: ContractWithClientProjection): Contract {
        val client = Client(id = projection.clientId, clientName = projection.clientName)
        return Contract(
            id = projection.id,
            contractNumber = projection.contractNumber,
            client = client
        )
    }

    private fun filterOutValidEntities(
        validPayments: List<PaymentDTO>,
        contractsByNumber: Map<String, Contract>
    ): List<Payment> {
        return validPayments.map { dto ->
            val contract = contractsByNumber[dto.contractNumber]
                ?: run {
                    log.error("Contract not found for number: {}", dto.contractNumber)
                    throw IllegalArgumentException("Contract not found: ${dto.contractNumber}")
                }
            mapper.toEntity(dto, contract)
        }
    }

    override fun findAll(pageable: Pageable): Page<PaymentDTO> {
        return paymentService.findAll(pageable)
    }

    override fun findById(id: Long): PaymentDTO {
        return mapper.toDTO(paymentService.findById(id))
    }

    @Transactional
    override fun save(dto: PaymentCreateDTO): PaymentDTO {
        val contract = contractService.findByContractNumber(dto.contractNumber)
            ?: throw IllegalArgumentException("Contract not found: ${dto.contractNumber}")
        val entity = mapper.toEntity(dto, contract)
        val savedPayment = paymentService.savePayment(entity, dto.trackingNumber)
        return mapper.toDTO(savedPayment)
    }

    @Transactional
    override fun update(id: Long, dto: PaymentDTO): PaymentDTO {
        paymentService.validate(dto)
        val payment = paymentService.findById(id)

        // Validate that the contract exists
        contractService.findByContractNumber(dto.contractNumber)
            ?: throw IllegalArgumentException("Contract not found: ${dto.contractNumber}")

        // Apply updates directly to the managed entity
        payment.paymentDate = LocalDate.parse(dto.paymentDate, DATE_FORMATTER)
        payment.amount = BigDecimal.valueOf(dto.amount)
        payment.type = dto.type

        val savedPayment = paymentService.savePayment(payment)
        return mapper.toDTO(savedPayment)
    }

    override fun findPaymentsByContractNumber(contractNumber: String): List<PaymentDTO> {
        return paymentService.findPaymentsByContractNumber(contractNumber)
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    override fun validatePaymentsOrFail(payments: List<PaymentDTO>) {
        payments.forEach(paymentService::validate)
    }
}