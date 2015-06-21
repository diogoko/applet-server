package com.github.diogoko.applet;

import org.apache.commons.lang3.reflect.MethodUtils;

import javax.swing.*;
import java.applet.Applet;
import java.lang.reflect.InvocationTargetException;

public class AppletInstanceImpl implements AppletInstance {
    private AppletDescription description;

    private AppletInstanceState state;

    private boolean visible;

    private Applet instance;

    private JFrame frame;

    public AppletInstanceImpl(AppletDescription description) {
        this.description = description;
        state = AppletInstanceState.INACTIVE;
        visible = false;
    }

    @Override
    public AppletDescription getDescription() {
        return description;
    }

    @Override
    public void start() {
        if (state.equals(AppletInstanceState.STARTED)) {
            return;
        }

        if (!state.equals(AppletInstanceState.INACTIVE) && !state.equals(AppletInstanceState.STOPPED)) {
            throw new IllegalStateException("Applet must be INACTIVE or STOPPED to be started");
        }

        try {
            if (state.equals(AppletInstanceState.INACTIVE)) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Class<Applet> clazz = (Class<Applet>) classLoader.loadClass(description.getCodeClassName());

                instance = clazz.newInstance();

                frame = new JFrame(description.getName() + " - Applet");
                frame.setSize(description.getEffectiveWidth(), description.getEffectiveHeight());
                frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                frame.setContentPane(instance);

                instance.init();
            }

            instance.start();
            state = AppletInstanceState.STARTED;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void show() {
        if (!state.equals(AppletInstanceState.STARTED) && !state.equals(AppletInstanceState.STOPPED)) {
            throw new IllegalStateException("Applet must be STARTED or STOPPED to be started");
        }

        if (!visible) {
            frame.setVisible(true);
            visible = true;
        }
    }

    @Override
    public void hide() {
        if (!state.equals(AppletInstanceState.STARTED) && !state.equals(AppletInstanceState.STOPPED)) {
            throw new IllegalStateException("Applet must be STARTED or STOPPED to be started");
        }

        if (visible) {
            frame.setVisible(false);
            visible = false;
        }
    }

    @Override
    public void stop() {
        if (state.equals(AppletInstanceState.STOPPED)) {
            return;
        }

        if (!state.equals(AppletInstanceState.STARTED)) {
            throw new IllegalStateException("Applet must be STARTED to be stopped");
        }

        instance.stop();
        state = AppletInstanceState.STOPPED;
    }

    @Override
    public void destroy() {
        if (!state.equals(AppletInstanceState.STOPPED)) {
            throw new IllegalStateException("Applet must be STOPPED to be destroyed");
        }

        instance.destroy();
        if (frame != null) {
            frame.dispose();
        }
        state = AppletInstanceState.DESTROYED;
    }

    @Override
    public Object callMethod(String methodName, Object[] args) {
        if (!state.equals(AppletInstanceState.STARTED)) {
            throw new IllegalStateException("Applet must be STARTED to have methods called");
        }

        try {
            return MethodUtils.invokeMethod(instance, methodName, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Applet getInstance() {
        return instance;
    }

    @Override
    public AppletInstanceState getState() {
        return state;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }
}
