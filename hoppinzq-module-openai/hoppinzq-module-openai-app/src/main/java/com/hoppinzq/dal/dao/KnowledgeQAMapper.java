package com.hoppinzq.dal.dao;

import com.hoppinzq.dal.po.KnowledgeQAPO;
import com.hoppinzq.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeQAMapper extends BaseMapperX<KnowledgeQAPO> {

    @Delete("DELETE FROM knowledge_qa")
    void deleteAll();
}
