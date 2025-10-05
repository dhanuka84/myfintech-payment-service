package org.myfintech.payment.service

import org.myfintech.payment.domain.ContractCreateDTO
import org.myfintech.payment.domain.ContractDTO
import org.myfintech.payment.entity.Contract
import org.myfintech.payment.entity.projection.ContractWithClientProjection
import java.util.Optional

// ... other imports

interface ContractService {
    fun findAll(): List<ContractDTO>
    fun findById(id: Long): ContractDTO
    fun save(dto: ContractCreateDTO): ContractDTO
    fun update(id: Long, dto: ContractDTO): ContractDTO

    fun findByContractNumber(contractNumber: String): Contract
    fun findAllByContractNumbers(contractNumbers: Set<String>): List<ContractWithClientProjection>
    fun validateUpdateRequest(id: Long, contractDTO: ContractDTO)
    fun validateCreate(contractDTO: ContractCreateDTO)
}