package org.myfintech.payment.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mapstruct.factory.Mappers
import org.mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.ClientDTO
import org.myfintech.payment.entity.Client
import org.myfintech.payment.mapper.ClientMapper
import org.myfintech.payment.repository.ClientRepository
import org.myfintech.payment.service.impl.ClientServiceImpl
import org.myfintech.payment.validator.ClientValidator
import java.time.OffsetDateTime
import java.util.*
import java.util.List

@ExtendWith(MockitoExtension::class)
class ClientServiceImplTest {
    @Mock
    private val clientRepository: ClientRepository? = null

    @Mock
    private val restValidator: ClientValidator? = null

    @Spy
    private val clientMapper: ClientMapper? = Mappers.getMapper<ClientMapper?>(ClientMapper::class.java)

    @InjectMocks
    private val clientService: ClientServiceImpl? = null

    private var client: Client? = null
    private var clientDTO: ClientDTO? = null
    private var clientCreateDTO: ClientCreateDTO? = null

    @BeforeEach
    fun setUp() {
        val now = OffsetDateTime.now()
        client = Client(1L, now, now, "Acme")
        clientDTO = ClientDTO(1L, "Acme")
        clientCreateDTO = ClientCreateDTO("Acme")
    }

    @Test
    fun shouldReturnAllClients() {
        Mockito.`when`<MutableList<Client?>?>(clientRepository!!.findAll()).thenReturn(List.of<Client?>(client))
        val result = clientService!!.findAll()
        Assertions.assertEquals(1, result.size)
        assertEquals("Acme", result.get(0).clientName())
    }

    @Test
    fun shouldReturnClientById() {
        Mockito.`when`<Optional<Client?>?>(clientRepository!!.findById(1L)).thenReturn(Optional.of<Client?>(client!!))
        val result = clientService!!.findById(1L)
        assertEquals("Acme", result.clientName())
    }

    @Test
    fun shouldSaveClient() {
        Mockito.`when`<Any?>(clientRepository!!.save<Client?>(ArgumentMatchers.any<Client?>())).thenReturn(client)
        val result = clientService!!.save(clientCreateDTO)
        assertEquals("Acme", result.clientName())
    }

    @Test
    fun shouldUpdateClient() {
        Mockito.`when`<Optional<Client?>?>(clientRepository!!.findById(1L)).thenReturn(Optional.of<Client?>(client!!))
        val result = clientService!!.update(1L, clientDTO!!)
        assertEquals("Acme", result.clientName())
    }
}
