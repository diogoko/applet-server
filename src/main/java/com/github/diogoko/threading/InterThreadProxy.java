package com.github.diogoko.threading;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InterThreadProxy implements Runnable {
    public static Runnable newProxyInstance(ClassLoader loader, final Object target) {
        final InterThreadProxy proxy = new InterThreadProxy(target);

        List<Class<?>> interfaces = new ArrayList(Arrays.asList(target.getClass().getInterfaces()));
        interfaces.add(Runnable.class);
        interfaces.add(Stoppable.class);

        return (Runnable) Proxy.newProxyInstance(loader, interfaces.toArray(new Class[0]), new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass().equals(Runnable.class)) {
                    proxy.run();
                    return null;
                } else if (method.getDeclaringClass().equals(Stoppable.class)) {
                    proxy.stop();
                    return null;
                } else {
                    return proxy.callMethodAndWait(new MethodCall(method, args));
                }
            }
        });
    }

    private boolean shouldFinishProcessing;

    private BlockingQueue<MethodCall> methodCalls;

    public static class MethodCall {
        private Method method;

        private Object[] args;

        private Object result;

        private Throwable error;

        private boolean completed;

        public MethodCall(Method method, Object[] args) {
            this.method = method;
            this.args = args;
            completed = false;
        }

        public Method getMethod() {
            return method;
        }

        public Object[] getArgs() {
            return args;
        }

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public Throwable getError() {
            return error;
        }

        public void setError(Throwable error) {
            this.error = error;
        }

        public synchronized void waitForCompletion() throws InterruptedException {
            while (!completed) {
                wait();
            }
        }

        public synchronized void complete() {
            completed = true;
            notify();
        }
    }

    private Object target;

    private InterThreadProxy(Object target) {
        shouldFinishProcessing = false;
        methodCalls = new LinkedBlockingQueue<>();
        this.target = target;
    }

    private synchronized Object callMethodAndWait(MethodCall methodCall) throws Throwable {
        methodCalls.put(methodCall);

        methodCall.waitForCompletion();
        if (methodCall.getError() != null) {
            throw methodCall.getError();
        }
        return methodCall.getResult();
    }

    private synchronized void stop() {
        shouldFinishProcessing = true;
    }

    public void run() {
        while (!shouldFinishProcessing) {
            MethodCall next;
            try {
                next = methodCalls.poll(100, TimeUnit.MILLISECONDS);
                if (next == null) {
                    continue;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            try {
                Object result = next.getMethod().invoke(target, next.getArgs());
                if (!next.getMethod().getReturnType().equals(Void.TYPE)) {
                    next.setResult(result);
                }
            } catch (InvocationTargetException e) {
                next.setError(e.getCause());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            next.complete();
        }
    }
}
