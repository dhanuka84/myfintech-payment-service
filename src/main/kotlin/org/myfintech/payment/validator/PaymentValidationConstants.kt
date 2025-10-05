/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 */
package org.myfintech.payment.validator

/**
 * Payment validation constants for use in annotations.
 * All values are compile-time constants that can be used in annotation attributes.
 *
 * @author Dhanuka Ranasinghe
 * @since 11/07/2025
 */
object PaymentValidationConstants {
    // ================== REGEX PATTERNS ==================
    // Date patterns
    const val DATE_YYYY_MM_DD_PATTERN: String = "\\d{4}-\\d{2}-\\d{2}"

    // Contract patterns
    const val CONTRACT_NUMBER_PATTERN: String = "[A-Z0-9]{3,20}"

    // Tracking number patterns
    const val TRACKING_NUMBER_PATTERN: String = "[A-Z0-9]{8,16}"

    // Payment type patterns
    const val PAYMENT_TYPE_PATTERN: String = "(CREDIT|DEBIT|TRANSFER|REFUND)"

    // Amount patterns
    const val AMOUNT_DECIMAL_PATTERN: String = "\\d+(\\.\\d{1,2})?"

    // Currency patterns
    const val CURRENCY_CODE_PATTERN: String = "[A-Z]{3}"

    // ================== VALIDATION MESSAGES ==================
    // Required field messages
    const val PAYMENT_DATE_REQUIRED: String = "Payment date is required"
    const val TYPE_REQUIRED: String = "Type is required"
    const val CONTRACT_NUMBER_REQUIRED: String = "Contract number is required"
    const val CONTRACT_ID_REQUIRED: String = "ContractId is required"
    const val TRACKING_NUMBER_REQUIRED: String = "Tracking number is required"
    const val AMOUNT_REQUIRED: String = "Amount is required"
    const val CURRENCY_REQUIRED: String = "Currency is required"
    const val CLIENT_ID_REQUIRED: String = "Client ID is required"
    const val CLIENT_NAME_REQUIRED: String = "Client name is required"

    // Format messages
    const val PAYMENT_DATE_FORMAT: String = "Payment date must be in yyyy-MM-dd format"
    const val CONTRACT_NUMBER_FORMAT: String = "Contract number must be 3-20 alphanumeric characters"
    const val TRACKING_NUMBER_FORMAT: String = "Tracking number must be 8-16 alphanumeric characters"
    const val PAYMENT_TYPE_FORMAT: String = "Payment type must be one of: CREDIT, DEBIT, TRANSFER, REFUND"
    const val CURRENCY_CODE_FORMAT: String = "Currency code must be 3 uppercase letters (ISO 4217)"

    // Value constraint messages
    const val AMOUNT_POSITIVE: String = "Amount must be greater than zero"
    const val AMOUNT_MIN: String = "Amount must be at least "
    const val AMOUNT_MAX: String = "Amount must not exceed "

    // ================== VALIDATION GROUPS ==================
    // Can be used for grouped validation
    interface CreateValidation
    interface UpdateValidation
    interface FullValidation : CreateValidation, UpdateValidation
}