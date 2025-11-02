package com.hoppinzq.function.zq.ssh;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SshFunctionCall {

    public String msg;
    public Boolean success = false;

    public static void main(String[] args) {
        JSONObject jsonObject = new SshFunctionCall().mvnPackage("yudao-server");
        System.err.println(jsonObject);
    }

    public JSONObject mvnPackage(String projectName) {
        String projectSource = "D:\\myProject\\github\\ruoyi-vue-pro\\" + projectName + "\\";
        String pomSource = projectSource + "pom.xml";
        System.out.println("找到pom：" + pomSource);
        System.err.println("ai打包ing");
        String os = System.getProperty("os.name").toLowerCase();
        JSONObject result = new JSONObject();
        try {
            String cmd = "cmd /c cd " + projectSource + " && mvn package";
            System.out.println(cmd);
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("BUILD SUCCESS")) {
                    this.success = true;
                    this.msg = "打包成功，jar包生成目录：" + projectSource + "target\\" + projectName + ".jar";
                    result.put("success", this.success);
                    result.put("msg", this.msg);
                    process.destroy();
                    return result;
                }
            }
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("msg", e.getMessage());
            return result;
        }
        result.put("success", false);
        result.put("msg", "完成");
        return result;
    }

    public static class StartProcessFunctionCallRequest {
        @JsonPropertyDescription("流程名")
        @JsonProperty(required = true)
        public String processName;

        @JsonPropertyDescription("请假天数")
        @JsonProperty(required = true)
        public String days;

        @JsonPropertyDescription("请假原因")
        public String reason;

        @JsonPropertyDescription("下一节点任务负责人")
        public String assigneeUser;
    }

    public static class showProcessInstListFunctionCallRequest {

        @JsonPropertyDescription("用户名")
        @JsonProperty(required = true)
        public String userName;
    }

    public static class handleProcessInstListFunctionCallRequest {

        @JsonPropertyDescription("流程实例ID")
        @JsonProperty(required = true)
        public String procInstId;

        @JsonPropertyDescription("任务ID")
        @JsonProperty(required = true)
        public String taskId;

        @JsonPropertyDescription("意见")
        public String option;

        @JsonPropertyDescription("是否同意审批")
        @JsonProperty(required = true)
        public Boolean isOk;
    }


}

