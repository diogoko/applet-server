package com.github.diogoko.rest.result;

public class CallMethodResult {
    private Object result;

    private ErrorResult error;

    public CallMethodResult() {
    }

    public CallMethodResult(Object result) {
        this.result = result;
    }

    public CallMethodResult(ErrorResult error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public ErrorResult getError() {
        return error;
    }

    public void setError(ErrorResult error) {
        this.error = error;
    }
}
