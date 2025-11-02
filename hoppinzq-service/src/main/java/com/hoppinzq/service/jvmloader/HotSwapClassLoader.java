package com.hoppinzq.service.jvmloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class HotSwapClassLoader extends URLClassLoader {
    private Map<String, Class<?>> loadedClasses;

    public HotSwapClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        loadedClasses = new HashMap<>();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> cls = loadedClasses.get(name);
        if (cls == null) {
            cls = super.findClass(name);
        }
        return cls;
    }

    public void reload(String className, byte[] classBytes) {
        Class<?> cls = defineClass(null, classBytes, 0, classBytes.length);
        loadedClasses.put(className, cls);
    }
}