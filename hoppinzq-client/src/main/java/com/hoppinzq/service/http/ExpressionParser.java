package com.hoppinzq.service.http;

import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author:ZhangQi 解析 ${param} 表达式
 */
public class ExpressionParser {

    /**
     * 用指定字段的值替换表达式
     *
     * @param expression
     * @param parameters
     * @param args
     * @return
     */
    public static String replaceExpression(String expression, Parameter[] parameters, Object[] args) {
        String pattern = "\\$\\{(.*?)\\}";
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(expression);
        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            String placeholder = matcher.group();
            String key = placeholder.substring(2, placeholder.length() - 1);
            if (key == null) {
                continue;
            }
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                if (key.equals(parameter.getName())) {
                    Class<?> type = parameter.getType();
                    Object value = args[i];
                    matcher.appendReplacement(stringBuffer, String.valueOf(value));
                }
            }
        }
        matcher.appendTail(stringBuffer);
        String result = stringBuffer.toString();
        return result;
    }
}