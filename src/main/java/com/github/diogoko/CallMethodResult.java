package com.github.diogoko;

public class CallMethodResult {
    private Object result;

    private Exception error;

    public CallMethodResult() {
    }

    public CallMethodResult(Object result) {
        this.result = result;
    }

    public CallMethodResult(Exception error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }
}
