
package org.myfintech.payment.validator;

import static org.myfintech.payment.validator.PaymentValidationConstants.CONTRACT_NUMBER_FORMAT;
import static org.myfintech.payment.validator.PaymentValidationConstants.CONTRACT_NUMBER_REQUIRED;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class CommonValidations {

	private CommonValidations() {
	} // utility class

	@Documented
	@Constraint(validatedBy = {})
	@Target({ ElementType.PARAMETER, ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	@NotNull
	@Min(1)
	@Max(999999999999999999L)
	public @interface ValidId {
		String message() default "Invalid ID. Must be between 1 and 20 digits.";

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}

	@Documented
	@Constraint(validatedBy = {})
	@Target({ ElementType.PARAMETER, ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	@NotBlank(message = CONTRACT_NUMBER_REQUIRED)
	@Size(min = 3, max = 30)
	@ReportAsSingleViolation
	public @interface ValidContractNumber {
		String message() default CONTRACT_NUMBER_REQUIRED+" "+CONTRACT_NUMBER_FORMAT;

		Class<?>[] groups() default {};

		Class<? extends Payload>[] payload() default {};
	}

	// Add more @interface validations here as needed

}
