package com.github.diogoko.rest.result;

public class VisibleResult {
    private boolean visible;

    public VisibleResult() {
    }

    public VisibleResult(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
