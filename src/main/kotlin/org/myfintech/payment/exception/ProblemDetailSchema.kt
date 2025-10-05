package org.myfintech.payment.exception

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "ProblemDetail", description = "Standard error structure")
class ProblemDetailSchema {
    @Schema(example = "about:blank")
    var type: String? = null

    @Schema(example = "Bad Request")
    var title: String? = null

    @Schema(example = "400")
    var status: Int = 0

    @Schema(example = "Validation failed")
    var detail: String? = null

    @Schema(example = "/api/v1/payments")
    var instance: String? = null

    @Schema(description = "List of validation errors")
    var errors: MutableList<MutableMap<String?, String?>?>? = null
}
