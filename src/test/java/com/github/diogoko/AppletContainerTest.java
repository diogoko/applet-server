package com.github.diogoko;

import org.junit.Test;

import static org.junit.Assert.*;

public class AppletContainerTest {
    @Test
    public void findingAndRemoving() {
        AppletContainer container = new TestableAppletContainer();

        assertNull("finding null always returns null", container.findByName(null));
        assertNull("a name not found returns null", container.findByName("test"));

        AppletDescription description = new AppletDescription();
        description.setCodeBase("http://absolute/url/");
        description.setCode("Test");
        description.setName("test");
        AppletInstance applet = container.createApplet(description);

        assertSame("can find created applet", container.findByName("test"), applet);

        container.removeApplet("test");
        assertNull("removed applet can't be found", container.findByName("test"));
    }

    @Test
    public void creating() {
        TestableAppletContainer container = new TestableAppletContainer();

        AppletDescription d1 = new AppletDescription();
        d1.setCodeBase("http://absolute/url/");
        d1.setCode("Test");
        d1.setName("test");
        AppletInstance applet1 = container.createApplet(d1);

        assertNotNull("a new applet was created", applet1);
        assertTrue("applet is runnable (inter thread proxy)", applet1 instanceof Runnable);

        Thread appletThread1 = container.getLastCreatedThread();
        assertNotEquals("applet's thread is not the current one", appletThread1, Thread.currentThread());
        assertTrue("applet's thread has been started", appletThread1.isAlive());

        ClassLoader appletClassLoader1 = appletThread1.getContextClassLoader();
        assertNotEquals("applet's classloader is not the current one", appletClassLoader1, getClass().getClassLoader());
        assertTrue("applet's classloader is an AppletClassLoader", appletClassLoader1 instanceof AppletClassLoader);

        AppletDescription d2 = new AppletDescription();
        d2.setCodeBase("http://absolute/url/");
        d2.setCode("Test");
        AppletInstance applet2 = container.createApplet(d2);

        assertNotNull("a name was created for the unnamed applet", applet2.getDescription().getName());
        assertNotEquals("a non-empty name was created", applet2.getDescription().getName(), "");
        Thread appletThread2 = container.getLastCreatedThread();
        assertNotEquals("the applets' threads are not the same", appletThread2, appletThread1);
        ClassLoader appletClassLoader2 = appletThread2.getContextClassLoader();
        assertNotEquals("the applets' classloaders are not the same", appletClassLoader2, appletClassLoader1);
    }

    private class TestableAppletContainer extends AppletContainer {
        private Thread lastCreatedThread;

        @Override
        protected Thread createThread(Runnable runnable) {
            Thread thread = super.createThread(runnable);
            lastCreatedThread = thread;
            return thread;
        }

        public Thread getLastCreatedThread() {
            return lastCreatedThread;
        }
    }
}
