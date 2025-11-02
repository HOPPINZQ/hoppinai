package com.hoppinzq.service.serializer;

import com.hoppinzq.service.annotation.InterfaceImplName;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author:ZhangQi JSON序列化，未实现 todo
 **/
@InterfaceImplName("JSON序列化序列化方式")
public class JsonCustomSerializer implements CustomSerializer {

    @Override
    public void serialize(Object obj, OutputStream outputStream) throws IOException {

    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> clazz) throws IOException, ClassNotFoundException {
        return null;
    }
}