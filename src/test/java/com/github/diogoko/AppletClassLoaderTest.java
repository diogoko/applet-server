package com.github.diogoko;

import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class AppletClassLoaderTest {

    public static final String CHAIN_CLASS = "org.apache.commons.chain.Context";
    public static final String CLI_CLASS = "org.apache.commons.cli.Util";
    public static final String CHAIN_JAR = "commons-chain-1.2.jar";
    public static final String CLI_JAR = "commons-cli-1.3.jar";

    @Test
    public void requiredArgs() throws MalformedURLException {
        try {
            new AppletClassLoader(null, null, null);
            fail("both codeBase and code are required");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new AppletClassLoader("", null, null);
            fail("both codeBase and code are required");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new AppletClassLoader(null, "", null);
            fail("both codeBase and code are required");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new AppletClassLoader("", "", null);
            fail("both codeBase and code are required");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new AppletClassLoader("relative/url", "my.sample.Applet", null);
            fail("codeBase must be an absolute URL");
        } catch (IllegalArgumentException e) {
            // ok
        }

        try {
            new AppletClassLoader("http://absolute/url", "http://absolute/my.sample.Applet", null);
            fail("codeBase must be an absolute URL");
        } catch (IllegalArgumentException e) {
            // ok
        }

        AppletClassLoader loader1 = new AppletClassLoader("http://absolute/url/", "Applet", null);
        assertThat(loader1.getURLs(), equalTo(new URL[]{new URL("http://absolute/url/Applet.class")}));

        AppletClassLoader loader2 = new AppletClassLoader("http://absolute/url/", "Applet.class", null);
        assertThat(loader2.getURLs(), equalTo(new URL[]{new URL("http://absolute/url/Applet.class")}));

        AppletClassLoader loader3 = new AppletClassLoader("http://absolute/url/", "my.sample.Applet", null);
        assertThat(loader3.getURLs(), equalTo(new URL[]{new URL("http://absolute/url/my/sample/Applet.class")}));

        AppletClassLoader loader4 = new AppletClassLoader("http://absolute/url/", "my.sample.Applet.class", null);
        assertThat(loader4.getURLs(), equalTo(new URL[]{new URL("http://absolute/url/my/sample/Applet.class")}));
    }

    @Test
    public void archive() throws MalformedURLException {
        AppletClassLoader loader1 = new AppletClassLoader("http://absolute/url/", "Applet", "");
        assertThat(loader1.getURLs(), equalTo(new URL[]{new URL("http://absolute/url/Applet.class")}));

        AppletClassLoader loader2 = new AppletClassLoader("http://absolute/url/", "Applet", "a.jar");
        assertThat(loader2.getURLs(), equalTo(new URL[]{
                new URL("http://absolute/url/Applet.class"),
                new URL("http://absolute/url/a.jar"),
        }));

        AppletClassLoader loader3 = new AppletClassLoader("http://absolute/url/", "Applet", "a.jar  ,  x/b.jar");
        assertThat(loader3.getURLs(), equalTo(new URL[]{
                new URL("http://absolute/url/Applet.class"),
                new URL("http://absolute/url/a.jar"),
                new URL("http://absolute/url/x/b.jar")
        }));
    }

    @Test
    public void loadsClasses() throws ClassNotFoundException {
        checkPreloadedClasses();

        String codeBase = FilenameUtils.getPath(getClass().getResource("/").toString());

        AppletClassLoader loader1 = new AppletClassLoader(codeBase, CLI_CLASS, null);
        Class c1 = loader1.loadClass(CLI_CLASS);
        assertEquals(CLI_CLASS, c1.getName());

        checkPreloadedClasses();

        AppletClassLoader loader2 = new AppletClassLoader(codeBase, CHAIN_CLASS, CHAIN_JAR);
        Class c2 = loader2.loadClass(CHAIN_CLASS);
        assertEquals(CHAIN_CLASS, c2.getName());

        checkPreloadedClasses();

        AppletClassLoader loader3 = new AppletClassLoader(codeBase, CHAIN_CLASS, CHAIN_JAR + "," + CLI_JAR);
        Class c3 = loader3.loadClass(CHAIN_CLASS);
        assertEquals(CHAIN_CLASS, c3.getName());
        Class c4 = loader3.loadClass(CLI_CLASS);
        assertEquals(CLI_CLASS, c4.getName());
    }

    private void checkPreloadedClasses() {
        try {
            ClassLoader loader = getClass().getClassLoader();
            loader.loadClass(CLI_CLASS);
            loader.loadClass(CHAIN_CLASS);

            fail("test classes should not alread by on parent classloader");
        } catch (ClassNotFoundException e) {
            // ok
        }
    }
}
