package org.myfintech.payment.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mapstruct.factory.Mappers
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.stubbing.Answer
import org.myfintech.payment.domain.PaymentCreateDTO
import org.myfintech.payment.domain.PaymentDTO
import org.myfintech.payment.entity.Client
import org.myfintech.payment.entity.Contract
import org.myfintech.payment.entity.Payment
import org.myfintech.payment.entity.PaymentTracking
import org.myfintech.payment.mapper.ContractMapper
import org.myfintech.payment.mapper.PaymentMapper
import org.myfintech.payment.repository.ContractRepository
import org.myfintech.payment.repository.PaymentRepository
import org.myfintech.payment.repository.PaymentTrackingRepository
import org.myfintech.payment.service.impl.ContractServiceImpl
import org.myfintech.payment.service.impl.PaymentServiceFacadeImpl
import org.myfintech.payment.service.impl.PaymentServiceImpl
import org.myfintech.payment.validator.CotractValidator
import org.myfintech.payment.validator.PaymentValidator
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*
import java.util.List

@ExtendWith(MockitoExtension::class)
class PaymentServiceImplTest {
    @Mock
    private val paymentRepository: PaymentRepository? = null

    @Mock
    private val contractRepository: ContractRepository? = null

    @Mock
    private val paymentTrackingRepository: PaymentTrackingRepository? = null

    @Mock
    private val clientService: ClientService? = null

    @Mock
    private val validator: PaymentValidator? = null

    @Mock
    private val contractValidator: CotractValidator? = null

    private val paymentMapper: PaymentMapper = Mappers.getMapper<PaymentMapper>(PaymentMapper::class.java)
    private val contractMapper: ContractMapper = Mappers.getMapper<ContractMapper>(ContractMapper::class.java)

    private var paymentService: PaymentServiceImpl? = null
    private var contractService: ContractServiceImpl? = null

    @InjectMocks
    private var paymentFacade: PaymentServiceFacadeImpl? = null

    private var paymentCreateDTO: PaymentCreateDTO? = null
    private var payment: Payment? = null
    private var contract: Contract? = null

    @BeforeEach
    fun setUp() {
        paymentService =
            PaymentServiceImpl(paymentRepository!!, paymentTrackingRepository!!, paymentMapper, validator!!)
        contractService =
            ContractServiceImpl(contractRepository!!, contractMapper, clientService!!, contractValidator!!)
        paymentFacade = PaymentServiceFacadeImpl(paymentService!!, contractService!!, paymentMapper)

        val now = OffsetDateTime.now()
        val client = Client(1L, now, now, "Acme")
        contract = Contract(1L, now, now, client, "12345")

        //payment = new Payment(1L, now, now, LocalDate.of(2024, 1, 30), BigDecimal.valueOf(1000.0), "incoming");
        payment = Payment()
        payment.setPaymentDate(LocalDate.of(2024, 1, 30))
        payment.setAmount(BigDecimal.valueOf(1000.0))
        payment.setType("incoming")
        payment.setContract(contract)

        paymentCreateDTO = PaymentCreateDTO("2024-01-30", 1000.0, "incoming", "12345", "tr-1")
    }

    @Test
    fun shouldReturnAllPayments() {
        val pageable: Pageable = PageRequest.of(0, 10)
        Mockito.`when`<Page<Payment?>?>(paymentRepository!!.findAllWithContract(pageable)).thenReturn(
            PageImpl<Payment?>(
                List.of<Payment?>(payment)
            )
        )

        val result = paymentFacade!!.findAll(pageable)

        Assertions.assertEquals(1, result!!.getTotalPages())
        assertEquals("12345", result.getContent().get(0).contractNumber())
    }

    @Test
    fun shouldReturnPaymentsForTheContract() {
        Mockito.`when`<MutableList<Payment?>?>(paymentRepository!!.findPaymentsByContract_ContractNumber("12345"))
            .thenReturn(
                List.of<Payment?>(payment)
            )
        val result = paymentFacade!!.findPaymentsByContractNumber("12345")
        Assertions.assertEquals(1, result!!.size)
        assertEquals("12345", result.get(0).contractNumber())
    }

    @Test
    fun shouldReturnPaymentById() {
        Mockito.`when`<Optional<Payment?>?>(paymentRepository!!.findById(1L))
            .thenReturn(Optional.of<Payment?>(payment!!))
        val result = paymentFacade!!.findById(1L)
        assertEquals("12345", result.contractNumber())
    }

    @Test
    fun shouldSavePayment() {
        val tracking: PaymentTracking = PaymentTracking()
        tracking.id = 1L
        Mockito.`when`<Any?>(paymentTrackingRepository!!.save<PaymentTracking?>(ArgumentMatchers.any<PaymentTracking?>()))
            .thenReturn(tracking)
        Mockito.`when`<Optional<Contract?>?>(contractRepository!!.findByContractNumber("12345"))
            .thenReturn(Optional.of<Contract?>(contract!!))
        Mockito.`when`<Payment?>(paymentRepository!!.save<Payment?>(ArgumentMatchers.any<Payment?>(Payment::class.java)))
            .thenAnswer(Answer { invocation: InvocationOnMock? ->
                val saved = invocation!!.getArgument<Payment>(0)
                saved.id = 99L
                saved
            })

        val result = paymentFacade!!.save(paymentCreateDTO!!)
        assertEquals("12345", result.contractNumber())
        assertEquals(1000.0, result.amount())
    }

    @Test
    fun shouldUpdatePaymentSuccessfully() {
        val id = 1L
        val existing = Payment()
        existing.id = id
        existing.setAmount(BigDecimal.valueOf(500.0))
        existing.setType("MANUAL")
        existing.setPaymentDate(LocalDate.of(2024, 1, 1))
        existing.setContract(contract)

        val updatedDTO = PaymentDTO("2024-06-01", 200.0, "AUTO", "12345")


        //when(contractService.findByContractNumber("12345")).thenReturn(Optional.of(contract));
        Mockito.`when`<Optional<Contract?>?>(contractRepository!!.findByContractNumber("12345"))
            .thenReturn(Optional.of<Contract?>(contract!!))
        Mockito.`when`<Optional<Payment?>?>(paymentRepository!!.findById(id))
            .thenReturn(Optional.of<Payment?>(existing))
        Mockito.`when`<Payment>(paymentService!!.savePayment(existing)).thenReturn(existing)

        val result = paymentFacade!!.update(id, updatedDTO)

        assertEquals("AUTO", result.type())
        assertEquals(200.0, result.amount())
        assertEquals("12345", result.contractNumber())
    }
}
