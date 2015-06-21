package com.github.diogoko;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Options {
    public static final String ALLOW_ORIGIN = "allowOrigin";

    public static final String PORT = "port";

    private int port;

    private Set<String> allowOrigin;

    public Options() {
    }

    public Options(Map<String, String> optionsMap) {
        parseAllowOrigin(optionsMap.get(ALLOW_ORIGIN));
    }

    private Set<String> parseAllowOrigin(String optionValue) {
        if (optionValue == null || optionValue.isEmpty()) {
            throw new IllegalArgumentException(String.format("Option %s is required", ALLOW_ORIGIN));
        }

        Set<String> allowOrigin = new HashSet<>();
        for (String origin : optionValue.split(",")) {
            allowOrigin.add(origin.trim());
        }

        return allowOrigin;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Set<String> getAllowOrigin() {
        return allowOrigin;
    }

    public void setAllowOrigin(Set<String> allowOrigin) {
        this.allowOrigin = allowOrigin;
    }
}
