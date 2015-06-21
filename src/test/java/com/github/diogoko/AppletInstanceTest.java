package com.github.diogoko;

import com.github.diogoko.applet.AppletDescription;
import com.github.diogoko.applet.AppletInstanceImpl;
import com.github.diogoko.applet.AppletInstanceState;
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
        assertEquals(AppletInstanceState.INACTIVE, applet.getState());
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
        assertEquals(AppletInstanceState.STARTED, applet.getState());
        SampleApplet i = (SampleApplet) applet.getInstance();
        assertTrue(i.isCalledInit() && i.isCalledStart() && !i.isCalledStop() && !i.isCalledDestroy());
        Container iParent = getAncestor(i);
        assertTrue(iParent instanceof JFrame);
        assertEquals(123, iParent.getWidth());
        assertEquals(456, iParent.getHeight());
        assertEquals("sample - Applet", ((JFrame) iParent).getTitle());
        try {
            applet.destroy();
            fail("applet can't be destroyed when started");
        } catch (IllegalStateException e) {
            // ok
        }

        Object result = applet.callMethod("hello", new Object[] { "test" });
        assertEquals("hello test", result);

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
            fail("applet can't be destroyed when already destroyed");
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
