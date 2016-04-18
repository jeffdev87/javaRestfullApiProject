package com.jeff.ac.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class SQLErrorException extends WebApplicationException {

    /**
     * Serial version ID
     */
    private static final long serialVersionUID = -1251975830661419034L;

    /**
     * Create a HTTP 4XX (server error) exception.
     */
    public SQLErrorException() {
        super(Response.Status.FORBIDDEN);
    }

    /**
     * Create a HTTP 4XX (server error) exception.
     * @param message the String that is the entity of the response.
     */
    public SQLErrorException(String message) {
        super(Response.status(Response.Status.FORBIDDEN).
                entity(message).type(MediaType.APPLICATION_JSON).build());
    }
}