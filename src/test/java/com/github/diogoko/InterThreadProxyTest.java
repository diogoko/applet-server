package com.github.diogoko;

import com.github.diogoko.threading.InterThreadProxy;
import com.github.diogoko.threading.Stoppable;
import org.junit.Test;

import static org.junit.Assert.*;

public class InterThreadProxyTest {
    public interface SampleInterface {
        void m1();

        Thread getLastThreadAndReset();

        int div(int a, int b);

        void triggerError();
    }

    public class SampleInterfaceImpl implements SampleInterface {
        private Thread lastThread = null;

        @Override
        public void m1() {
            lastThread = Thread.currentThread();
        }

        @Override
        public Thread getLastThreadAndReset() {
            Thread t = lastThread;
            lastThread = null;
            return t;
        }

        @Override
        public int div(int a, int b) {
            lastThread = Thread.currentThread();
            return a / b;
        }

        @Override
        public void triggerError() {
            lastThread = Thread.currentThread();
            throw new Error("not an Exception");
        }
    }

    @Test
    public void basic() throws InterruptedException {
        SampleInterface o = new SampleInterfaceImpl();
        Runnable r = InterThreadProxy.newProxyInstance(getClass().getClassLoader(), o);
        Thread t = new Thread(r);
        t.start();

        assertTrue("proxy is Runnable", r instanceof Runnable);
        assertTrue("proxy is Stoppable", r instanceof Stoppable);
        assertTrue("proxy implements original interface", r instanceof SampleInterface);

        SampleInterface ro = (SampleInterface) r;

        ro.m1();
        assertEquals(t, o.getLastThreadAndReset());

        int result = ro.div(10, 2);
        assertEquals(t, o.getLastThreadAndReset());
        assertEquals(5, result);

        try {
            ro.div(3, 0);
            fail("thrown exception should have bubbled here");
        } catch (ArithmeticException e) {
            // ok
            assertEquals(t, o.getLastThreadAndReset());
        }

        try {
            ro.triggerError();
            fail("thrown error should have bubbled here");
        } catch (Error e) {
            // ok
            assertEquals(t, o.getLastThreadAndReset());
            assertEquals("not an Exception", e.getMessage());
        }

        ((Stoppable)ro).stop();
        t.join(500);
        assertTrue("thread should have stopped already", !t.isAlive());
    }
}
