package com.github.diogoko;

import org.junit.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.Assert.*;

public class AppletInstanceTest {

    @Test
    public void lifeCycle() {
        AppletDescription desc = new AppletDescription();
        desc.setCode("com.github.diogoko.SampleApplet.class");
        desc.setWidth(123);
        desc.setHeight(456);
        desc.setName("sample");

        AppletInstanceImpl applet = new AppletInstanceImpl(desc);
        assertNull(applet.getInstance());
        assertEquals(applet.getState(), AppletInstanceState.INACTIVE);
        try {
            applet.stop();
            fail("applet can't be stopped when inactive");
        } catch (IllegalStateException e) {
            // ok
        }
        try {
            applet.destroy();
            fail("applet can't be destroyed when inactive");
        } catch (IllegalStateException e) {
            // ok
        }

        applet.start();
        assertEquals(applet.getState(), AppletInstanceState.STARTED);
        SampleApplet i = (SampleApplet) applet.getInstance();
        assertTrue(i.isCalledInit() && i.isCalledStart() && !i.isCalledStop() && !i.isCalledDestroy());
        Container iParent = getAncestor(i);
        assertTrue(iParent instanceof JFrame);
        assertEquals(iParent.getWidth(), 123);
        assertEquals(iParent.getHeight(), 456);
        assertEquals(((JFrame)iParent).getTitle(), "sample - Applet");
        try {
            applet.start();
            fail("applet can't be started when already started");
        } catch (IllegalStateException e) {
            // ok
        }
        try {
            applet.destroy();
            fail("applet can't be destroyed when started");
        } catch (IllegalStateException e) {
            // ok
        }

        Object result = applet.callMethod("hello", new Object[] { "test" });
        assertEquals(result, "hello test");

        applet.show();
        assertTrue(iParent.isVisible());

        applet.hide();
        assertFalse(iParent.isVisible());

        applet.stop();
        assertTrue(i.isCalledInit() && i.isCalledStart() && i.isCalledStop() && !i.isCalledDestroy());

        applet.destroy();
        assertTrue(i.isCalledInit() && i.isCalledStart() && i.isCalledStop() && i.isCalledDestroy());
        try {
            applet.start();
            fail("applet can't be started when destroyed");
        } catch (IllegalStateException e) {
            // ok
        }
        try {
            applet.stop();
            fail("applet can't be stopped when destroyed");
        } catch (IllegalStateException e) {
            // ok
        }
        try {
            applet.stop();
            fail("applet can't be destroyed when alread destroyed");
        } catch (IllegalStateException e) {
            // ok
        }
    }

    private Container getAncestor(Container container) {
        while (container.getParent() != null) {
            container = container.getParent();
        }

        return container;
    }

}
