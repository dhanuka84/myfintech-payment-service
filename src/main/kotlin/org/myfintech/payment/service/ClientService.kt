package org.myfintech.payment.service

import org.myfintech.payment.domain.ClientCreateDTO
import org.myfintech.payment.domain.ClientDTO
import org.myfintech.payment.entity.Client

interface ClientService {
    fun findAll(): List<ClientDTO>
    fun findById(id: Long): ClientDTO
    fun findEntityById(id: Long): Client
    fun save(dto: ClientCreateDTO): ClientDTO
    fun update(id: Long, dto: ClientDTO): ClientDTO
    fun validate(dto: ClientDTO)
}
