package com.hoppinzq.mcp.util;

import com.alibaba.fastjson.JSONObject;

import java.text.MessageFormat;
import java.util.List;

public class MCPUtil {

    private MCPUtil() {
    }

    public static void main(String[] args) {
        String inputSchema = "{\"type\":\"object\",\"properties\":{\"searchContent\":{\"type\":\"string\",\"description\":\"搜索内容\",\"required\":true},\"searchType\":{\"type\":\"string\",\"description\":\"搜索类型，可以是音乐，网站和其他\",\"enum\":[\"music\",\"website\",\"other\"]}}}";
        List<HoppinTool> tools = List.of(new HoppinTool("搜索工具", "可以搜索互联网上的信息", inputSchema));
        System.out.println(getPrompt(tools));
    }

    public static String getPrompt(List<HoppinTool> tools) {
        if (tools == null || tools.isEmpty()) {
            return "你是一个有用的助手";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tools.size(); i++) {
            HoppinTool tool = tools.get(i);
            String name = tool.name();
            if (name == null || "".equals(name.trim())) {
                throw new IllegalArgumentException("工具名称不能为空");
            }
            String description = tool.description();
            String inputSchema = tool.inputSchema();
            sb.append(MessageFormat.format("工具{0}:\n\t- 工具名称:{1}\n\t- 工具描述: {2}", i + 1, name, description));

            JSONObject jsonObject = JSONObject.parseObject(inputSchema);
            if (jsonObject != null && jsonObject.containsKey("properties")) {
                JSONObject properties = jsonObject.getJSONObject("properties");
                if (properties != null) {
                    sb.append("\n\t- 工具参数：\n\t");
                    for (String key : properties.keySet()) {
                        JSONObject property = properties.getJSONObject(key);
                        sb.append(MessageFormat.format("    - {0}:\n\t", key));
                        sb.append(MessageFormat.format("        - 类型：{0}\n\t", property.getString("type")));
                        sb.append(MessageFormat.format("        - 描述：{0}\n\t", property.getString("description")));
                        if (property.containsKey("enum")) {
                            sb.append(MessageFormat.format("        - 可选值：{0}\n\t", property.getJSONArray("enum").toJSONString()));
                        }
                        if (property.getBooleanValue("required")) {
                            sb.append("        - 是否必填：是\n\t");
                        } else {
                            sb.append("        - 是否必填：否\n\t");
                        }
                    }
                }
            }
        }

        String prompt = "你是一个有用的助手，可以使用一些工具来回答用户的问题，现在有这些工具：\n" +
                "\n" +
                sb +
                "\n" +
                "根据用户的问题选择合适的工具。如果不需要工具，请直接回复。\n" +
                "\n" +
                "重要提示：当您需要使用工具时，您必须只使用以下确切的JSON对象格式进行响应，而不能使用其他格式，也不能额外输出其他内容：\n" +
                "\n" +
                "{\n" +
                "    \"tool\":\"tool_name\",\n" +
                "    \"arguments\":{\n" +
                "        \"arguments_name\"：\"value\"\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "收到工具的响应后：\n" +
                "    - 1、将原始数据转换为自然的对话式响应\n" +
                "    - 2、保持回答简洁但信息丰富\n" +
                "    - 3、关注最相关的信息\n" +
                "    - 4、使用用户问题中的适当上下文\n" +
                "    - 5、永远不要用完全相同的参数重新进行之前的工具调用\n" +
                "    - 6、与用户交流时切勿提及工具名称。例如，不要说\"我需要使用edit_file工具来编辑你的文件\"，只需说\"我将编辑你的文件\"\n" +
                "    - 7、只在必要时调用工具。如果用户的任务较为宽泛或你已经知道答案，直接回应而不调用工具\n" +
                "    - 8、在调用每个工具之前，先向用户解释为什么要调用它\n" +
                "\n" +
                "请仅使用上面明确定义的工具。\n";
        return prompt;
    }

    public static String[] getArgs(String response) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(response);
            if (jsonObject.containsKey("tool")) {
                String toolName = jsonObject.getString("tool");
                JSONObject arguments = jsonObject.getJSONObject("arguments");
                return new String[]{toolName, arguments.toJSONString()};
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}

