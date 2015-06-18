package com.github.diogoko;

public class StateResult {
    private AppletInstanceState state;

    public StateResult() {
    }

    public StateResult(AppletInstanceState state) {
        this.state = state;
    }

    public AppletInstanceState getState() {
        return state;
    }

    public void setState(AppletInstanceState state) {
        this.state = state;
    }
}
