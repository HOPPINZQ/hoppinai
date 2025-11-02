package com.hoppinzq.dal.dao;

import com.hoppinzq.dal.po.KnowledgeDocPO;
import com.hoppinzq.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeDocMapper extends BaseMapperX<KnowledgeDocPO> {

    @Delete("DELETE FROM knowledge_doc")
    void deleteAll();
}
