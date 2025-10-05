package org.myfintech.payment.service.impl

import org.myfintech.payment.domain.ContractCreateDTO
import org.myfintech.payment.domain.ContractDTO
import org.myfintech.payment.entity.Contract
import org.myfintech.payment.entity.projection.ContractWithClientProjection
import org.myfintech.payment.exception.Http404NotFoundException
import org.myfintech.payment.exception.handler.ExceptionHandlerConstants
import org.myfintech.payment.mapper.ContractMapper
import org.myfintech.payment.repository.ContractRepository
import org.myfintech.payment.service.ClientService
import org.myfintech.payment.service.ContractService
import org.myfintech.payment.validator.CotractValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

@Service
class ContractServiceImpl(
    private val repository: ContractRepository,
    private val mapper: ContractMapper,
    private val clientService: ClientService,
    private val validator: CotractValidator
) : ContractService {
    override fun findAll(): List<ContractDTO> {
        return repository.findAll().stream().map<ContractDTO> { contract: Contract -> mapper.toDTO(contract) }
            .toList()
    }

    @Transactional(readOnly = true)
    override fun findById(id: Long): ContractDTO {
        return repository.findById(id).map<ContractDTO>(Function { contract: Contract -> mapper.toDTO(contract) })
            .orElseThrow()
    }

    @Transactional
    override fun save(dto: ContractCreateDTO): ContractDTO {
        return mapper.toDTO(
            repository.save<Contract>(
                mapper.toEntity(
                    dto,
                    clientService.findEntityById(dto.clientId)
                )
            )
        )
    }

    @Transactional
    override fun update(id: Long, dto: ContractDTO): ContractDTO {
        val contract = repository.findById(id)
            .orElseThrow<Http404NotFoundException>(Supplier { Http404NotFoundException(ExceptionHandlerConstants.CONTRACT_NOT_FOUND + id) })
        contract.contractNumber = dto.contractNumber
        return mapper.toDTO(contract)
    }

    @Transactional(readOnly = true)
    override fun findByContractNumber(contractNumber: String): Contract {
        // The repository method returns Contract? (nullable)
        return repository.findByContractNumber(contractNumber)
            ?: throw Http404NotFoundException("Contract not found: $contractNumber")
    }

    @Transactional(readOnly = true)
    override fun findAllByContractNumbers(contractNumbers: Set<String>): List<ContractWithClientProjection> {
        return repository.findAllByContractNumberIn(contractNumbers)
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    override fun validateUpdateRequest(id: Long, contractDTO: ContractDTO) {
        validator.validateUpdateRequest(id, contractDTO)
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    override fun validateCreate(contractDTO: ContractCreateDTO) {
        validator.validateCreate(contractDTO)
    }
}
