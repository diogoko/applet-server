package com.github.diogoko;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AppletsResourceConfig extends ResourceConfig {
    public AppletsResourceConfig(AppletContainer appletContainer) {
        super(AppletsResource.class);

        Map<String, Object> props = new HashMap<>();
        props.put("appletContainer", appletContainer);

        addProperties(props);
        register(JacksonFeature.class);
        registerInstances(new LoggingFilter(Logger.getLogger(RestListener.class.getName()), true));
    }
}
