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
import static org.myfintech.payment.entity.EntityColumnConstants.CONTRACT_CONTRACT_NUMBER;
import static org.myfintech.payment.entity.EntityColumnConstants.FOREIGN_KEY_CLIENT_ID;
import static org.myfintech.payment.entity.EntityColumnConstants.TABLE_CONTRACT;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true,callSuper = true)
@Entity
@Table(name = TABLE_CONTRACT)
public class Contract extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = FOREIGN_KEY_CLIENT_ID)
    private Client client;

    @EqualsAndHashCode.Include
    @Column(name = CONTRACT_CONTRACT_NUMBER, nullable = false)
    private String contractNumber;

    public Contract(
            Long id,
            OffsetDateTime createdDate,
            OffsetDateTime modifiedDate,
            Client client,
            String contractNumber) {
        this.setId(id);
        this.setCreatedDate(createdDate);
        this.setModifiedDate(modifiedDate);
        this.client = client;
        this.contractNumber = contractNumber;
    }
}
