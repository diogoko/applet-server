package com.github.diogoko.rest;

import com.github.diogoko.Options;
import com.github.diogoko.applet.AppletContainer;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AppletsResourceConfig extends ResourceConfig {
    private Options options;

    public AppletsResourceConfig(AppletContainer appletContainer, Options options) {
        super(AppletsResource.class);
        this.options = options;

        Map<String, Object> props = new HashMap<>();
        props.put("appletContainer", appletContainer);
        addProperties(props);

        register(JacksonFeature.class);
        register(UncaughtThrowableExceptionMapper.class);
        registerInstances(new LoggingFilter(Logger.getLogger(RestListener.class.getName()), true));

        registerInstances(new CorsRequestFilter(options.getAllowOrigin()));
        registerInstances(new CorsResponseFilter(options.getAllowOrigin()));
    }
}
