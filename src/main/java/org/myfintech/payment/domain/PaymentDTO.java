/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 * @author : Dhanuka Ranasinghe
 * @since : Date: 11/07/2025
 */
package org.myfintech.payment.domain;

import static org.myfintech.payment.validator.PaymentValidationConstants.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "payment")
@XmlAccessorType(XmlAccessType.FIELD)
public record PaymentDTO(
		@XmlElement(name = "payment_date") @NotBlank(message = PAYMENT_DATE_REQUIRED) @Pattern(regexp = DATE_YYYY_MM_DD_PATTERN, message = PAYMENT_DATE_FORMAT) String paymentDate,
		@XmlElement @Positive(message = AMOUNT_POSITIVE) double amount,
		@XmlElement @NotBlank(message = TYPE_REQUIRED) String type,
		@XmlElement(name = "contract_number") @NotBlank(message = CONTRACT_NUMBER_REQUIRED) String contractNumber

) {
	public static PaymentDTO of(String paymentDate, double amount, String type, String contractNumber) {
		return new PaymentDTO(paymentDate, amount, type, contractNumber);
	}
}
