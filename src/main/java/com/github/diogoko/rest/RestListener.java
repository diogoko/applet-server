package com.github.diogoko.rest;

import com.github.diogoko.Options;
import com.github.diogoko.applet.AppletContainer;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class RestListener {
    private final AppletContainer appletContainer;

    private Options options;

    public RestListener(AppletContainer appletContainer, Options options) {
        this.appletContainer = appletContainer;
        this.options = options;
    }

    public void start() {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(options.getPort()).build();
        ResourceConfig config = new AppletsResourceConfig(appletContainer, options);
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
    }
}
