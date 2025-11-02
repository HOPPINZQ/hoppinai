package com.hoppinzq.service.util.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class Base64ToImage {

    public static void main(String[] args) {
        String base64String = "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"; // 替换为你的Base64字符串
        String outputFilePath = "D:\\csdn\\qrcode.png"; // 转换后的图片保存路径

        try {
            // 解码Base64字符串为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(base64String);

            // 将字节数组转换为图片
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);

            // 保存图片
            File outputFile = new File(outputFilePath);
            ImageIO.write(image, "png", outputFile);

            System.out.println("图片已保存到：" + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
