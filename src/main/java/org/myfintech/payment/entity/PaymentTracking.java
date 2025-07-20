/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true,callSuper = true)
@Entity
@Table(name = "payment_tracking")
public class PaymentTracking extends AbstractEntity<Long> {

    @Column(name = "tracking_number", nullable = false, unique = true, length = 100)
    @EqualsAndHashCode.Include
    private String trackingNumber;

    public PaymentTracking(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public PaymentTracking(Long id, String trackingNumber) {
        this.setId(id);
        this.trackingNumber = trackingNumber;
    }
}
