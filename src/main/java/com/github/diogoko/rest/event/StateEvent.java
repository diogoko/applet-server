package com.github.diogoko.rest.event;

import com.github.diogoko.applet.AppletInstanceState;

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
