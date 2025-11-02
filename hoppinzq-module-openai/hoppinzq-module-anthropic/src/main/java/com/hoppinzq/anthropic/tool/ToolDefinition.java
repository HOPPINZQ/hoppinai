package com.hoppinzq.anthropic.tool;

import com.anthropic.core.JsonValue;
import com.anthropic.models.messages.Tool;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoppinzq.anthropic.tool.schema.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * ToolDefinition 工具定义类
 * @author hoppinzq
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ToolDefinition {
    // toolCall 或者 MCP需要用的三个字段
    private String name;
    private String description;
    private Tool.InputSchema inputSchema;

    // 工具的参数类型和处理函数
    private Class type;
    private Function<String, String> function;

    public static ToolDefinition ReadFileDefinition = new ToolDefinition(
            "read_file",
            "读取指定相对文件路径的内容。适用于需要查看文件内容的场景，支持查看各种类型文件的完整内容。请注意，此工具仅用于文件，不可用于目录。",
            generateSchema(ReadFileInput.class),
            ReadFileInput.class,
            Tools::readFile
    );
    public static ToolDefinition ListFilesDefinition = new ToolDefinition(
            "list_files",
            "列出指定路径下的文件和目录，支持按文件类型筛选。若未指定路径，则默认列出当前目录的内容。",
            generateSchema(ListFilesInput.class),
            ListFilesInput.class,
            Tools::listFiles
    );
    public static ToolDefinition BashDefinition = new ToolDefinition(
            "bash",
            "执行Bash命令并返回其输出结果。适用于需要运行各种Shell命令的场景，可用于文件操作、程序执行、系统信息查询等多种任务。比如：如果用户让你打开网站，直接使用start [url]",
            generateSchema(BashInput.class),
            BashInput.class,
            Tools::executeBash
    );
    public static ToolDefinition EditFileDefinition = new ToolDefinition(
            "edit_file",
            "编辑文本文件。\n\n将指定文件中的'oldStr'替换为'newStr'。请注意，'oldStr'和'newStr'必须不同。\n若指定路径的文件不存在，则会自动创建该文件。",
            generateSchema(EditFileInput.class),
            EditFileInput.class,
            Tools::editFile
    );
    public static ToolDefinition ContentSearchDefinition = new ToolDefinition(
            "content_search",
            "使用ripgrep (rg)搜索代码或文本。\n\n适用于查找代码库中的代码片段、函数定义、变量使用情况或任何文本内容。\n支持按正则表达式、文件类型或目录进行精准搜索。",
            generateSchema(ContentSearchInput.class),
            ContentSearchInput.class,
            Tools::searchContent
    );
    public static ToolDefinition WebSearchDefinition = new ToolDefinition(
            "web_search",
            "搜索网络上的内容以获取实时信息\n\n获取最新技术信息、文档、API更新等\n使用场景：查找最新技术资料、解决技术问题。\n应谨慎使用，避免频繁搜索导致用户体验不佳和成本过高",
            generateSchema(WebSearchInput.class),
            WebSearchInput.class,
            Tools::searchWeb
    );

    /**
     * 根据输入的类类型生成对应的输入模式(Schema)
     * 该方法为简化版的schema生成实现，根据不同的输入类类型返回不同的属性定义。
     * 实际项目中可能需要更复杂的实现来处理更多类型和更复杂的属性关系。
     *
     * @param clazz 输入类类型，用于确定生成哪种类型的schema。支持以下类型：
     *              - ReadFileInput: 文件读取输入
     *              - ListFilesInput: 文件列表输入
     *              - BashInput: Bash命令输入
     *              - ContentSearchInput: 代码搜索输入
     *              - EditFileInput: 文件编辑输入
     *              - WebSearchInput: 网络搜索输入
     * @return 包含属性定义和必要字段的InputSchema对象，其中：
     *         - properties: 包含所有属性及其类型和描述的Map
     *         - required: 包含所有必填字段名称的列表
     */
    public static Tool.InputSchema generateSchema(Class<?> clazz) {
        // 简化版的schema生成，实际项目中可能需要更复杂的实现
        Map<String, Object> properties = new HashMap<>();
        List<String> required = new ArrayList<>();

        if (clazz == ReadFileInput.class) {
            Map<String, Object> pathProperty = new HashMap<>();
            pathProperty.put("type", "string");
            pathProperty.put("description", "工作目录中文件的相对路径。");
            properties.put("path", pathProperty);
            required.add("path");
        } else if (clazz == ListFilesInput.class) {
            Map<String, Object> pathProperty = new HashMap<>();
            pathProperty.put("type", "string");
            pathProperty.put("description", "可选的相对路径，用于列出文件。若未提供，则默认为当前目录。");
            properties.put("path", pathProperty);

            Map<String, Object> fileTypeProperty = new HashMap<>();
            fileTypeProperty.put("type", "string");
            fileTypeProperty.put("description", "可选的文件扩展名，用于限制搜索范围（例如：'md'、'java'、'txt'）。");
            properties.put("fileType", fileTypeProperty);

        } else if (clazz == BashInput.class) {
            Map<String, Object> commandProperty = new HashMap<>();
            commandProperty.put("type", "string");
            commandProperty.put("description", "要执行的bash命令。");
            properties.put("command", commandProperty);
            required.add("command");
        } else if (clazz == ContentSearchInput.class) {
            Map<String, Object> patternProperty = new HashMap<>();
            patternProperty.put("type", "string");
            patternProperty.put("description", "要查找的文本内容或内容的正则表达式。");
            properties.put("pattern", patternProperty);
            required.add("pattern");

            Map<String, Object> pathProperty = new HashMap<>();
            pathProperty.put("type", "string");
            pathProperty.put("description", "可选搜索路径（文件或目录）。");
            properties.put("path", pathProperty);

            Map<String, Object> fileTypeProperty = new HashMap<>();
            fileTypeProperty.put("type", "string");
            fileTypeProperty.put("description", "可选的文件扩展名，用于限制搜索范围（例如，'md'、'java'、'txt'）。");
            properties.put("fileType", fileTypeProperty);

            Map<String, Object> caseSensitiveProperty = new HashMap<>();
            caseSensitiveProperty.put("type", "boolean");
            caseSensitiveProperty.put("description", "搜索是否应区分大小写（默认值：false）。");
            properties.put("caseSensitive", caseSensitiveProperty);
        } else if (clazz == EditFileInput.class) {
            Map<String, Object> pathProperty = new HashMap<>();
            pathProperty.put("type", "string");
            pathProperty.put("description", "文件的路径");
            properties.put("path", pathProperty);

            Map<String, Object> oldStrProperty = new HashMap<>();
            oldStrProperty.put("type", "string");
            oldStrProperty.put("description", "要搜索的文本\n必须完全匹配，并且只能有一个完全匹配");
            properties.put("oldStr", oldStrProperty);

            Map<String, Object> newStrProperty = new HashMap<>();
            newStrProperty.put("type", "string");
            newStrProperty.put("description", "替换oldStr的文本");
            properties.put("newStr", newStrProperty);
        } else if (clazz == WebSearchInput.class) {
            Map<String, Object> queryProperty = new HashMap<>();
            queryProperty.put("type", "string");
            queryProperty.put("description", "要搜索的内容");
            properties.put("query", queryProperty);
            required.add("query");
        }

        return Tool.InputSchema.builder()
                .properties(JsonValue.fromJsonNode(new ObjectMapper().valueToTree(properties)))
                .build();
    }
}