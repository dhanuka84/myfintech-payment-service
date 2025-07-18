package org.myfintech.payment.exception;

/**
 *
 * @author : Dhanuka Ranasinghe
 * @since : Date: 05/07/2025
 */
public class Http404NotFoundException extends RuntimeException
{

    private static final long serialVersionUID = 1L;

	public Http404NotFoundException( String message )
    {
        super( message );
    }

    public Http404NotFoundException( String message, Throwable cause )
    {
        super( message, cause );
    }

}

