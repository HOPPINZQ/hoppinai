package com.hoppinzq.dal.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hoppinzq.dal.po.ChatPO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper extends BaseMapper<ChatPO> {

    List<ChatPO> queryChat(@Param(value = "chat_user_id") String chat_user_id);

    /**
     * 向数据库中的chat表插入或更新记录。
     * 如果字段chat_id存在，则会执行REPLACE INTO操作，否则执行INSERT操作。
     *
     * @param chat ChatPO对象，包含要插入或更新的字段信息。
     */
    @Insert("<script>" +
            "replace into chat" +
            "<trim prefix='(' suffix=')' suffixOverrides=','>" +
            "<if test=\"chat_id != null\">chat_id,</if>" +
            "<if test=\"chat_user_id != null and chat_user_id != ''\">chat_user_id,</if>" +
            "<if test=\"chat_createDate != null and chat_createDate != ''\">chat_createDate,</if>" +
            "<if test=\"chat_title != null\">chat_title,</if>" +
            "<if test=\"chat_answer != null and chat_answer != ''\">chat_answer,</if>" +
            "<if test=\"chat_state != null and chat_state != ''\">chat_state,</if>" +
            "<if test=\"chat_modal != null and chat_modal != ''\">chat_modal,</if>" +
            "<if test=\"chat_context != null\">chat_context,</if>" +
            "<if test=\"chat_system != null and chat_system != ''\">chat_system,</if>" +
            "</trim>" +
            "<trim prefix='values (' suffix=')' suffixOverrides=','>" +
            "   <if test=\"chat_id != null\">#{chat_id},</if>" +
            "   <if test=\"chat_user_id != null and chat_user_id != ''\">#{chat_user_id},</if>" +
            "   <if test=\"chat_createDate != null and chat_createDate != ''\">#{chat_createDate},</if>" +
            "   <if test=\"chat_title != null and chat_title != ''\">#{chat_title},</if>" +
            "   <if test=\"chat_answer != null and chat_answer != ''\">#{chat_answer},</if>" +
            "   <if test=\"chat_state != null and chat_state != ''\">#{chat_state},</if>" +
            "   <if test=\"chat_modal != null and chat_modal != ''\">#{chat_modal},</if>" +
            "   <if test=\"chat_context != null\">#{chat_context},</if>" +
            "   <if test=\"chat_system != null and chat_system != ''\">#{chat_system},</if>" +
            "</trim>" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "chat_id", keyColumn = "chat_id")
    void insertOrUpdateChat(ChatPO chat);
}
