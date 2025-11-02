package com.hoppinzq;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Vector;

public class SSHBean {

    // 私有的对象
    //private static SSHBean sshBean;
    private static final int DEFAULT_PORT = 22;// 默认端口号
    private static int port = -1;// 端口号
    private static String ipAddress = "";// ip地址
    private static String userName = "";// 账号
    // zhangqi被禁用talnet了
    private static String password = "";// 密码

    private Session session;// JSCH session
    private boolean logined = false;// 是否登陆

    public SSHBean() {
        this(ipAddress, DEFAULT_PORT, userName, password);
    }


    // 懒汉式,线程安全,适合多线程
//    public static synchronized SSHBean getInstance() {
//        if (sshBean == null) {
//            sshBean = new SSHBean();
//        }
//        return sshBean;
//    }

    /**
     * 构造方法,可以直接使用DEFAULT_PORT
     *
     * @param ipAddress
     * @param userName
     * @param password
     */
    public SSHBean(String ipAddress, String userName, String password) {
        this(ipAddress, DEFAULT_PORT, userName, password);
    }

    /**
     * 构造方法,方便直接传入ipAddress,userName,password进行调用
     *
     * @param ipAddress
     * @param port
     * @param userName
     * @param password
     */
    public SSHBean(String ipAddress, int port, String userName, String password) {
        super();
        this.ipAddress = ipAddress;
        this.userName = userName;
        this.password = password;
        this.port = port;
    }

    /**
     * 对将要执行的linux的命令进行遍历
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static String processDataStream(InputStream in) throws Exception {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String result = "";
        try {
            while ((result = br.readLine()) != null) {
                sb.append(result + System.getProperty("line.separator"));
                // System.out.println(sb.toString());
            }
        } catch (Exception e) {
            throw new Exception("获取数据流失败: " + e);
        } finally {
            br.close();
        }
        return sb.toString();
    }

    public void sshRemoteLogin() throws Exception {
        sshRemoteLogin(ipAddress, userName, password);
    }

    public void closeSession() {
        // 调用session的关闭连接的方法
        if (session != null) {
            // 如果session不为空,调用session的关闭连接的方法
            session.disconnect();
        }
    }

    public void sshRemoteLogin(String ipAddress, String userName, String password) throws Exception {
        // 如果登陆就直接返回
        if (logined) {
            return;
        }
        // 创建jSch对象
        JSch jSch = new JSch();
        try {
            // 获取到jSch的session, 根据用户名、主机ip、端口号获取一个Session对象
            session = jSch.getSession(userName, ipAddress, DEFAULT_PORT);
            // 设置密码
            session.setPassword(password);

            // 方式一,通过Session建立连接
            // session.setConfig("StrictHostKeyChecking", "no");
            // session.connect();

            // 方式二,通过Session建立连接
            // java.util.Properties;
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);// 为Session对象设置properties
            // session.setTimeout(3000);// 设置超时
            session.connect();//// 通过Session建立连接
            // 设置登陆状态
            logined = true;
        } catch (JSchException e) {
            // 设置登陆状态为false
            logined = false;
            throw new Exception(
                    "主机登录失败, IP = " + ipAddress + ", USERNAME = " + userName + ", Exception:" + e.getMessage());
        }
    }

    /**
     * 列出文件
     *
     * @param directory
     * @return
     * @throws JSchException
     * @throws SftpException
     */
    public Vector listFiles(String directory) throws JSchException, SftpException {
        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        // 远程连接
        channelSftp.connect();
        // 显示目录信息
        Vector ls = channelSftp.ls(directory);
        System.out.println("5、" + ls);
        // 切断连接
        channelSftp.exit();
        return ls;
    }

    /**
     * 执行相关的命令
     *
     * @param command
     * @throws IOException
     */
    public String execCommand(String command) throws IOException {
        InputStream in = null;// 输入流(读)
        Channel channel = null;// 定义channel变量
        String processDataStream = null;
        try {
            // 如果命令command不等于null
            if (command != null) {
                // 打开channel
                //说明：exec用于执行命令;sftp用于文件处理
                channel = session.openChannel("exec");
                // 设置command
                ((ChannelExec) channel).setCommand(command);
                // channel进行连接
                channel.connect();
                // 获取到输入流
                in = channel.getInputStream();
                // 执行相关的命令
                processDataStream = processDataStream(in);
            }
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
        return processDataStream;
    }

    public String execCommand2(String cmd) {
        JSch jSch = new JSch();
        ChannelExec channelExec = null;
        BufferedReader inputStreamReader = null;
        BufferedReader errInputStreamReader = null;
        StringBuilder runLog = new StringBuilder("");
        StringBuilder errLog = new StringBuilder("");
        try {

            // 2. 通过 exec 方式执行 shell 命令
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(cmd);
            channelExec.connect();  // 执行命令

            // 3. 获取标准输入流
            inputStreamReader = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            // 4. 获取标准错误输入流
            errInputStreamReader = new BufferedReader(new InputStreamReader(channelExec.getErrStream()));

            // 5. 记录命令执行 log
            String line = null;
            while ((line = inputStreamReader.readLine()) != null) {
                System.err.println(line);
                runLog.append(line).append("\n");
            }

            // 6. 记录命令执行错误 log
            String errLine = null;
            while ((errLine = errInputStreamReader.readLine()) != null) {
                errLog.append(errLine).append("\n");
            }

            // 7. 输出 shell 命令执行日志
            System.out.println("exitStatus=" + channelExec.getExitStatus() + ", openChannel.isClosed="
                    + channelExec.isClosed());
            System.out.println("命令执行完成，执行日志如下:");
            System.out.println(runLog.toString());
            System.out.println("命令执行完成，执行错误日志如下:");
            System.out.println(errLog.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (errInputStreamReader != null) {
                    errInputStreamReader.close();
                }

                if (channelExec != null) {
                    channelExec.disconnect();
                }
                if (session != null) {
                    session.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return runLog.toString();
    }

    public Boolean isLogin() {
        return logined;
    }

    public void setLogin(boolean le) {
        logined = le;
    }

    public Session getSession() {
        if (session.isConnected()) {
            return session;
        }
        return null;
    }

    public String getIPAddress() {
        return ipAddress;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port == -1 ? DEFAULT_PORT : port;
    }
}
