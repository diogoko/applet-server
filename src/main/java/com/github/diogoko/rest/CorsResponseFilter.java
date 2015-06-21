package com.github.diogoko.rest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Set;

public class CorsResponseFilter implements ContainerResponseFilter {
    private final Set<String> allowOrigin;

    public CorsResponseFilter(Set<String> allowOrigin) {
        this.allowOrigin = allowOrigin;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (requestContext.getProperty("failedCors") != true) {
            MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            headers.add("Access-Control-Allow-Origin", requestContext.getHeaderString("Origin"));
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
            headers.add("Access-Control-Allow-Headers", "Content-Type");
        }
    }
}
