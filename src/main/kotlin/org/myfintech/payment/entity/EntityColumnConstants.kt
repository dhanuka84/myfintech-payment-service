package org.myfintech.payment.entity

/**
 * Utility class to hold database table and column names as constants.
 * This helps to avoid magic strings and centralize schema references.
 */
object EntityColumnConstants {
    // Common Columns (from AbstractEntity and general conventions)
    const val COMMON_ID: String = "id"
    const val COMMON_CREATED_DATE: String = "created_datetime"
    const val COMMON_MODIFIED_DATE: String = "updated_datetime"
    const val COMMON_VERSION: String = "version"

    // Client Table and Columns
    const val TABLE_CLIENT: String = "client"
    val CLIENT_ID: String = COMMON_ID
    const val CLIENT_CLIENT_NAME: String = "client_name"
    val CLIENT_CREATED_DATE: String = COMMON_CREATED_DATE
    val CLIENT_MODIFIED_DATE: String = COMMON_MODIFIED_DATE

    // Contract Table and Columns
    const val TABLE_CONTRACT: String = "contract"
    val CONTRACT_ID: String = COMMON_ID
    const val FOREIGN_KEY_CLIENT_ID: String = "client_id"
    const val CONTRACT_CONTRACT_NUMBER: String = "contract_number"
    val CONTRACT_CREATED_DATE: String = COMMON_CREATED_DATE
    val CONTRACT_MODIFIED_DATE: String = COMMON_MODIFIED_DATE

    // Payment Table and Columns
    const val TABLE_PAYMENT: String = "payment"
    val PAYMENT_ID: String = COMMON_ID
    const val PAYMENT_PAYMENT_DATE: String = "payment_date"
    const val PAYMENT_AMOUNT: String = "amount"
    const val PAYMENT_TYPE: String = "type"
    const val FOREIGN_KEY_CONTRACT_ID: String = "contract_id"
    const val FOREIGN_KEY_PAYMENT_TRACKING_ID: String = "tracking_id"
    val PAYMENT_VERSION: String = COMMON_VERSION
    val PAYMENT_CREATED_DATE: String = COMMON_CREATED_DATE
    val PAYMENT_MODIFIED_DATE: String = COMMON_MODIFIED_DATE

    // Payment Tracking Table and Columns
    const val TABLE_PAYMENT_TRACKING: String = "payment_tracking"
    val PAYMENT_TRACKING_ID: String = COMMON_ID
    const val PAYMENT_TRACKING_TRACKING_NUMBER: String = "tracking_number"
    val PAYMENT_TRACKING_CREATED_DATE: String = COMMON_CREATED_DATE
    val PAYMENT_TRACKING_MODIFIED_DATE: String = COMMON_MODIFIED_DATE
}