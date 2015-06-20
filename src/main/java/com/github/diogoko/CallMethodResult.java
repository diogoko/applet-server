package com.github.diogoko;

public class CallMethodResult {
    private Object result;

    private ExceptionResult error;

    public CallMethodResult() {
    }

    public CallMethodResult(Object result) {
        this.result = result;
    }

    public CallMethodResult(ExceptionResult error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public ExceptionResult getError() {
        return error;
    }

    public void setError(ExceptionResult error) {
        this.error = error;
    }
}
