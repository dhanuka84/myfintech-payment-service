package org.myfintech.payment.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.ReportAsSingleViolation
import kotlin.reflect.KClass

class CommonValidations private constructor() // utility class
{
    @MustBeDocumented
    @Constraint(validatedBy = [])
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
    @Retention(
        AnnotationRetention.RUNTIME
    )
    annotation class ValidId(
        val message: String = "Invalid ID. Must be between 1 and 20 digits.",
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
    )

    @MustBeDocumented
    @Constraint(validatedBy = [])
    @Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
    @Retention(
        AnnotationRetention.RUNTIME
    )
    @ReportAsSingleViolation
    annotation class ValidContractNumber(
        val message: String = PaymentValidationConstants.CONTRACT_NUMBER_REQUIRED + " " + PaymentValidationConstants.CONTRACT_NUMBER_FORMAT,
        val groups: Array<KClass<*>> = [],
        val payload: Array<KClass<out Payload>> = []
    ) // Add more @interface validations here as needed
}
