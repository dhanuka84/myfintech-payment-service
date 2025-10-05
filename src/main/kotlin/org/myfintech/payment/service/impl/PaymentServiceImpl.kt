package org.myfintech.payment.service.impl

import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.entity.Payment
import org.myfintech.payment.entity.PaymentTracking
import org.myfintech.payment.exception.Http404NotFoundException
import org.myfintech.payment.mapper.PaymentMapper
import org.myfintech.payment.repository.PaymentRepository
import org.myfintech.payment.repository.PaymentTrackingRepository
import org.myfintech.payment.service.PaymentService
import org.myfintech.payment.validator.PaymentValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PaymentServiceImpl(
    private val paymentRepository: PaymentRepository,
    private val trackingRepository: PaymentTrackingRepository,
    private val mapper: PaymentMapper,
    private val validator: PaymentValidator
) : PaymentService {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(PaymentServiceImpl::class.java)
    }

    override fun findAll(pageable: Pageable): Page<PaymentDTO> {
        return paymentRepository.findAllWithContract(pageable).map(mapper::toDTO)
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): Payment {
        return paymentRepository.findByIdOrNull(id)
            ?: throw Http404NotFoundException("Payment not found with id: $id")
    }

    @Transactional(readOnly = true)
    override fun findPaymentsByContractNumber(contractNumber: String): List<PaymentDTO> {
        return  paymentRepository.findPaymentsByContract_ContractNumber(contractNumber).map(mapper::toDTO)
    }

    @Transactional
    override fun savePayments(paymentEntities: List<Payment>): List<Payment> {
        return paymentRepository.saveAll(paymentEntities)
    }

    @Transactional
    override fun savePayment(payment: Payment): Payment {
        return paymentRepository.save(payment)
    }

    @Transactional
    override fun saveTrackedPayments(trackingNumber: String, paymentEntities: List<Payment>) {
        val paymentTracking = trackingRepository.save(PaymentTracking(trackingNumber))

        paymentEntities.forEach { it.trackingId = paymentTracking.id!! }

        paymentRepository.saveAll(paymentEntities)

        log.info("Successfully saved tracking record {} and {} of payments", trackingNumber, paymentEntities.size)
    }

    @Transactional
    override fun savePayment(payment: Payment, trackingNumber: String): Payment {
        val tracking = trackingRepository.save(PaymentTracking(trackingNumber))
        payment.trackingId = tracking.id!!
        return paymentRepository.save(payment)
    }

    @Transactional(readOnly = true)
    override fun validate(dto: PaymentDTO) {
        validator.validatePaymentRequest(dto)
    }
}