package org.myfintech.payment.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.toDTO
import org.myfintech.payment.domain.toEntity
import org.myfintech.payment.entity.Client
import org.myfintech.payment.repository.ClientRepository
import org.myfintech.payment.service.impl.ClientServiceImpl
import org.myfintech.payment.validator.ClientValidator
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ClientServiceImplTest {

    @Mock
    private lateinit var clientRepository: ClientRepository

    @Mock
    private lateinit var validator: ClientValidator

    @InjectMocks
    private lateinit var clientService: ClientServiceImpl

    @Test
    fun `should return all clients`() {
        val client = Client("Acme").apply { id = 1L }
        whenever(clientRepository.findAll()).thenReturn(listOf(client))

        val result = clientService.findAll()

        assertEquals(1, result.size)
        assertEquals("Acme", result[0].clientName)
    }

    @Test
    fun `should save and return client`() {
        val createDto = ClientCreateDTO("New Corp")
        val entity = createDto.toEntity()
        whenever(clientRepository.save(entity)).thenReturn(entity.apply { id = 1L })

        val result = clientService.save(createDto)

        assertEquals("New Corp", result.clientName)
    }

    @Test
    fun `should update existing client`() {
        val existingClient = Client("Old Name").apply { id = 1L }
        val dto = existingClient.toDTO().copy(clientName = "New Name")
        whenever(clientRepository.findByIdOrNull(1L)).thenReturn(existingClient)

        val result = clientService.update(1L, dto)

        assertEquals("New Name", result.clientName)
        assertEquals(1L, result.clientId)
    }
}