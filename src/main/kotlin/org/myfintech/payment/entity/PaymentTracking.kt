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

@Entity
@Table(name = "payment_tracking")
class PaymentTracking(
    @Column(name = "tracking_number", nullable = false, unique = true, length = 100)
    var trackingNumber: String

) : AbstractEntity<Long>() {

    /**
     * Secondary constructor for convenience, e.g., for test data setup.
     */
    constructor(id: Long, trackingNumber: String) : this(trackingNumber) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PaymentTracking) return false
        // Call super for `id` comparison
        if (!super.equals(other)) return false

        // Compare the explicitly included field
        return trackingNumber == other.trackingNumber
    }

    override fun hashCode(): Int {
        // Start with the superclass's hash code (based on `id`)
        var result = super.hashCode()
        // Combine with the explicitly included field's hash code
        result = 31 * result + trackingNumber.hashCode()
        return result
    }
}