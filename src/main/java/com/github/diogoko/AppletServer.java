package com.github.diogoko;

import com.github.diogoko.applet.AppletContainer;
import com.github.diogoko.applet.AppletInstance;
import com.github.diogoko.rest.RestListener;
import com.github.diogoko.rest.event.CreateEvent;

public class AppletServer {

    private AppletContainer appletContainer;

    private RestListener restListener;

    public static void main(String[] args) {
        AppletServer a = new AppletServer();
        a.start();

//        AppletDescription d = new AppletDescription();
//        //d.setCode("com.github.diogoko.HelloWorld2");
//        //d.setArchive("file:///C/Users/Diogo/IdeaProjects/applet-server/target/classes/com/github/diogoko/HelloWorld.class");
//        d.setCode("HelloWorld");
//        d.setArchive("file:///C:/Users/Diogo/Downloads/HelloWorld.class");
//        StartAppletEvent e = new StartAppletEvent();
//        e.setShow(true);
//        e.setApplet(d);
//        a.startApplet(e);
    }

    private void start() {
        appletContainer = new AppletContainer();

        restListener = new RestListener(appletContainer);
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
