package com.hoppinzq.service.jvmloader.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginManager {
    public void loadPlugin(String pluginPath, String packageName) throws Exception {
        File file = new File(pluginPath);
        URL url = file.toURI().toURL();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{url});
        Class<?> pluginClass = classLoader.loadClass(packageName); // 替换为插件类的全限定名
        Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
        plugin.execute();
    }
}