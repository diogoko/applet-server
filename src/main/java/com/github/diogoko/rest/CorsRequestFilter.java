package com.github.diogoko.rest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Set;

public class CorsRequestFilter implements ContainerRequestFilter {
    private final Set<String> allowOrigin;

    public CorsRequestFilter(Set<String> allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String origin = requestContext.getHeaderString("Origin");
        if (origin == null || origin.isEmpty()) {
            cancelWithBadRequest(requestContext);
        }

        if (!allowOrigin.contains(origin) && !allowOrigin.contains("*")) {
            cancelWithBadRequest(requestContext);
        }
    }

    protected void cancelWithBadRequest(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST).build());
        requestContext.setProperty("failedCors", true);
    }
}
