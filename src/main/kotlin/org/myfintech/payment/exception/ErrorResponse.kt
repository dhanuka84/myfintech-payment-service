package org.myfintech.payment.exception

import org.springframework.http.HttpStatus

data class ErrorResponse(
    val title: String,
    val errorMsg: String,
    val responseStatus: HttpStatus,
    val errors: List<String> = emptyList() // Defaults to an empty list
)