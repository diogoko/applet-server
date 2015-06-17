package com.github.diogoko;

import org.glassfish.jersey.spi.ExtendedExceptionMapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UncaughtThrowableExceptionMapper implements ExtendedExceptionMapper<Throwable> {
    private static final Logger log = Logger.getLogger(RestListener.class.getName());

    @Override
    public boolean isMappable(Throwable throwable) {
        return !(throwable instanceof WebApplicationException);
    }

    @Override
    public Response toResponse(Throwable throwable) {
        log.log(Level.SEVERE, "Uncaught error in resource", throwable);
        return Response.serverError().build();
    }
}
