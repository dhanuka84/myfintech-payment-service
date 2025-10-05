/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.entity

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = EntityColumnConstants.TABLE_CONTRACT)
class Contract(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = EntityColumnConstants.FOREIGN_KEY_CLIENT_ID)
    var client: Client,

    @Column(name = EntityColumnConstants.CONTRACT_CONTRACT_NUMBER, nullable = false)
    var contractNumber: String

) : AbstractEntity<Long>() {

    /**
     * Secondary constructor for convenience, often used in testing or mapping.
     */
    constructor(
        id: Long,
        createdDate: OffsetDateTime,
        modifiedDate: OffsetDateTime,
        client: Client,
        contractNumber: String
    ) : this(client, contractNumber) {
        this.id = id
        this.createdDate = createdDate
        this.modifiedDate = modifiedDate
    }

    constructor(
        id: Long,
        client: Client,
        contractNumber: String
    ) : this(client, contractNumber) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Contract) return false
        // Call super for `id` comparison
        if (!super.equals(other)) return false

        // Compare the explicitly included field
        return contractNumber == other.contractNumber
    }

    override fun hashCode(): Int {
        // Start with the superclass's hash code (based on `id`)
        var result = super.hashCode()
        // Combine with the explicitly included field's hash code
        result = 31 * result + contractNumber.hashCode()
        return result
    }
}