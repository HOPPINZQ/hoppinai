package com.hoppinzq.mcp.bean;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.util.Properties;

/**
 * 这个类需要维护会话状态并进行操作，不适合使用record。record更适合纯数据传输对象。
 */
public class SSHConnection {

    private final int DEFAULT_PORT = 22;

    private String ipAddress;
    private String userName;
    private String password;

    private Session session;

    public SSHConnection() {
        this("", "root", "");
    }

    public SSHConnection(String ipAddress, String userName, String password) {
        this.ipAddress = ipAddress;
        this.userName = userName;
        this.password = password;
        try {
            sshRemoteLogin(ipAddress, userName, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sshRemoteLogin() throws Exception {
        sshRemoteLogin(ipAddress, userName, password);
    }

    public void closeSession() {
        if (session != null) {
            session.disconnect();
        }
    }

    public void sshRemoteLogin(String ipAddress, String userName, String password) throws Exception {
        JSch jSch = new JSch();
        try {
            session = jSch.getSession(userName, ipAddress, DEFAULT_PORT);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
        } catch (JSchException e) {
            throw new Exception("主机登录失败, IP = " + ipAddress + ", 用户名 = " + userName + ", 原因是:" + e.getMessage());
        }
    }

    public Session getSession() {
        if (session.isConnected()) {
            return session;
        }
        return null;
    }
}
