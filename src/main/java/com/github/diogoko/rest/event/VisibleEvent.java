package com.github.diogoko.rest.event;

public class VisibleEvent {
    private boolean visible;

    public VisibleEvent() {
    }

    public VisibleEvent(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
