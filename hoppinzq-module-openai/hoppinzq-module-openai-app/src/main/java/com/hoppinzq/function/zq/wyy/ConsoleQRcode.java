package com.hoppinzq.function.zq.wyy;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author 薄荷你玩
 * @Date 2023/04/16
 * @Website www.bhshare.cn
 */
public class ConsoleQRcode {
    /**
     * 打印二维码 -> console
     *
     * @param content 二维码内容
     */
    public static void printQRcode(String content) {
        int width = 1; // 二维码宽度
        int height = 1; // 二维码高度

        // 定义二维码的参数
        HashMap<EncodeHintType, java.io.Serializable> hints = new HashMap<EncodeHintType, java.io.Serializable>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");//编码方式
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//纠错等级

        // 打印二维码
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            for (int j = 0; j < bitMatrix.getHeight(); j++) {
                for (int i = 0; i < bitMatrix.getWidth(); i++) {
                    if (bitMatrix.get(i, j)) {
                        System.out.print("■");
                    } else {
                        System.out.print("  ");
                    }

                }
                System.out.println();
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    public static void generateQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置字符集为UTF-8
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path); // 输出为PNG格式的图片文件
        System.out.println("QR Code generated and saved at: " + filePath); // 打印文件路径信息到控制台
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        try {
//            generateQRCodeImage("Hello World!", 300, 300, "D:\\csdn\\qrcode.png"); // 生成二维码并保存为文件
//        } catch (WriterException | IOException e) {
//            e.printStackTrace(); // 打印异常信息到控制台
//        }

        Image image = ImageIO.read(new File("D:\\csdn\\qrcode.png")); // 读取图像文件
        JLabel label = new JLabel(new ImageIcon(image)); // 将图像添加到标签中
        JFrame frame = new JFrame(); // 创建窗口
        frame.add(label); // 将标签添加到窗口中
        frame.setTitle("请使用网易云APP扫码"); // 设置窗口标题
        frame.setLocationRelativeTo(null); // 将窗口居中显示
        frame.setIconImage(ImageIO.read(new URL("https://hoppinzq.com/api/static/picture/chatGPT.png")));
        frame.setSize(900, 600); // 设置窗口大小为图像大小
        frame.pack(); // 根据标签大小调整窗口大小
        frame.setVisible(true); // 显示窗口
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置窗口关闭操作

        Thread.sleep(10000);
        frame.dispose();
    }
}
