package com.github.diogoko;

public interface RestEventHandler {
    void startApplet(CreateEvent event);

    void stopApplet(String appletName);

    void destroyApplet(String appletName);

    void showApplet(String appletName);

    void hideApplet(String appletName);

    Object callMethod(String appletName, String methodName, Object[] args);
}
