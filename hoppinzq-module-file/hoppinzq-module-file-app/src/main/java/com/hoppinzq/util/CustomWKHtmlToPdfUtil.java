package com.hoppinzq.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @program: simple_tools
 * @description: Html2PDF
 * @author: Mr.chen
 * @create: 2020-06-09 09:39
 **/
public class CustomWKHtmlToPdfUtil {
    /**
     * 把html字节数组转换成pdf的字节数组，非线性安全
     *
     * @param src
     * @return
     * @throws IOException
     */
    public static byte[] html2Pdf(byte[] src, String wkhtmlToPdfHome) throws IOException {
        File srcFile = new File(getTmpFilePath(".html"));
        FileUtils.writeByteArrayToFile(srcFile, src);

        String targetFilePath = getTmpFilePath(".pdf");
        File descFile = new File(targetFilePath);
        html2Pdf(srcFile.getAbsolutePath(), descFile.getAbsolutePath(), wkhtmlToPdfHome);
        byte[] result = FileUtils.readFileToByteArray(new File(targetFilePath));
        srcFile.delete();
        descFile.delete();

        return result;
    }

    private static String getTmpFilePath(String suffix) {
        String system = System.getProperty("os.name");
        if (system.contains("Windows")) {
            return "./tmp" + System.currentTimeMillis() + suffix;
        } else if (system.contains("Linux")) {
            return "/tmp/tmp" + System.currentTimeMillis() + suffix;
        } else if (system.contains("Mac OS")) {
            return "/tmp/tmp" + System.currentTimeMillis() + suffix;
        }

        return null;
    }

    public static String html2Pdf(String sourceFilePath, String targetFilePath, String wkhtmlToPdfHome) {
        Process process = null;
        String cmd = getCommand(sourceFilePath, targetFilePath, wkhtmlToPdfHome);
        try {
            process = Runtime.getRuntime().exec(cmd);
            // 这个调用比较关键，就是等当前命令执行完成后再往下执行
            process.waitFor();
//			File file = new File(sourceFilePath);
//			if (file.exists()) {
//				file.delete();
//			}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return targetFilePath;
    }

    public static String getCommand(String sourceFilePath, String targetFilePath, String wkhtmlToPdfHome) {
        String system = System.getProperty("os.name");
        if (system.contains("Windows")) {
            return wkhtmlToPdfHome + "/wkhtmltopdf.exe " + sourceFilePath + " " + targetFilePath;
        } else if (system.contains("Linux")) {
            return wkhtmlToPdfHome + "/wkhtmltopdf " + sourceFilePath + " " + targetFilePath;
        } else if (system.contains("Mac OS")) {
            return wkhtmlToPdfHome + "/wkhtmltopdf " + sourceFilePath + " " + targetFilePath;
        }
        return "";
    }

}
