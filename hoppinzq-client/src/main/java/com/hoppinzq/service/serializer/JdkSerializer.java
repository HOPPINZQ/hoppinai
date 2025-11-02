package com.hoppinzq.service.serializer;

import com.hoppinzq.service.annotation.InterfaceImplName;

import java.io.*;

/**
 * @author:ZhangQi jdk自带的序列化
 **/
@InterfaceImplName("jdk自带的序列化方式")
public class JdkSerializer implements CustomSerializer {
//    @Override
//    public byte[] serialize(Object obj) throws IOException {
//        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
//        objectStream.writeObject(obj);
//        return byteStream.toByteArray();
//    }

    @Override
    public void serialize(Object obj, OutputStream outputStream) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(outputStream);
        out.writeObject(obj);
        out.flush();
    }

//    @Override
//    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
//        InputStream inputStream = new ByteArrayInputStream(bytes);
//        ObjectInputStream in = new ObjectInputStream(inputStream);
//        return (T) in.readObject();
//    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> clazz) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(inputStream);
        return (T) in.readObject();
    }
}