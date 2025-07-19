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
import static org.myfintech.payment.entity.EntityColumnConstants.CLIENT_CLIENT_NAME;
import static org.myfintech.payment.entity.EntityColumnConstants.TABLE_CLIENT;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = TABLE_CLIENT)
public class Client extends AbstractEntity<Long> {

	@EqualsAndHashCode.Include
	@Column(name = CLIENT_CLIENT_NAME, nullable = false)
    private String clientName;

    public Client(
            Long id, OffsetDateTime createdDate, OffsetDateTime modifiedDate, String clientName) {
        this.setId(id);
        this.setCreatedDate(createdDate);
        this.setModifiedDate(modifiedDate);
        this.clientName = clientName;
    }

}
