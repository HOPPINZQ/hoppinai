package com.hoppinzq.service.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author:ZhangQi 序列化接口，用以拓展不同的序列化方式
 **/
public interface CustomSerializer {
    /**
     * 获取序列化原始比特流
     * @param obj
     * @return
     * @throws IOException
     */
//    byte[] serialize(Object obj) throws IOException;

    /**
     * 直接将序列化流写入outputStream
     *
     * @param obj
     * @param outputStream
     * @throws IOException
     */
    void serialize(Object obj, OutputStream outputStream) throws IOException;

    /**
     * 反序列化，直接将原始比特流反序列化为类
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
//    <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException;

    /**
     * 反序列化，直接把输入流反序列化为类
     *
     * @param inputStream
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    <T> T deserialize(InputStream inputStream, Class<T> clazz) throws IOException, ClassNotFoundException;

}