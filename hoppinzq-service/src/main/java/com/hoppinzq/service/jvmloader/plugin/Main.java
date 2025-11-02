package com.hoppinzq.service.jvmloader.plugin;

import com.hoppinzq.service.jvmloader.HotSwapClassLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.concurrent.Executors;

public class Main {

    private static String pluginPath = "D:\\";
    private static String packageName = "com.hoppinzq.service.jvmloader.plugin.MyPlugin";
    private static String javaPath = "D:\\MyPlugin.java";
    private HotSwapClassLoader classLoader;
    private Plugin implementationInstance;

    public static void main(String[] args) throws Exception {

        PluginManager pluginManager = new PluginManager();
        pluginManager.loadPlugin(pluginPath, packageName);

        Main main = new Main();
        main.init(javaPath);

    }

    private void loadImplementation2() throws Exception {
        PluginManager pluginManager = new PluginManager();
        pluginManager.loadPlugin(pluginPath, packageName);
    }

    private void loadImplementation1() throws Exception {
        // 加载新的接口实现类
        File file = new File("E:\\");
        URL url = file.toURI().toURL();
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{url});
        Class<?> pluginClass = classLoader.loadClass(packageName); // 替换为插件类的全限定名
        // 调用新的接口实现类的 execute() 方法
        Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
        Method executeMethod = pluginClass.getMethod("execute");
        executeMethod.invoke(plugin);
    }

    public void init(String javaPath) throws Exception {
        URL[] classUrls = new URL[]{new File(javaPath).getParentFile().toURI().toURL()};
        classLoader = new HotSwapClassLoader(classUrls, getClass().getClassLoader());
        Class<?> implementationClass = classLoader.loadClass(packageName);
        implementationInstance = (Plugin) implementationClass.getDeclaredConstructor().newInstance();

        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path dir = Paths.get(new File(javaPath).getParent());
        dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        Executors.newSingleThreadExecutor().submit(() -> {
            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    return;
                }
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                        String fileName = event.context().toString();
                        System.out.println(fileName);
                        if (fileName.equals(new File(javaPath).getName())) {
                            try {
                                Class<?> implementationClass1 = classLoader.loadClass(packageName);
                                implementationInstance = (Plugin) implementationClass1.getDeclaredConstructor().newInstance();
                                System.out.println("接口热加载");
                                implementationInstance.execute();
                                loadImplementation1();
                                loadImplementation2();
                            } catch (Exception e) {
                                System.out.println("Failed to reload implementation: " + e.getMessage());
                            }
                        }
                    }
                }
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        });
    }
}