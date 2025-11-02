package com.hoppinzq.mcp.service;

import com.hoppinzq.mcp.bean.MCPResponse;
import com.hoppinzq.mcp.bean.SSHConnection;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

@Service
public class SSHService {

    /**
     * 上传本地文件到远程服务器
     * <p>
     * 该函数通过SFTP协议将本地文件上传到远程服务器的指定目录。
     * 如果目标目录不存在，会自动创建。上传成功后返回访问URL。
     *
     * @param filePath      要上传文件的绝对路径列表，可以指定多个文件
     * @param targetDirName 远程服务器的目标文件夹名称
     * @return MCPResponse 包含操作结果的响应对象：
     * - 成功时返回成功消息和文件访问URL
     * - 失败时返回错误信息
     */
    @Tool(name = "upload_file", description = "上传本地文件到远程服务器，也可以用做部署前端项目")
    public MCPResponse uploadFile(@ToolParam(description = "要上传文件的绝对路径，可以指定多个文件") List<String> filePath,
                                  @ToolParam(description = "远程服务器的目标文件夹名称") String targetDirName) {
        SSHConnection sshConnection = null;
        ChannelSftp channelSftp = null;
        try {
            sshConnection = new SSHConnection();
            Session sshSession = sshConnection.getSession();
            channelSftp = (ChannelSftp) sshSession.openChannel("sftp");
            channelSftp.connect();
            String remoteDirPath = "/home/file/file/trae/"; // nginx 配置的目录
            /**
             * 进入nginx配置的目录，查找是否存在目标文件夹，不存在则创建
             */
            Vector<ChannelSftp.LsEntry> lsEntries = channelSftp.ls(remoteDirPath);
            boolean folderExists = false;
            for (ChannelSftp.LsEntry entry : lsEntries) {
                if (entry.getFilename().equals(targetDirName)) {
                    folderExists = true;
                    break;
                }
            }
            if (!folderExists) {
                channelSftp.mkdir(remoteDirPath + targetDirName);
            }
            for (String path : filePath) {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    String dst = remoteDirPath + targetDirName + "/" + file.getName();
                    InputStream inputStream = new FileInputStream(file);
                    channelSftp.put(inputStream, dst);
                } else {
                    return MCPResponse.fail("文件不存在或不是一个有效的文件：" + path);
                }
            }
            return MCPResponse.success("文件上传成功，访问路径：https://hoppinzq.com/trae/" + targetDirName + "/");
        } catch (Exception e) {
            return MCPResponse.fail("上传失败：" + e.getMessage());
        } finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.exit();
                channelSftp.quit();
            }
            if (sshConnection != null) {
                sshConnection.closeSession();
            }
        }
    }
}


