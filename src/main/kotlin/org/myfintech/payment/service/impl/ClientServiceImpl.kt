package org.myfintech.payment.service.impl

import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.ClientDTO
import org.myfintech.payment.entity.Client
import org.myfintech.payment.exception.Http404NotFoundException
import org.myfintech.payment.exception.handler.ExceptionHandlerConstants
import org.myfintech.payment.mapper.ClientMapper
import org.myfintech.payment.repository.ClientRepository
import org.myfintech.payment.service.ClientService
import org.myfintech.payment.validator.ClientValidator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class ClientServiceImpl(
    private val repository: ClientRepository,
    private val mapper: ClientMapper,
    private val validator: ClientValidator
) : ClientService {

    override fun findAll(): List<ClientDTO> {
        return repository.findAll().map(mapper::toDTO)
    }

    override fun findById(id: Long): ClientDTO {
        val client = findEntityById(id)
        return mapper.toDTO(client)
    }

    @Transactional
    override fun save(dto: ClientCreateDTO): ClientDTO {
        val entity = mapper.toEntity(dto)
        val savedEntity = repository.save(entity)
        return mapper.toDTO(savedEntity)
    }

    @Transactional
    override fun update(id: Long, dto: ClientDTO): ClientDTO {
        val client = findEntityById(id)
        client.clientName = dto.clientName
        // The transaction will automatically save the updated entity
        return mapper.toDTO(client)
    }

    override fun findEntityById(id: Long): Client {
        return repository.findByIdOrNull(id)
            ?: throw Http404NotFoundException(ExceptionHandlerConstants.CLIENT_NOT_FOUND + id)
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    override fun validate(dto: ClientDTO) {
        validator.validateClientNameRequest(dto)
    }
}