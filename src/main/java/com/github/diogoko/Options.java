package com.github.diogoko;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Options {
    public static final String ALLOW_ORIGIN = "allowOrigin";

    public static final String PORT = "port";

    private int port;

    private Set<String> allowOrigin;

    public Options() {
    }

    public Options(File optionsFile) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(optionsFile));

        Map<String, String> stringProperties = new HashMap<>();
        for (Map.Entry item : properties.entrySet()) {
            stringProperties.put(item.getKey().toString(), item.getValue().toString());
        }
        parse(stringProperties);
    }

    public Options(Map<String, String> optionsMap) {
        parse(optionsMap);
    }

    protected void parse(Map<String, String> optionsMap) {
        parseAllowOrigin(optionsMap.get(ALLOW_ORIGIN));
        parsePort(optionsMap.get(PORT));
    }

    private void parsePort(String optionValue) {
        if (optionValue == null || optionValue.isEmpty()) {
            throw new IllegalArgumentException(String.format("Option %s is required", PORT));
        }

        this.port = Integer.parseInt(optionValue);
    }

    private void parseAllowOrigin(String optionValue) {
        if (optionValue == null || optionValue.isEmpty()) {
            throw new IllegalArgumentException(String.format("Option %s is required", ALLOW_ORIGIN));
        }

        this.allowOrigin = new HashSet<>();
        for (String origin : optionValue.split(",")) {
            this.allowOrigin.add(origin.trim());
        }
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
