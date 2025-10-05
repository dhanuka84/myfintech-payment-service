package org.myfintech.payment.repository

import org.myfintech.payment.entity.Contract
import org.myfintech.payment.entity.projection.ContractWithClientProjection
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ContractRepository : BaseRepository<Contract, Long> {

    /**
     * Finds a contract by its unique contract number.
     * Returns a nullable Contract, which is more idiomatic in Kotlin than Optional.
     */
    fun findByContractNumber(contractNumber: String): Contract

    /**
     * Finds all contracts whose numbers are in the given set.
     */
    @Query(
        value = """
          SELECT c.id, c.contract_number,
                 cl.id as client_id,
                 cl.client_name as client_name
          FROM contract c
          JOIN client cl ON c.client_id = cl.id
          WHERE c.contract_number IN (:contractNumbers)
          """, nativeQuery = true
    )
    fun findAllByContractNumberIn(@Param("contractNumbers") contractNumbers: Set<String>): List<ContractWithClientProjection>
}