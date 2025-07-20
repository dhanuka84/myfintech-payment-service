/*
 * Copyright (c) 2024 MyFintech Payment Service
 * All rights reserved.
 *
 * This software is proprietary and confidential. Unauthorized copying of this file,
 * via any medium, is strictly prohibited.
 */
package org.myfintech.payment.validator;

/**
 * Payment validation constants for use in annotations.
 * All values are compile-time constants that can be used in annotation attributes.
 *
 * @author Dhanuka Ranasinghe
 * @since 11/07/2025
 */
public final class PaymentValidationConstants {
    
    private PaymentValidationConstants() {
        // Prevent instantiation
    }
    
    // ================== REGEX PATTERNS ==================
    // Date patterns
    public static final String DATE_YYYY_MM_DD_PATTERN = "\\d{4}-\\d{2}-\\d{2}";
    
    // Contract patterns
    public static final String CONTRACT_NUMBER_PATTERN = "[A-Z0-9]{3,20}";
    
    // Tracking number patterns
    public static final String TRACKING_NUMBER_PATTERN = "[A-Z0-9]{8,16}";
    
    // Payment type patterns
    public static final String PAYMENT_TYPE_PATTERN = "(CREDIT|DEBIT|TRANSFER|REFUND)";
    
    // Amount patterns
    public static final String AMOUNT_DECIMAL_PATTERN = "\\d+(\\.\\d{1,2})?";
    
    // Currency patterns
    public static final String CURRENCY_CODE_PATTERN = "[A-Z]{3}";
    
    // ================== VALIDATION MESSAGES ==================
    // Required field messages
    public static final String PAYMENT_DATE_REQUIRED = "Payment date is required";
    public static final String TYPE_REQUIRED = "Type is required";
    public static final String CONTRACT_NUMBER_REQUIRED = "Contract number is required";
    public static final String CONTRACT_ID_REQUIRED = "ContractId is required";
    public static final String TRACKING_NUMBER_REQUIRED = "Tracking number is required";
    public static final String AMOUNT_REQUIRED = "Amount is required";
    public static final String CURRENCY_REQUIRED = "Currency is required";
    public static final String CLIENT_ID_REQUIRED = "Client ID is required";
    public static final String CLIENT_NAME_REQUIRED = "Client name is required";
    
    // Format messages
    public static final String PAYMENT_DATE_FORMAT = "Payment date must be in yyyy-MM-dd format";
    public static final String CONTRACT_NUMBER_FORMAT = "Contract number must be 3-20 alphanumeric characters";
    public static final String TRACKING_NUMBER_FORMAT = "Tracking number must be 8-16 alphanumeric characters";
    public static final String PAYMENT_TYPE_FORMAT = "Payment type must be one of: CREDIT, DEBIT, TRANSFER, REFUND";
    public static final String CURRENCY_CODE_FORMAT = "Currency code must be 3 uppercase letters (ISO 4217)";
    
    // Value constraint messages
    public static final String AMOUNT_POSITIVE = "Amount must be greater than zero";
    public static final String AMOUNT_MIN = "Amount must be at least ";
    public static final String AMOUNT_MAX = "Amount must not exceed ";
    
    // ================== VALIDATION GROUPS ==================
    // Can be used for grouped validation
    public interface CreateValidation {}
    public interface UpdateValidation {}
    public interface FullValidation extends CreateValidation, UpdateValidation {}
}