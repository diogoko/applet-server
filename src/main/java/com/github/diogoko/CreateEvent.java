package com.github.diogoko;

public class CreateEvent {
    private AppletDescription applet;

    private boolean show;

    public CreateEvent() {
    }

    public CreateEvent(AppletDescription applet, boolean show) {
        this.applet = applet;
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public AppletDescription getApplet() {
        return applet;
    }

    public void setApplet(AppletDescription applet) {
        this.applet = applet;
    }
}
