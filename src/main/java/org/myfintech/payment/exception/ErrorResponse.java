package org.myfintech.payment.exception;

/**
 *
 * @author : Dhanuka Ranasinghe
 * @since : Date: 05/07/2025
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Dhanuka Ranasinghe
 * @since : Date: 4/12/2024
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse
{
    private String title;
    private String errorMsg;
    private HttpStatus responseStatus;
    private List<String> errors;

    public ErrorResponse (String title, String errorMsg, HttpStatus responseStatus)
    {
        this.title = title;
        this.errorMsg = errorMsg;
        this.responseStatus = responseStatus;
        this.errors = new ArrayList<>();
    }

}

