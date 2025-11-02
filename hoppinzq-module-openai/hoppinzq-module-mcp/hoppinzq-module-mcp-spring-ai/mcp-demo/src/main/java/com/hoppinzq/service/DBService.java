package com.hoppinzq.service;

import com.google.common.collect.Maps;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 演示用AI生成并执行SQL，SQL脚本并未给出，自行修改sql或者动态生成
 */
@Service
public class DBService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Tool(name = "query_data_by_sql", description = "获取有效SQL的数据，现在存在具有以下表和列的数据库：\n" +
            "\n" +
            "chat:\n" +
            "- chat_id (varchar, primary key) -- 聊天ID\n" +
            "- chat_user_id (varchar)\n -- 聊天的创建人ID" +
            "- chat_createDate (datetime)\n -- 聊天的创建日期，默认是当前时间" +
            "- chat_title (blob)\n -- 聊天标题" +
            "- chat_answer (varchar)\n -- 聊天的摘要" +
            "- chat_state (int)\n -- 聊天状态，0表示不公开，1表示公开，默认是1" +
            "- chat_modal (varchar) -- 聊天大模型，默认是gpt-3.5-turbo\n" +
            "- chat_context (varchar) -- 聊天的上下文数量，默认是6\n" +
            "- chat_system (varchar)\n -- 聊天的系统提示词" +
            "- chat_image (varchar) -- 聊天的背景图\n" +
            "\n" +
            "chatmessage:\n" +
            "- message_id (varchar, primary key) -- 消息ID\n" +
            "- message_createDate (datetime) -- 消息创建日期 默认是当前时间\n" +
            "- message (longblob)\n -- 消息内容" +
            "- message_role (varchar)\n -- 消息角色，有system,user,assistant三个角色" +
            "- message_index (varchar)\n -- 消息位置" +
            "- chat_id (varchar)\n -- 聊天ID 关联 chat表的chat_id" +
            "- reason_message (longblob)\n -- 消息推理内容" +
            "\n")
    public DBDataResponseDTO getDataBySql(@ToolParam(description = "根据自然语言请求提供可以检索数据的 SQL语句") String sql) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        SqlRowSetMetaData metaData = sqlRowSet.getMetaData();
        String[] columnNames = metaData.getColumnNames();
        DBDataResponseDTO dbDataResponseDTO = new DBDataResponseDTO(Arrays.asList(columnNames), new LinkedList<>());
        while (sqlRowSet.next()) {
            Map<String, Object> data = Maps.newHashMapWithExpectedSize(columnNames.length);
            for (String columnName : columnNames) {
                data.put(columnName, sqlRowSet.getObject(columnName));
            }
            dbDataResponseDTO.source().add(data);
        }
        return dbDataResponseDTO;
    }
}

record DBDataResponseDTO(List<String> dimensions, List<Map<String, Object>> source) {
};

