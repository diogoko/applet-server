package com.github.diogoko;

import com.github.diogoko.applet.AppletContainer;
import com.github.diogoko.rest.RestListener;

import java.io.File;
import java.io.IOException;

public class AppletServer {

    private AppletContainer appletContainer;

    private RestListener restListener;

    public static void main(String[] args) throws IOException {
        AppletServer a = new AppletServer();
        a.start();
    }

    private void start() throws IOException {
        appletContainer = new AppletContainer();

        Options options = new Options(new File("./server.properties"));
        restListener = new RestListener(appletContainer, options);
        restListener.start();
    }

}
