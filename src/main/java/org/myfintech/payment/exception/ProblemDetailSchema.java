package org.myfintech.payment.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(name = "ProblemDetail", description = "Standard error structure")
public class ProblemDetailSchema {
    @Schema(example = "about:blank") public String type;
    @Schema(example = "Bad Request") public String title;
    @Schema(example = "400") public int status;
    @Schema(example = "Validation failed") public String detail;
    @Schema(example = "/api/v1/payments") public String instance;
    @Schema(description = "List of validation errors") public List<Map<String, String>> errors;
}
