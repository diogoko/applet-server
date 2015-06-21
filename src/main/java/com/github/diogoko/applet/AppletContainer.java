package com.github.diogoko.applet;

import com.github.diogoko.threading.InterThreadProxy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppletContainer {
    private Map<String, AppletInstance> applets;

    public AppletContainer() {
        applets = new HashMap<>();
    }

    public synchronized AppletInstance findByName(String name) {
        if (name == null) {
            return null;
        }

        return applets.get(name);
    }

    public synchronized AppletInstance createApplet(AppletDescription description) {
        if (description.getName() == null || description.getName().isEmpty()) {
            description.setName(generateUniqueName());
        }
        AppletInstance instance = new AppletInstanceImpl(description);

        ClassLoader currentClassLoader = getClass().getClassLoader();
        Runnable runnableInstance = InterThreadProxy.newProxyInstance(currentClassLoader, instance);

        Thread thread = createThread(runnableInstance);
        ClassLoader instanceClassLoader = new AppletClassLoader(description.getCodeBase(), description.getCode(), description.getArchive());
        thread.setContextClassLoader(instanceClassLoader);
        thread.start();

        applets.put(description.getName(), (AppletInstance) runnableInstance);
        return (AppletInstance) runnableInstance;
    }

    protected Thread createThread(Runnable runnable) {
        return new Thread(runnable);
    }

    private String generateUniqueName() {
        return UUID.randomUUID().toString();
    }

    public synchronized void removeApplet(String name) {
        applets.remove(name);
    }
}
