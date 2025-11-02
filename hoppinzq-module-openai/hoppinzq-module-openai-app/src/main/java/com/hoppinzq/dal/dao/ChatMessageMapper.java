package com.hoppinzq.dal.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hoppinzq.dal.po.ChatMessagePO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessagePO> {
    @Insert("<script>" +
            " insert into sm (message) " +
            "    VALUES" +
            "    <foreach collection='list' item='message' separator=','>" +
            "        (#{message})" +
            "    </foreach>" +
            "</script>")
        //训练数据用
    void isms(List<String> message);

}