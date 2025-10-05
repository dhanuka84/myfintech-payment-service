package org.myfintech.payment.mapper

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.ClientDTO
import org.myfintech.payment.entity.Client

@Mapper(componentModel = "spring")
interface ClientMapper {

    @Mapping(source = "id", target = "clientId")
    fun toDTO(client: Client): ClientDTO // Ensure this returns a non-nullable ClientDTO

    fun toEntity(dto: ClientCreateDTO): Client
}