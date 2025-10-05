package org.myfintech.payment.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mapstruct.factory.Mappers
import org.mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.myfintech.payment.domain.ContractCreateDTO
import org.myfintech.payment.domain.ContractDTO
import org.myfintech.payment.entity.Client
import org.myfintech.payment.entity.Contract
import org.myfintech.payment.mapper.ContractMapper
import org.myfintech.payment.repository.ContractRepository
import org.myfintech.payment.service.impl.ContractServiceImpl
import org.myfintech.payment.validator.CotractValidator
import java.time.OffsetDateTime
import java.util.*
import java.util.List

@ExtendWith(MockitoExtension::class)
class ContractServiceImplTest {
    @Mock
    private val contractRepository: ContractRepository? = null

    @Mock
    private val clientService: ClientService? = null

    @Mock
    private val validator: CotractValidator? = null

    @InjectMocks
    private var contractService: ContractServiceImpl? = null

    @Spy
    private val contractMapper: ContractMapper = Mappers.getMapper<ContractMapper>(ContractMapper::class.java)

    private var contract: Contract? = null
    private var contractDTO: ContractDTO? = null
    private var contractCreateDTO: ContractCreateDTO? = null
    private var client: Client? = null

    @BeforeEach
    fun setUp() {
        contractService = Mockito.spy<ContractServiceImpl>(
            ContractServiceImpl(
                contractRepository!!,
                contractMapper,
                clientService!!,
                validator!!
            )
        )
        val now = OffsetDateTime.now()
        client = Client(1L, now, now, "Acme")
        contract = Contract(1L, now, now, client, "12345")
        contractDTO = ContractDTO(1L, 1L, "12345")
        contractCreateDTO = ContractCreateDTO(1L, "12345")
    }

    @Test
    fun shouldReturnAllContracts() {
        Mockito.`when`<MutableList<Contract?>?>(contractRepository!!.findAll()).thenReturn(List.of<Contract?>(contract))
        val result = contractService!!.findAll()
        Assertions.assertEquals(1, result.size)
        assertEquals("12345", result.get(0).contractNumber())
    }

    @Test
    fun shouldReturnContractById() {
        Mockito.`when`<Optional<Contract?>?>(contractRepository!!.findById(1L))
            .thenReturn(Optional.of<Contract?>(contract!!))
        val result = contractService!!.findById(1L)
        assertEquals("12345", result.contractNumber())
    }

    @Test
    fun shouldSaveContract() {
        Mockito.`when`<Client?>(clientService!!.findEntityById(1L)).thenReturn(client)
        Mockito.`when`<Any?>(contractRepository!!.save<Contract?>(ArgumentMatchers.any<Contract?>()))
            .thenReturn(contract)
        val result = contractService!!.save(contractCreateDTO!!)
        assertEquals("12345", result.contractNumber())
    }

    @Test
    fun shouldUpdateContract() {
        Mockito.`when`<Optional<Contract?>?>(contractRepository!!.findById(1L))
            .thenReturn(Optional.of<Contract?>(contract!!))
        val result = contractService!!.update(1L, contractDTO!!)
        assertEquals("12345", result.contractNumber())
    }
}
