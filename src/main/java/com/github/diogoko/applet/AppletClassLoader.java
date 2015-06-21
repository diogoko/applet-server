package com.github.diogoko.applet;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AppletClassLoader extends URLClassLoader {
    public AppletClassLoader(String codeBase, String code, String archive) {
        super(createURLArray(codeBase, code, archive));
    }

    private static URL[] createURLArray(String codeBase, String code, String archive) {
        List<URL> urls = new ArrayList<>();

        if (codeBase == null || codeBase.isEmpty()) {
            throw new IllegalArgumentException("codeBase parameter can't be null nor empty");
        }

        URL codeBaseURL;
        try {
            URI codeBaseURI = new URI(codeBase);
            if (!codeBaseURI.isAbsolute()) {
                throw new IllegalArgumentException("codeBase parameter must be an absolute URL");
            }

            codeBaseURL = codeBaseURI.toURL();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL for codeBase parameter", e);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL for codeBase parameter", e);
        }

        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("code parameter can't be null nor empty");
        }

        if (code.endsWith(".class")) {
            code = code.substring(0, code.length() - ".class".length());
        }

        Pattern packagePattern = Pattern.compile("\\w+(\\.\\w+)*");
        if (!packagePattern.matcher(code).matches()) {
            throw new IllegalArgumentException("code parameter must be a valid class name");
        }

        code = code.replaceAll("\\.", "/");
        code += ".class";

        try {
            URL codeURL = new URL(codeBaseURL, code);
            urls.add(codeURL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("code parameter could not be added to codeBase", e);
        }

        if (archive != null) {
            for (String jar : archive.split(",")) {
                jar = jar.trim();
                if (!jar.isEmpty()) {
                    try {
                        urls.add(new URL(codeBaseURL, jar));
                    } catch (MalformedURLException e) {
                        throw new IllegalArgumentException("archive parameter must contain valid URLs", e);
                    }
                }
            }
        }

        return urls.toArray(new URL[urls.size()]);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        try {
            return super.loadClass(className);
        } catch (ClassNotFoundException e) {
            try {
                for (URL url : getURLs()) {
                    if (!url.toString().endsWith(".jar") && url.toString().endsWith(className.replaceAll("\\.", "/") + ".class")) {
                        byte[] classData = IOUtils.toByteArray(url);
                        return defineClass(className, classData, 0, classData.length);
                    }
                }
            } catch (IOException ee) {
                throw new ClassNotFoundException("Could not load class file", ee);
            }
        }

        throw new ClassNotFoundException("Could not find suitable class file for class: " + className);
    }
}
