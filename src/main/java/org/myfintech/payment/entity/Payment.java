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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true,callSuper = true)
@Entity
@Table(name = "payment")
public class Payment extends AbstractEntity<Long> {

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @NotNull private LocalDate paymentDate;

    @NotNull
    @Positive
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 50)
    @EqualsAndHashCode.Include
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_id", insertable = false, updatable = false)
    private PaymentTracking paymentTracking;

    @Column(name = "contract_id", nullable = false)
    @EqualsAndHashCode.Include
    private Long contractId;

    @Column(name = "tracking_id", nullable = false)
    @EqualsAndHashCode.Include
    private Long trackingId;
}
