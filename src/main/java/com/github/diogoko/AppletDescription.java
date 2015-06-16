package com.github.diogoko;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public class AppletDescription {
    private static final int DEFAULT_WIDTH = 400;

    private static final int DEFAULT_HEIGHT = 300;

    private String name;

    private String codeBase;

    private String code;

    private String archive;

    private int width;

    private int height;

    private Map<String, String> params;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCodeBase() {
        return codeBase;
    }

    public void setCodeBase(String codeBase) {
        this.codeBase = codeBase;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @JsonIgnore
    public String getCodeClassName() {
        return (code == null) ? null : (code.replaceFirst("\\.class$", ""));
    }

    @JsonIgnore
    public int getEffectiveWidth() {
        return (width <= 0) ? DEFAULT_WIDTH : width;
    }

    @JsonIgnore
    public int getEffectiveHeight() {
        return (height <= 0) ? DEFAULT_HEIGHT : height;
    }
}
