package com.hoppinzq.service.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.hoppinzq.service.annotation.InterfaceImplName;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author:ZhangQi Hession2序列化框架
 **/
@InterfaceImplName("Hession2序列化框架")
public class HessionSerializer implements CustomSerializer {
//    @Override
//    public byte[] serialize(Object obj) throws IOException {
//        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
//        Hessian2Output hessianOutput = new Hessian2Output(byteOutputStream);
//        hessianOutput.writeObject(obj);
//        hessianOutput.flush();
//        return byteOutputStream.toByteArray();
//    }

    @Override
    public void serialize(Object obj, OutputStream outputStream) throws IOException {
        Hessian2Output hessianOutput = new Hessian2Output(outputStream);
        hessianOutput.writeObject(obj);
        hessianOutput.close();
    }

//    @Override
//    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException, ClassNotFoundException {
//        InputStream inputStream = new ByteArrayInputStream(bytes);
//        Hessian2Input hessianInput = new Hessian2Input(inputStream);
//        return (T) hessianInput.readObject();
//    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> clazz) throws IOException, ClassNotFoundException {
        Hessian2Input hessianInput = new Hessian2Input(inputStream);
        return (T) hessianInput.readObject();
    }
}

