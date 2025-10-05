package org.myfintech.payment.exception

class Http400BadRequest @JvmOverloads constructor(
    message: String,
    val errors: List<String> = emptyList(),
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    companion object {
        private const val serialVersionUID = 1L
    }
}
