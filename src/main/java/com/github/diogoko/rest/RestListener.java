package com.github.diogoko.rest;

import com.github.diogoko.applet.AppletContainer;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

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
