package com.hoppinzq.mcp.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class ClassLoaderUtils {

    public static String getResource(String file) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream resource = contextClassLoader.getResourceAsStream(file);
             InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader);
             StringWriter writer = new StringWriter()) {
            char[] chars = new char[1024];
            int readChars;
            while ((readChars = bufferedReader.read(chars)) != -1) {
                writer.write(chars, 0, readChars);
            }
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("读取资源文件错误：" + e.getMessage(), e);
        }
    }
}

