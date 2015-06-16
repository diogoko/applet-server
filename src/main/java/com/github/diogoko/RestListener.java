package com.github.diogoko;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class RestListener {
    private final AppletContainer appletContainer;

    public RestListener(AppletContainer appletContainer) {
        this.appletContainer = appletContainer;
    }

    public void start() {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
        ResourceConfig config = new AppletsResourceConfig(appletContainer);
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
    }
}
