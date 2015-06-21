package com.github.diogoko.rest.result;

public class ExceptionResult {
    private String className;

    private String message;

    public ExceptionResult() {
    }

    public ExceptionResult(String className, String message) {
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
