package org.myfintech.payment.service.impl

import org.myfintech.payment.domain.*
import org.myfintech.payment.entity.Contract
import org.myfintech.payment.entity.projection.ContractWithClientProjection
import org.myfintech.payment.exception.Http404NotFoundException
import org.myfintech.payment.repository.ContractRepository
import org.myfintech.payment.service.ClientService
import org.myfintech.payment.service.ContractService
import org.myfintech.payment.validator.CotractValidator
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContractServiceImpl(
    private val repository: ContractRepository,
    private val clientService: ClientService,
    private val validator: CotractValidator
) : ContractService {

    override fun findAll(): List<ContractDTO> {
        return repository.findAll().map { it.toDTO() }
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): ContractDTO {
        return repository.findByIdOrNull(id)?.toDTO()
            ?: throw Http404NotFoundException("Contract not found: $id")
    }

    @Transactional
    override fun save(dto: ContractCreateDTO): ContractDTO {
        val client = clientService.findEntityById(dto.clientId)
        val savedEntity = repository.save(dto.toEntity(client))
        return savedEntity.toDTO()
    }

    @Transactional
    override fun update(id: Long, dto: ContractDTO): ContractDTO {
        val contract = repository.findByIdOrNull(id)
            ?: throw Http404NotFoundException("Contract not found: $id")
        contract.contractNumber = dto.contractNumber
        return contract.toDTO()
    }

    @Transactional(readOnly = true)
    override fun findByContractNumber(contractNumber: String): Contract {
        return repository.findByContractNumber(contractNumber)
    }

    @Transactional(readOnly = true)
    override fun findAllByContractNumbers(contractNumbers: Set<String>): List<ContractWithClientProjection> {
        return repository.findAllByContractNumberIn(contractNumbers)
    }

    @Transactional(readOnly = true)
    override fun validateUpdateRequest(id: Long, contractDTO: ContractDTO) {
        validator.validateUpdateRequest(id, contractDTO)
    }

    @Transactional(readOnly = true)
    override fun validateCreate(contractDTO: ContractCreateDTO) {
        validator.validateCreate(contractDTO)
    }
}