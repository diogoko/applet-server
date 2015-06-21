package com.github.diogoko.applet;

public interface AppletInstance {
    void start();

    void show();

    void hide();

    void stop();

    void destroy();

    Object callMethod(String methodName, Object[] args);

    AppletDescription getDescription();

    AppletInstanceState getState();

    boolean isVisible();
}
