package com.github.diogoko.rest.result;

public class ErrorResult {
    private String className;

    private String message;

    public ErrorResult() {
    }

    public ErrorResult(String className, String message) {
        this.className = className;
        this.message = message;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
