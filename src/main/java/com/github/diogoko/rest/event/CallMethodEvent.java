package com.github.diogoko.rest.event;

public class CallMethodEvent {
    private Object[] args;

    public CallMethodEvent() {
    }

    public CallMethodEvent(Object[] args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
