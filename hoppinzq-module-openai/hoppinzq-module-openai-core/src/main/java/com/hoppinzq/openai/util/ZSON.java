package com.hoppinzq.openai.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZSON {
    private ZSON() {
    }

    public static void main(String[] args) {
        String nonStandardJson = "{location:青岛,type:qwe,unit:CELSIUS}";

        // 使用正则匹配键值对
        Pattern pattern = Pattern.compile("(\\w+):([^,}]+)");
        Matcher matcher = pattern.matcher(nonStandardJson);

        JSONObject jsonObject = new JSONObject();

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            jsonObject.put(key, value);
        }

        System.out.println(jsonObject.toString());
    }

    public static String toJSONString(String json) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(
                /**
                 * 允许 JSON 中包含 java注释
                 */
                JsonReadFeature.ALLOW_JAVA_COMMENTS.mappedFeature(),
                /**
                 * 允许使用单引号（'）代替双引号包裹键或字符串值
                 * 如{'name':'value'}
                 */
                JsonReadFeature.ALLOW_SINGLE_QUOTES.mappedFeature(),
                /**
                 * 允许键名（字段名）不加引号
                 * 如{name: "value"}
                 */
                JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(),
                /**
                 * 允许字符串值中包含未转义的控制字符
                 * 如{name: "value"}
                 */
                JsonReadFeature.ALLOW_UNQUOTED_FIELD_NAMES.mappedFeature(),
                /**
                 * 允许 JSON 中包含尾随逗号
                 * 如{name: "value",}
                 */
                JsonReadFeature.ALLOW_TRAILING_COMMA.mappedFeature(),
                /**
                 * 允许数组或对象中存在缺失值
                 * 如[1,,3]
                 */
                JsonReadFeature.ALLOW_MISSING_VALUES.mappedFeature()
        );

        try {
            int start = json.indexOf('{');
            int end = json.lastIndexOf('}');
            if (start == -1 || end == -1 || end <= start) {
                json = fixWithStack(json);
            }
            json = json.substring(start, end + 1);
            json = json.trim()
                    .replaceAll("```json|```", "")
                    .replace('“', '"').replace('”', '"')
                    .replace('‘', '"').replace('’', '"')
                    .replace('：', ':').replace('=', ':')
                    .replaceAll("(\"[^\"]+\")\\s+\"", "$1:\"")
                    .replaceAll("\"(\\s*)\"", "\",$1\"")
                    .replaceAll(":\\s*([a-zA-Z_][a-zA-Z0-9_]*)", ":\"$1\"");
            // 处理多余括号
            while (json.startsWith("{")) {
                String temp = json.substring(1).trim();
                if (temp.startsWith("{")) json = temp;
                else break;
            }
            return mapper.writeValueAsString(json);
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = e.getMessage();
            if (e instanceof JsonParseException) {
                if (errorMessage.contains("Unexpected end-of-input")) {
                    if (errorMessage.contains("expecting '}'")) {
                        throw new RuntimeException("缺失闭合括号}");
                    } else if (errorMessage.contains("expecting ']'")) {
                        throw new RuntimeException("缺失闭合括号]");
                    }
                }
            }
            throw new RuntimeException(e);
        }
    }

    private static String fixWithStack(String jsonStr) {
        Stack<Character> stack = new Stack<>();
        boolean inString = false;
        for (char c : jsonStr.toCharArray()) {
            if (c == '"' && !inString) inString = true;
            else if (c == '"' && inString) inString = false;
            if (inString) continue;
            if (c == '{' || c == '[') {
                stack.push(c);
            } else if (c == '}') {
                if (!stack.isEmpty() && stack.peek() == '{') stack.pop();
            } else if (c == ']') {
                if (!stack.isEmpty() && stack.peek() == '[') stack.pop();
            }
        }
        StringBuilder fixed = new StringBuilder(jsonStr);
        while (!stack.isEmpty()) {
            char open = stack.pop();
            fixed.append(open == '{' ? '}' : ']');
        }
        return fixed.toString();
    }
}

