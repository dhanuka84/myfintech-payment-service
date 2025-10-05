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

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = EntityColumnConstants.TABLE_CLIENT)
class Client(
    @Column(name = EntityColumnConstants.CLIENT_CLIENT_NAME, nullable = false)
    var clientName: String

) : AbstractEntity<Long>() {

    /**
     * Secondary constructor to initialize entity with id and audit dates.
     * Useful for testing or mapping from projections.
     */
    constructor(id: Long, createdDate: OffsetDateTime, modifiedDate: OffsetDateTime, clientName: String) : this(clientName) {
        this.id = id
        this.createdDate = createdDate
        this.modifiedDate = modifiedDate
    }

    constructor(id: Long, clientName: String) : this(clientName) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Client) return false
        // Call super for `id` comparison from AbstractEntity
        if (!super.equals(other)) return false

        // Compare the explicitly included field
        return clientName == other.clientName
    }

    override fun hashCode(): Int {
        // Start with the superclass's hash code (which is based on `id`)
        var result = super.hashCode()
        // Combine with the explicitly included field's hash code
        result = 31 * result + clientName.hashCode()
        return result
    }
}