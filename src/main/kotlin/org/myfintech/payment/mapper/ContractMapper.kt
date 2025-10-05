package org.myfintech.payment.mapper

import org.mapstruct.Context
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.myfintech.payment.domain.ContractCreateDTO
import org.myfintech.payment.domain.ContractDTO
import org.myfintech.payment.entity.Client
import org.myfintech.payment.entity.Contract

@Mapper(componentModel = "spring")
interface ContractMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "id", target = "contractId")
    fun toDTO(contract: Contract): ContractDTO

    @Mapping(target = "client", source = "client")
    fun toEntity(dto: ContractCreateDTO, @Context client: Client): Contract
}