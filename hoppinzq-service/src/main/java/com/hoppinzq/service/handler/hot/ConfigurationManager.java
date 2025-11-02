package com.hoppinzq.service.handler.hot;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;

public class ConfigurationManager {
    private Properties properties;
    private String configFile;

    public ConfigurationManager(String configFile) {
        this.configFile = configFile;
        this.properties = new Properties();
        loadConfig();
        watchConfigFile();
    }

    private void loadConfig() {
        try (FileReader reader = new FileReader(configFile)) {
            properties.load(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void watchConfigFile() {
        try {
            Path path = Paths.get(configFile);
            WatchService watchService = FileSystems.getDefault().newWatchService();
            path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            Thread thread = new Thread(() -> {
                while (true) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException e) {
                        return;
                    }
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.context().toString().equals(path.getFileName().toString())) {
                            loadConfig();
                            System.out.println("Config file has been updated.");
                        }
                    }
                    key.reset();
                }
            });
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}