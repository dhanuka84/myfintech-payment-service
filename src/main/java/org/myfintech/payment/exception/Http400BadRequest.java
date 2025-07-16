package org.myfintech.payment.exception;

/**
 *
 * @author : Dhanuka Ranasinghe
 * @since : Date: 05/07/2025
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author : Dhanuka Ranasinghe
 * @since : Date: 4/12/2024
 */
public class Http400BadRequest extends RuntimeException
{
    private List<String> errors;
    public Http400BadRequest( String message )
    {
        super( message );
    }

    public Http400BadRequest( String message, List<String> errors )
    {
        super( message );
        this.errors = errors;
    }

    public Http400BadRequest( String message, Throwable cause )
    {
        super( message, cause );
    }

    public List<String> getErrors()
    {
        if(errors == null)
        {
            new ArrayList<>();
        }
        return errors;
    }
}
