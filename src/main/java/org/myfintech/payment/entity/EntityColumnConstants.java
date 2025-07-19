package org.myfintech.payment.entity;

/**
 * Utility class to hold database table and column names as constants.
 * This helps to avoid magic strings and centralize schema references.
 */
public final class EntityColumnConstants {

    private EntityColumnConstants() {
        // Prevent instantiation
    }

    // Common Columns (from AbstractEntity and general conventions)
    public static final String COMMON_ID = "id";
    public static final String COMMON_CREATED_DATE = "created_datetime";
    public static final String COMMON_MODIFIED_DATE = "updated_datetime";
    public static final String COMMON_VERSION = "version";

    // Client Table and Columns
    public static final String TABLE_CLIENT = "client";
    public static final String CLIENT_ID = COMMON_ID;
    public static final String CLIENT_CLIENT_NAME = "client_name";
    public static final String CLIENT_CREATED_DATE = COMMON_CREATED_DATE;
    public static final String CLIENT_MODIFIED_DATE = COMMON_MODIFIED_DATE;

    // Contract Table and Columns
    public static final String TABLE_CONTRACT = "contract";
    public static final String CONTRACT_ID = COMMON_ID;
    public static final String FOREIGN_KEY_CLIENT_ID = "client_id";
    public static final String CONTRACT_CONTRACT_NUMBER = "contract_number";
    public static final String CONTRACT_CREATED_DATE = COMMON_CREATED_DATE;
    public static final String CONTRACT_MODIFIED_DATE = COMMON_MODIFIED_DATE;

    // Payment Table and Columns
    public static final String TABLE_PAYMENT = "payment";
    public static final String PAYMENT_ID = COMMON_ID;
    public static final String PAYMENT_PAYMENT_DATE = "payment_date";
    public static final String PAYMENT_AMOUNT = "amount";
    public static final String PAYMENT_TYPE = "type";
    public static final String FOREIGN_KEY_CONTRACT_ID = "contract_id";
    public static final String FOREIGN_KEY_PAYMENT_TRACKING_ID = "tracking_id";
    public static final String PAYMENT_VERSION = COMMON_VERSION;
    public static final String PAYMENT_CREATED_DATE = COMMON_CREATED_DATE;
    public static final String PAYMENT_MODIFIED_DATE = COMMON_MODIFIED_DATE;

    // Payment Tracking Table and Columns
    public static final String TABLE_PAYMENT_TRACKING = "payment_tracking";
    public static final String PAYMENT_TRACKING_ID = COMMON_ID;
    public static final String PAYMENT_TRACKING_TRACKING_NUMBER = "tracking_number";
    public static final String PAYMENT_TRACKING_CREATED_DATE = COMMON_CREATED_DATE;
    public static final String PAYMENT_TRACKING_MODIFIED_DATE = COMMON_MODIFIED_DATE;
}