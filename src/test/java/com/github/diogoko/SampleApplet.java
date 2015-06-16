package com.github.diogoko;

import java.applet.Applet;

public class SampleApplet extends Applet {
    private boolean calledInit = false;

    private boolean calledStart = false;

    private boolean calledStop = false;

    private boolean calledDestroy = false;

    @Override
    public void init() {
        calledInit = true;
    }

    @Override
    public void start() {
        calledStart = true;
    }

    @Override
    public void stop() {
        calledStop = true;
    }

    @Override
    public void destroy() {
        calledDestroy = true;
    }

    public String hello(String name) {
        return "hello " + name;
    }

    public boolean isCalledInit() {
        return calledInit;
    }

    public boolean isCalledStart() {
        return calledStart;
    }

    public boolean isCalledStop() {
        return calledStop;
    }

    public boolean isCalledDestroy() {
        return calledDestroy;
    }
}
