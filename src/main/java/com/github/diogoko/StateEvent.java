package com.github.diogoko;

public class StateEvent {
    private AppletInstanceState state;

    public StateEvent() {
    }

    public StateEvent(AppletInstanceState state) {
        this.state = state;
    }

    public AppletInstanceState getState() {
        return state;
    }

    public void setState(AppletInstanceState state) {
        this.state = state;
    }
}
