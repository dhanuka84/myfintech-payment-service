package org.myfintech.payment.service.impl

import org.myfintech.payment.domain.*
import org.myfintech.payment.entity.Client
import org.myfintech.payment.exception.Http404NotFoundException
import org.myfintech.payment.repository.ClientRepository
import org.myfintech.payment.service.ClientService
import org.myfintech.payment.validator.ClientValidator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClientServiceImpl(
    private val repository: ClientRepository,
    private val validator: ClientValidator
) : ClientService {

    override fun findAll(): List<ClientDTO> {
        return repository.findAll().map { it.toDTO() }
    }

    override fun findById(id: Long): ClientDTO {
        return findEntityById(id).toDTO()
    }

    @Transactional
    override fun save(dto: ClientCreateDTO): ClientDTO {
        val savedEntity = repository.save(dto.toEntity())
        return savedEntity.toDTO()
    }

    @Transactional
    override fun update(id: Long, dto: ClientDTO): ClientDTO {
        val client = findEntityById(id)
        client.clientName = dto.clientName
        return client.toDTO()
    }

    override fun findEntityById(id: Long): Client {
        return repository.findByIdOrNull(id)
            ?: throw Http404NotFoundException("Client not found: $id")
    }

    @Transactional(readOnly = true)
    override fun validate(dto: ClientDTO) {
        validator.validateClientNameRequest(dto)
    }
}