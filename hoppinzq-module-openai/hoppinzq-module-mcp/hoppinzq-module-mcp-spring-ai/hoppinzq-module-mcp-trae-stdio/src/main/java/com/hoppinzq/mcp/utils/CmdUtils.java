package com.hoppinzq.mcp.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author: zq
 */
public class CmdUtils {

    public static String startProcess(String cmd) {
        StringBuilder sbSuccess = new StringBuilder();
        StringBuilder sbError = new StringBuilder();
        try {
            String line;
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd);
            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
            while ((line = bufferedReader1.readLine()) != null) {
                sbSuccess.append(line);
            }
            bufferedReader1.close();
            while ((line = bufferedReader2.readLine()) != null) {
                sbError.append(line);
            }
            bufferedReader2.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            sbError.append("执行命令失败: ");
            sbError.append(e.getMessage());
        }
        if (sbError.length() > 0) {
            sbError.append("\n错误信息: ");
            sbError.append(sbError);
            return sbError.toString();
        }
        return sbSuccess.toString();
    }
}
