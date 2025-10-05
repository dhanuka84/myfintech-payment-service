package org.myfintech.payment.repository

import org.myfintech.payment.entity.AbstractEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface BaseRepository<T : AbstractEntity<ID>, ID : Any> : JpaRepository<T, ID> {
    // You can define shared custom repository methods here if needed
}