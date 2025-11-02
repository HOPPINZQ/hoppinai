package com.hoppinzq.dal.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hoppinzq.dal.po.KnowledgePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface KnowledgeMapper extends BaseMapper<KnowledgePO> {

    KnowledgePO queryKnowledge(@Param(value = "knowledge_id") String knowledge_id);
}
