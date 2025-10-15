package org.myfintech.payment.service.impl

import org.myfintech.payment.domain.*
import org.myfintech.payment.entity.Contract
import org.myfintech.payment.entity.Payment
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

@Service
class PaymentServiceFacadeImpl(
    private val paymentService: PaymentService,
    private val contractService: ContractService
) : PaymentServiceFacade {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(PaymentServiceFacadeImpl::class.java)
    }

    @Async
    override fun saveAsynch(trackingNumber: String, validPayments: List<PaymentDTO>) {
        val contractNumbers = validPayments.map { it.contractNumber }.toSet()

        val contractsByNumber = contractService.findAllByContractNumbers(contractNumbers)
            .map(::mapProjectionToContractEntity)
            .associateBy { it.contractNumber }

        val paymentEntities = filterOutValidEntities(validPayments, contractsByNumber)

        paymentService.saveTrackedPayments(trackingNumber, paymentEntities)
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
            dto.toEntity(contract)
        }
    }

    override fun findAll(pageable: Pageable): Page<PaymentDTO> {
        return paymentService.findAll(pageable)
    }

    override fun findById(id: Long): PaymentDTO {
        return paymentService.findById(id).toDTO()
    }

    override fun save(dto: PaymentCreateDTO): PaymentDTO {
        val contract = contractService.findByContractNumber(dto.contractNumber)
            ?: throw IllegalArgumentException("Contract not found: ${dto.contractNumber}")
        val entity = dto.toEntity(contract)
        val savedPayment = paymentService.savePayment(entity, dto.trackingNumber)
        return savedPayment.toDTO()
    }

    override fun update(id: Long, dto: PaymentDTO): PaymentDTO {
        paymentService.validate(dto)
        val payment = paymentService.findById(id)

        // Validate that the contract exists, but don't re-assign it
        contractService.findByContractNumber(dto.contractNumber)
            ?: throw IllegalArgumentException("Contract not found: ${dto.contractNumber}")

        // Apply updates to the managed entity
        payment.updateFrom(dto)

        // The transaction will commit the changes, no need to call save
        return payment.toDTO()
    }

    override fun findPaymentsByContractNumber(contractNumber: String): List<PaymentDTO> {
        return paymentService.findPaymentsByContractNumber(contractNumber)
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    override fun validatePaymentsOrFail(payments: List<PaymentDTO>) {
        payments.forEach(paymentService::validate)
    }
}