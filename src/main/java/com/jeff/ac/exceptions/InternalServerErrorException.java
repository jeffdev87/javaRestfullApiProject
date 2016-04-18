package com.jeff.ac.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class InternalServerErrorException extends WebApplicationException {

    /**
     * Serial version ID
     */
    private static final long serialVersionUID = -1251975830661419034L;

    /**
     * Create a HTTP 5XX (server error) exception.
     */
    public InternalServerErrorException() {
        super(Response.Status.INTERNAL_SERVER_ERROR);
    }

    /**
     * Create a HTTP 5XX (server error) exception.
     * @param message the String that is the entity of the response.
     */
    public InternalServerErrorException(String message) {
        super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                entity(message).type(MediaType.APPLICATION_JSON).build());
    }
}