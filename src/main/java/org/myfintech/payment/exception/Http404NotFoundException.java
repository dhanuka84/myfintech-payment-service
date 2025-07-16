package org.myfintech.payment.exception;

/**
 *
 * @author : Dhanuka Ranasinghe
 * @since : Date: 05/07/2025
 */

/**
 * @author : Dhanuka Ranasinghe
 * @since : Date: 4/12/2024
 */
public class Http404NotFoundException extends RuntimeException
{

    public Http404NotFoundException( String message )
    {
        super( message );
    }

    public Http404NotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }

}

