package com.github.diogoko;

import com.github.diogoko.applet.AppletContainer;
import com.github.diogoko.applet.AppletInstance;
import com.github.diogoko.rest.RestListener;
import com.github.diogoko.rest.event.CreateEvent;

import java.util.HashMap;
import java.util.Map;

public class AppletServer {

    private AppletContainer appletContainer;

    private RestListener restListener;

    public static void main(String[] args) {
        AppletServer a = new AppletServer();
        a.start();
    }

    private void start() {
        appletContainer = new AppletContainer();

        Map<String, String> optionsMap = new HashMap<>();
        optionsMap.put(Options.PORT, "9998");
        restListener = new RestListener(appletContainer, new Options(optionsMap));
        restListener.start();
    }

    public void startApplet(CreateEvent event) {
    }

    public void showApplet(String appletName) {

    }

    public void hideApplet(String appletName) {

    }

    public void stopApplet(String appletName) {
        AppletInstance applet = appletContainer.findByName(appletName);
        // TODO: check null

//        applet.hide();
//        applet.stop();
    }

    public void destroyApplet(String appletName) {
        AppletInstance applet = appletContainer.findByName(appletName);
        // TODO: check null

//        applet.hide();
//        applet.stop();
//        applet.destroy();

        appletContainer.removeApplet(appletName);
    }

    public Object callMethod(String appletName, String methodName, Object[] args) {
        AppletInstance applet = appletContainer.findByName(appletName);
        // TODO: check null

//        return applet.callMethod(methodName, args);
        return null;
    }
}
