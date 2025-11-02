package com.hoppinzq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MyAsyncServer {

    private static final Logger logger = LoggerFactory.getLogger(MyAsyncServer.class);

    private static final String wroot = "D:\\myProject\\github\\hoppin-ai\\";
    private static final String lroot = "/home/hoppinzq/";


    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        StdioServerTransportProvider transportProvider = new StdioServerTransportProvider(new ObjectMapper());
        McpAsyncServer asyncServer = McpServer.async(transportProvider)
                .serverInfo("异步任务MCP", "1.0.0")
                .capabilities(ServerCapabilities.builder()
                        .tools(true)
                        .logging()
                        .build())
                .build();
        try {
            addBuildJarTool(asyncServer);
            addCheckJarExistTool(asyncServer);
            addUploadJarTool(asyncServer);
            addCheckRemoteJarFileTool(asyncServer);
            addCheckShFileTool(asyncServer);
            addCreateShFileTool(asyncServer);
            addRunJarFileTool(asyncServer);
            addCheckRemoteJarEnableTool(asyncServer);
            addShowJVMInfoTool(asyncServer);
            addEndJVMTool(asyncServer);
            addExportHeadTool(asyncServer);
            addGenLogHtmlTool(asyncServer);
            analysisHeadTool(asyncServer);
            // asyncServer.close();
        } catch (JsonProcessingException e) {
            asyncServer.loggingNotification(LoggingMessageNotification.builder()
                    .level(LoggingLevel.INFO)
                    .logger("custom-logger")
                    .data("创建JSON Schema时发生错误: " + e.getMessage())
                    .build());
        }
    }

    private static void addBuildJarTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = createBuildJarSchema();
        McpServerFeatures.AsyncToolSpecification buildJarTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("build_local_jar", "本地构建jar包，在构建jar包前，需要先使用check_local_jar_exist来检查jar是否存在，若不存在则直接构建。" +
                        "若存在，则检查isForce的值，若为ture则重新构建，false则无需构建。", schema),
                (exchange, arguments) -> {
                    try {
                        String projectName = (String) arguments.get("projectName");
                        String cmdJSON = "cmd /c cd " + wroot + projectName + " && mvn package ";
                        Runtime runtime = Runtime.getRuntime();
                        Process process = runtime.exec(cmdJSON);

                        try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
                             BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream(), "UTF-8"))) {

                            String stdLine;
                            while ((stdLine = stdInput.readLine()) != null) {
                                String finalStdLine = stdLine;
                                Mono defer = Mono.defer(() -> Mono.just(new CallToolResult(List.of(
                                        new TextContent(finalStdLine)
                                ), false)));
                                logger.info(finalStdLine);
                                if (stdLine.contains("BUILD SUCCESS")) {
                                    return Mono.just(new CallToolResult(List.of(
                                            new TextContent("打包成功，可以继续执行操作")
                                    ), false));
                                }
                            }
                            String errorLine;
                            while ((errorLine = stdError.readLine()) != null) {
                                return Mono.just(new CallToolResult(List.of(
                                        new TextContent("打包失败，请检查pom文件")
                                ), true));
                            }
                        }
                        int exitVal = process.waitFor();
                        if (exitVal == 0) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("打包成功，可以继续执行操作")
                            ), false));
                        } else {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("打包失败，请检查pom文件")
                            ), true));
                        }
                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("检查jar错误: " + e.getMessage())
                        ), true));
                    }
                });
        asyncServer.addTool(buildJarTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addCheckJarExistTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = createCheckJarSchema();
        McpServerFeatures.AsyncToolSpecification checkJarTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("check_local_jar_exist", "检查本地jar包是否存在", schema),
                (exchange, arguments) -> {
                    List<Content> result = new ArrayList<>();
                    try {
                        String projectName = (String) arguments.get("projectName");
                        String targetDir = wroot + projectName + "\\target\\";
                        File file = new File(targetDir);
                        if (file.exists()) {
                            // 遍历子文件
                            File[] files = file.listFiles();
                            for (File f : files) {
                                if (f.getName().endsWith(".jar")) {
                                    return Mono.just(new CallToolResult(List.of(
                                            new TextContent("jar包存在，可以继续执行操作")
                                    ), false));
                                }
                            }
                        } else {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("项目目录不存在，请先打包后再执行此操作")
                            ), true));
                        }
                    } catch (Exception e) {
                        result.add(new TextContent("检查jar包失败: " + e.getMessage()));
                        return Mono.just(new CallToolResult(result, true));
                    }
                    return Mono.just(new CallToolResult(result, false));
                });
        asyncServer.addTool(checkJarTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addUploadJarTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = createUploadJarSchema();
        McpServerFeatures.AsyncToolSpecification uploadJarTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("upload_local_jar", "上传本地jar包到远程服务器，上传前需要使用check_local_jar_exist检验jar包是否存在，若存在直接上传。若不存在，先使用build_local_jar构建jar包", schema),
                (exchange, arguments) -> {
                    List<Content> result = new ArrayList<>();
                    try {
                        String projectName = (String) arguments.get("projectName");
                        SSHBean sshBean = new SSHBean();
                        sshBean.sshRemoteLogin();
                        Session sshSession = sshBean.getSession();
                        ChannelSftp channelSftp = (ChannelSftp) sshSession.openChannel("sftp");
                        channelSftp.connect();
                        Vector<ChannelSftp.LsEntry> lsEntries = channelSftp.ls(lroot);
                        boolean folderExists = false;
                        for (ChannelSftp.LsEntry entry : lsEntries) {
                            if (entry.getFilename().equals(projectName)) {
                                folderExists = true;
                                break;
                            }
                        }
                        if (folderExists) {
                            //文件夹存在，不进行任何操作
                        } else {
                            //文件夹不存在，新建文件夹
                            channelSftp.mkdir(lroot + projectName);
                        }
                        File jarFileDir = new File(wroot + projectName + "\\target");
                        String jarName = "";
                        File[] files = jarFileDir.listFiles();
                        for (File f : files) {
                            if (f.getName().endsWith(".jar")) {
                                jarName = f.getName();
                            }
                        }
                        String jarFileName = jarName.substring(0, jarName.lastIndexOf(".jar"));
                        String dst = lroot + projectName + "/" + jarName; // 目标文件名
                        File jarFile = new File(wroot + projectName + "\\target\\" + jarName);
                        InputStream inputStream = new FileInputStream(jarFile);
                        //jar包上传
                        channelSftp.put(inputStream, dst, new FileProgressMonitor(jarFile.length()));
                        channelSftp.exit();
                        channelSftp.quit();
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("jar包上传成功，jar包名称：" + jarFileName + "，远程路径：" + dst + "，可以继续执行操作")
                        ), false));
                    } catch (Exception e) {
                        result.add(new TextContent("上传jar失败: " + e.getMessage()));
                        return Mono.just(new CallToolResult(result, true));
                    }
                });
        asyncServer.addTool(uploadJarTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addCheckRemoteJarFileTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = createCheckJarRemoteFileSchema();
        McpServerFeatures.AsyncToolSpecification checkShTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("check_remote_jar_file_exist", "检查远程服务器的jar包是否存在，若不存在，提示用户jar包不存在，是否需要上传本地jar包", schema),
                (exchange, arguments) -> {
                    try {
                        String projectName = (String) arguments.get("projectName");
                        SSHBean sshBean = new SSHBean();
                        sshBean.sshRemoteLogin();
                        Session sshSession = sshBean.getSession();
                        ChannelSftp channelSftp = (ChannelSftp) sshSession.openChannel("sftp");
                        channelSftp.connect();
                        String remoteJarName = getRemoteJarName(channelSftp, projectName);
                        if (remoteJarName == null) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("远程jar包不存在，请先上传jar包后再执行此操作")
                            ), true));
                        }
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("远程的jar文件存在，可以继续执行操作")
                        ), false));
                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("远程jar包不存在，请先上传jar包后再执行此操作")
                        ), true));
                    }
                });
        asyncServer.addTool(checkShTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addCheckShFileTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = createCheckShFileSchema();
        McpServerFeatures.AsyncToolSpecification checkShTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("check_remote_sh_file_exist", "检查远程服务器的sh文件是否存在，先使用check_remote_jar_file_exist检查远程服务器的jar包是否存在，若不存在，提示用户jar不存在。若jar存在，继续检查sh文件是否存在。", schema),
                (exchange, arguments) -> {
                    try {
                        String projectName = (String) arguments.get("projectName");
                        SSHBean sshBean = new SSHBean();
                        sshBean.sshRemoteLogin();
                        Session sshSession = sshBean.getSession();
                        ChannelSftp channelSftp = (ChannelSftp) sshSession.openChannel("sftp");
                        channelSftp.connect();
                        String remoteJarName = getRemoteJarName(channelSftp, projectName);
                        String jarFileName = remoteJarName.substring(0, remoteJarName.lastIndexOf(".jar"));
                        String shName = lroot + projectName + "/" + "deploy_" + jarFileName + ".sh";
                        logger.info("检查sh文件路径: {}", shName);
                        channelSftp.stat(shName);
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("sh文件存在，可以继续执行操作")
                        ), false));
                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("sh文件不存在，请先创建sh文件后再执行此操作")
                        ), true));
                    }
                });
        asyncServer.addTool(checkShTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addCreateShFileTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = createBuildShFileSchema();
        McpServerFeatures.AsyncToolSpecification checkShTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("create_remote_sh_file", "在远程服务器创建sh文件，在创建sh文件前，需要先使用check_remote_sh_file_exist来检查sh文件是否存在，若不存在则直接创建", schema),
                (exchange, arguments) -> {
                    try {
                        String projectName = (String) arguments.get("projectName");
                        boolean isJMX = false;
                        boolean isDebugger = false;
                        int jmxPort = 0;
                        int debuggerPort = 0;
                        String logFile = "rizhi.log";
                        if (arguments.get("isJMX") != null) {
                            isJMX = (boolean) arguments.get("isJMX");
                            jmxPort = 9003;
                        }
                        if (arguments.get("isDebugger") != null) {
                            isDebugger = (boolean) arguments.get("isDebugger");
                            debuggerPort = 9004;
                        }
                        if (arguments.get("logFile") != null) {
                            logFile = (String) arguments.get("logFile");
                        }
                        SSHBean sshBean = new SSHBean();
                        sshBean.sshRemoteLogin();
                        Session sshSession = sshBean.getSession();
                        ChannelSftp channelSftp = (ChannelSftp) sshSession.openChannel("sftp");
                        channelSftp.connect();
                        String remoteJarName = getRemoteJarName(channelSftp, projectName);
                        if (remoteJarName == null) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("远程jar包不存在，请先上传jar包后再执行此操作")
                            ), true));
                        }
                        String jarFileName = remoteJarName.substring(0, remoteJarName.lastIndexOf(".jar"));
                        String shName = lroot + projectName + "/" + "deploy_" + jarFileName + ".sh";
                        String jmxsh = "";
                        String debuggersh = "";
                        if (isJMX && jmxPort > 0) {
                            jmxsh = " -Djava.rmi.server.hostname=43.142.242.237 -Dcom.sun.management.jmxremote.port=" + jmxPort + " -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false ";
                        }
                        if (isDebugger && debuggerPort > 0) {
                            debuggersh = " -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=" + debuggerPort + " ";
                        }
                        String shContent = "SERVER_NAME=" + remoteJarName + " #服务jar包名称\n" +
                                "pid=$(ps -ef|grep $SERVER_NAME|grep -v grep|awk '{print $2}');\n" +
                                "if [  -n  \"$pid\"  ];  then\n" +
                                "  kill  -9  $pid;\n" +
                                "fi\n" +
                                "nohup java -jar " + jmxsh + "  -Dspring.profiles.active=dev " + debuggersh + " $SERVER_NAME  >  " + logFile + ".log &\n" +
                                " \n";
                        channelSftp.put(new ByteArrayInputStream(shContent.getBytes()), shName);
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("sh文件创建成功，路径为：" + shName)
                        ), false));
                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("sh文件生成错误：" + e.getMessage())
                        ), true));
                    }
                });
        asyncServer.addTool(checkShTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addRunJarFileTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = createRunJarSchema();
        McpServerFeatures.AsyncToolSpecification checkShTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("run_remote_jar", "在远程服务器启动java项目，先使用check_remote_sh_file_exist检查一下远程服务器的jar包和sh文件是否存在，若不存在，提醒用户jar或sh文件不存在。", schema),
                (exchange, arguments) -> {
                    try {
                        String projectName = (String) arguments.get("projectName");
                        SSHBean sshBean = new SSHBean();
                        sshBean.sshRemoteLogin();
                        Session sshSession = sshBean.getSession();
                        ChannelSftp channelSftp = (ChannelSftp) sshSession.openChannel("sftp");
                        ChannelExec channelExec = (ChannelExec) sshBean.getSession().openChannel("exec");
                        channelSftp.connect();
                        String remoteJarName = getRemoteJarName(channelSftp, projectName);
                        if (remoteJarName == null) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("远程jar包不存在，请先上传jar包后再执行此操作")
                            ), true));
                        }
                        String jarFileName = remoteJarName.substring(0, remoteJarName.lastIndexOf(".jar"));
                        String shName = lroot + projectName + "/" + "deploy_" + jarFileName + ".sh";
                        logger.info("检查sh文件路径: {}", shName);
                        channelSftp.stat(shName);
                        deleteLogFile(channelSftp, projectName);
                        channelExec.setCommand("cd .. && cd " + lroot + projectName + " && sh deploy_" + jarFileName + ".sh");
                        channelExec.connect();
                        String processDataStream = getCommand("ps -ef|grep " + jarFileName + ".jar|grep -v grep|awk '{print $2}'");
                        if (processDataStream == null || "".equals(processDataStream) || "error".equals(processDataStream)) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("部署失败,可能的原因是jar包损坏或者打包有问题")
                            ), true));
                        } else {
                            processDataStream = processDataStream.split(System.lineSeparator())[0];
                            int i = Integer.parseInt(processDataStream);
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("部署成功，进程ID为：" + i)
                            ), false));
                        }
                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("sh文件不存在，请先创建sh文件后再执行此操作")
                        ), true));
                    }
                });
        asyncServer.addTool(checkShTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addCheckRemoteJarEnableTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = checkRemoteJarEnableSchema();
        McpServerFeatures.AsyncToolSpecification checkShTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("check_remote_jar_enable", "检查远程服务器的java服务是否可用，是否在启动状态。在检查启动状态前，先使用check_remote_jar_file_exist检查jar包是否存在，若不存在，无需检查，直接提示java服务不可用。", schema),
                (exchange, arguments) -> {
                    try {
                        String projectName = (String) arguments.get("projectName");
                        SSHBean sshBean = new SSHBean();
                        sshBean.sshRemoteLogin();
                        Session sshSession = sshBean.getSession();
                        ChannelSftp channelSftp = (ChannelSftp) sshSession.openChannel("sftp");
                        channelSftp.connect();
                        String remoteJarName = getRemoteJarName(channelSftp, projectName);
                        if (remoteJarName == null) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("远程jar包不存在，请先上传jar包后再执行此操作")
                            ), true));
                        }
                        String pid = getPid(remoteJarName);
                        if (pid == null) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("java服务不可用")
                            ), true));
                        }
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("java服务可用，进程ID为：" + pid)
                        ), false));
                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("java服务不可用用")
                        ), true));
                    }
                });
        asyncServer.addTool(checkShTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addShowJVMInfoTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = showJVMInfoSchema();
        McpServerFeatures.AsyncToolSpecification checkShTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("show_jvm_info", "打印远程服务器的java服务的jvm参数，先使用check_remote_jar_enable检查远程服务器的java服务是否可用，若不可用，提示用户，否则，正常打印", schema),
                (exchange, arguments) -> {
                    try {
                        String projectName = (String) arguments.get("projectName");
                        SSHBean sshBean = new SSHBean();
                        sshBean.sshRemoteLogin();
                        Session sshSession = sshBean.getSession();
                        ChannelSftp channelSftp = (ChannelSftp) sshSession.openChannel("sftp");
                        channelSftp.connect();
                        String remoteJarName = getRemoteJarName(channelSftp, projectName);
                        if (remoteJarName == null) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("远程jar包不存在，请先上传jar包后再执行此操作")
                            ), true));
                        }
                        String pid = getPid(remoteJarName);
                        if (pid == null) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("java服务不可用")
                            ), true));
                        }
                        String processDataStream = getCommand("jcmd " + pid + " VM.command_line");
                        if (processDataStream == null || "".equals(processDataStream) || "error".equals(processDataStream)) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("获取失败,可能的原因是jar包损坏或者打包有问题")
                            ), true));
                        }
                        String[] jvms = processDataStream.split(System.lineSeparator());
                        if (jvms.length <= 1) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("获取失败,可能的原因是服务已终止")
                            ), true));
                        }
                        List<Content> results = new ArrayList<>();
                        results.add(new TextContent("java服务可用，进程ID为：" + pid));
                        for (String jvm : jvms) {
                            results.add(new TextContent(jvm));
                        }
                        return Mono.just(new CallToolResult(results, false));

                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("获取远程服务jvm参数失败")
                        ), true));
                    }
                });
        asyncServer.addTool(checkShTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addEndJVMTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = endRemoteJarSchema();
        McpServerFeatures.AsyncToolSpecification checkShTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("end_remote_jar", "终止远程服务器的java进程，先使用check_remote_jar_enable检查远程服务器的java服务是否正常运行，若java服务正常运行，直接使用check_remote_jar_enable打印的pid作为pid的入参。" +
                        "若java服务不正常运行或者pid为null，则提示用户java服务没有在运行", schema),
                (exchange, arguments) -> {
                    try {
                        String pid = (String) arguments.get("pid");
                        getCommand("kill -s 9 " + pid);
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("终止java服务成功，pid：" + pid)
                        ), false));

                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("终止服务失败")
                        ), true));
                    }
                });
        asyncServer.addTool(checkShTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addExportHeadTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = exportHeadSchema();
        McpServerFeatures.AsyncToolSpecification checkShTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("export_head", "导出远程服务器的java服务的堆栈文件，先使用check_remote_jar_enable检查远程服务器的java服务是否正常运行，若java服务正常运行，直接使用check_remote_jar_enable打印的pid作为pid的入参。" +
                        "若java服务不正常运行或者pid为null，则提示用户java服务没有在运行", schema),
                (exchange, arguments) -> {
                    try {
                        String projectName = (String) arguments.get("projectName");
                        String pid = (String) arguments.get("pid");
                        String heapdumpFileName = lroot + projectName + "/" + projectName + "_heapdump.hprof";
                        String processDataStream = getCommand("jcmd " + pid + " GC.heap_dump " + heapdumpFileName);
                        if ("".equals(processDataStream) || "error".equals(processDataStream)) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("获取失败,可能的原因是jar包损坏或者打包有问题")
                            ), true));
                        }
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("导出堆栈文件成功，堆栈文件路径：" + heapdumpFileName)
                        ), false));

                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("终止服务失败")
                        ), true));
                    }
                });
        asyncServer.addTool(checkShTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void analysisHeadTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = analysisHeadSchema();
        McpServerFeatures.AsyncToolSpecification checkShTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("analysis_head", "分析远程服务器java服务的堆内存摘要，先使用check_remote_jar_enable检查远程服务器的java服务是否正常运行，若java服务正常运行，直接使用check_remote_jar_enable打印的pid作为pid的入参。" +
                        "若java服务不正常运行或者pid为null，则提示用户java服务没有在运行。若成功返回了堆内存数据，尝试帮助用户分析。", schema),
                (exchange, arguments) -> {
                    try {
                        String pid = (String) arguments.get("pid");
                        String processDataStream = getCommand("jcmd " + pid + " GC.heap_info");
                        if (processDataStream == null || "".equals(processDataStream) || "error".equals(processDataStream)) {
                            return Mono.just(new CallToolResult(List.of(
                                    new TextContent("获取失败,可能的原因是jar包损坏或者打包有问题")
                            ), true));
                        }
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("获取堆内存摘要数据成功"),
                                new TextContent(processDataStream)
                        ), false));

                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("终止服务失败")
                        ), true));
                    }
                });
        asyncServer.addTool(checkShTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static void addGenLogHtmlTool(McpAsyncServer asyncServer) throws JsonProcessingException {
        String schema = genLogHtmlSchema();
        McpServerFeatures.AsyncToolSpecification checkShTool = new McpServerFeatures.AsyncToolSpecification(
                new Tool("gen_log_html", "生成查看远程服务器的java服务的日志的html代码，如果正确生成了html代码，打印给用户", schema),
                (exchange, arguments) -> {
                    try {
                        String projectName = (String) arguments.get("projectName");
                        SSHBean sshBean = new SSHBean();
                        sshBean.sshRemoteLogin();
                        Session sshSession = sshBean.getSession();
                        ChannelSftp channelSftp = (ChannelSftp) sshSession.openChannel("sftp");
                        channelSftp.connect();
                        // 获取最新的。log文件
                        Vector<ChannelSftp.LsEntry> lsEntries = channelSftp.ls(lroot + projectName);
                        for (ChannelSftp.LsEntry entry : lsEntries) {
                            if (entry.getFilename().endsWith(".log")) {
                                String logPath = lroot + projectName + "/" + entry.getFilename();
                                return Mono.just(new CallToolResult(List.of(
                                        new TextContent(getWebHtmlTemp().replace("zq_rizhiPath_$$$", logPath))
                                ), false));
                            }
                        }
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("没有找到日志文件，请先上传日志文件")
                        ), true));
                    } catch (Exception e) {
                        return Mono.just(new CallToolResult(List.of(
                                new TextContent("终止服务失败")
                        ), true));
                    }
                });
        asyncServer.addTool(checkShTool)
                .doOnSuccess(v -> logger.info("tool注册成功"))
                .subscribe();
    }

    private static String getCommand(String command) throws Exception {
        InputStream in = null;
        ChannelExec newChannelExec = null;
        try {
            SSHBean newSsh = new SSHBean();
            newSsh.sshRemoteLogin();
            newChannelExec = (ChannelExec) newSsh.getSession().openChannel("exec");
            newChannelExec.setCommand(command);
            newChannelExec.connect();
            in = newChannelExec.getInputStream();
            return processDataStream(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (in != null) {
                in.close();
            }
            if (newChannelExec != null) {
                newChannelExec.disconnect();
            }
        }
    }

    private static String processDataStream(InputStream in) throws Exception {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String result = "";
        try {
            while ((result = br.readLine()) != null) {
                sb.append(result).append(System.lineSeparator());
            }
        } catch (Exception e) {
            throw new Exception("获取数据流失败: " + e);
        } finally {
            br.close();
        }
        return sb.toString();
    }

    private static String getRemoteJarName(ChannelSftp channelSftp, String projectName) throws SftpException {
        Vector<ChannelSftp.LsEntry> lsEntries = channelSftp.ls(lroot + projectName);
        for (ChannelSftp.LsEntry entry : lsEntries) {
            if (entry.getFilename().endsWith(".jar")) {
                return entry.getFilename();
            }
        }
        return null;
    }

    private static void deleteLogFile(ChannelSftp channelSftp, String projectName) throws SftpException {
        Vector<ChannelSftp.LsEntry> lsEntries = channelSftp.ls(lroot + projectName);
        for (ChannelSftp.LsEntry entry : lsEntries) {
            if (entry.getFilename().endsWith(".log")) {
                String logFileName = entry.getFilename();
                channelSftp.rm(lroot + projectName + "/" + logFileName);
            }
        }
    }

    private static String getPid(String jarName) throws Exception {
        String strJC = getCommand("ps -ef|grep " + jarName + "|grep -v grep|awk '{print $2}'");
        if (strJC == null || "".equals(strJC) || "error".equals(strJC)) {
            return null;
        } else {
            strJC = strJC.split(System.lineSeparator())[0];
            return strJC;
        }
    }

    private static String createCheckJarSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:check_local_jar_exist");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        propertiesNode.set("projectName", projectNameNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("projectName"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String createBuildJarSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:build_local_jar");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        ObjectNode forceFileNode = mapper.createObjectNode();
        forceFileNode.put("type", "boolean");
        forceFileNode.put("description", "是否重新打包");
        forceFileNode.put("default", false);
        propertiesNode.set("projectName", projectNameNode);
        propertiesNode.set("isForce", forceFileNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("projectName").add("isForce"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String createUploadJarSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:upload_local_jar");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        propertiesNode.set("projectName", projectNameNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("projectName"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String createCheckJarRemoteFileSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:check_remote_jar_file_exist");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        propertiesNode.set("projectName", projectNameNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("projectName"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String createCheckShFileSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:check_remote_sh_file_exist");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        propertiesNode.set("projectName", projectNameNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("projectName"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String createBuildShFileSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:create_remote_sh_file");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        ObjectNode isJMXNode = mapper.createObjectNode();
        isJMXNode.put("type", "boolean");
        isJMXNode.put("description", "是否开启jmx监控");
        isJMXNode.put("default", false);
        ObjectNode isDebuggerNode = mapper.createObjectNode();
        isDebuggerNode.put("type", "boolean");
        isDebuggerNode.put("description", "是否开启远程调试");
        isDebuggerNode.put("default", false);
        ObjectNode logFileNode = mapper.createObjectNode();
        logFileNode.put("type", "string");
        logFileNode.put("description", "日志文件名称");
        logFileNode.put("default", "rezhi");
        propertiesNode.set("projectName", projectNameNode);
        propertiesNode.set("isJMX", isJMXNode);
        propertiesNode.set("isDebugger", isDebuggerNode);
        propertiesNode.set("logFile", logFileNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("projectName"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String createRunJarSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:run_remote_jar");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        propertiesNode.set("projectName", projectNameNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("projectName"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String checkRemoteJarEnableSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:check_remote_jar_enable");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        propertiesNode.set("projectName", projectNameNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("projectName"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String showJVMInfoSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:show_jvm_info");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        propertiesNode.set("projectName", projectNameNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("projectName"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String endRemoteJarSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:end_remote_jar");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode pidNode = mapper.createObjectNode();
        pidNode.put("type", "string");
        pidNode.put("description", "pid进程号");
        propertiesNode.set("pid", pidNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("pid"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String exportHeadSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:export_head");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        propertiesNode.set("projectName", projectNameNode);
        ObjectNode pidNode = mapper.createObjectNode();
        pidNode.put("type", "string");
        pidNode.put("description", "pid进程号");
        propertiesNode.set("pid", pidNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("pid").add("projectName"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String analysisHeadSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:analysis_head");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode pidNode = mapper.createObjectNode();
        pidNode.put("type", "string");
        pidNode.put("description", "pid进程号");
        propertiesNode.set("pid", pidNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("pid"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String genLogHtmlSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "uri:jsonschema:gen_log_html");
        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode projectNameNode = mapper.createObjectNode();
        projectNameNode.put("type", "string");
        projectNameNode.put("description", "项目名称");
        propertiesNode.set("projectName", projectNameNode);
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("projectName"));
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    private static String getWebHtmlTemp() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <meta name=\"description\" content=\"\">\n" +
                "    <title>项目日志</title>\n" +
                "    <link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"https://hoppinzq.com/zui/static/favicon/favicon.ico\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://hoppinzq.com/zui/static/css/style.min.css\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://hoppinzq.com/static/css/plugins/highlight.css\">\n" +
                "    <link rel=\"stylesheet\" href=\"https://hoppinzq.com/static/css/plugins/jquery-ui.min.css\">\n" +
                "</head>\n" +
                "<style>\n" +
                "    code{border: 0;border-radius: 0;overflow-x: hidden !important;}\n" +
                "    pre {white-space: pre;word-wrap: break-word;}\n" +
                "    #log{overflow: auto;overflow-y: scroll;height: 500px;overflow-x: hidden;}\n" +
                "    .ui-resizable-helper { border: 2px dotted #000000; }\n" +
                "</style>\n" +
                "<body>\n" +
                "    <section class=\"wrapper content\">\n" +
                "        <div class=\"box\">\n" +
                "            <div class=\"box-header\"><h4 class=\"box-title\">日志</h4></div>\n" +
                "            <div class=\"box-body\"><div id=\"jui\"><pre id=\"log\"></pre></div></div>\n" +
                "            <div class=\"box-footer text-center p-0\"><a href=\"#\" id=\"logss\" class=\"btn btn-block btn-primary-light\">实时展示</a></div>\n" +
                "        </div>\n" +
                "    </section>\n" +
                "<script src=\"https://hoppinzq.com/zui/static/js/vendors.min.js\"></script>\n" +
                "<script src=\"https://hoppinzq.com/static/js/plugins/highlight.min.js\"></script>\n" +
                "<script src=\"https://hoppinzq.com/static/js/plugins/jquery-ui.min.js\"></script>\n" +
                "<script>\n" +
                "    let userno = \"hoppinzq\";\n" +
                "    let rizhiPath = \"zq_rizhiPath_$$$\";\n" +
                "    $.get(`http://hoppin.cn:8899/showlog?rizhiPath=${rizhiPath}`,function (logs) {\n" +
                "        let _logs = logs.split(\"\\r\\n\");\n" +
                "        $.each(_logs, function (index, log) {$(\"#log\").append(`<code class='hljs logs'>${log}</code>`);})\n" +
                "        hljs.initHighlightingOnLoad();\n" +
                "    });\n" +
                "    $( \"#jui\" ).resizable({helper: \"ui-resizable-helper\", alsoResize: \"#log\",});\n" +
                "    //实时日志展示\n" +
                "    $(\"#logss\").on(\"click\",function () {\n" +
                "        $(this).off(\"click\");scrollLog();\n" +
                "        let ws = new WebSocket(`ws://hoppin.cn:8899/websocketProcess/${userno}`);\n" +
                "        ws.onopen = function() {$.get(`http://hoppin.cn:8899/showlogs?rizhiPath=${rizhiPath}&userno=${userno}`);};\n" +
                "        ws.onmessage = function(event) {\n" +
                "            let _msg=JSON.parse(event.data);\n" +
                "            $(\"#log\").append(`<code class='hljs logs'>${_msg.msg}</code>`);\n" +
                "            scrollLog();\n" +
                "        };\n" +
                "    });\n" +
                "    function scrollLog() {let scrollableDiv = document.getElementById(\"log\");scrollableDiv.scrollTop = scrollableDiv.scrollHeight;}\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
    }
}
