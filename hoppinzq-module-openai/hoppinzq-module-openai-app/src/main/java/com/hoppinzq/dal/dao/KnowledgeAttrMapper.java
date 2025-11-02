package com.hoppinzq.dal.dao;

import com.hoppinzq.dal.po.KnowledgeAttrPO;
import com.hoppinzq.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeAttrMapper extends BaseMapperX<KnowledgeAttrPO> {

    @Delete("DELETE FROM knowledge_attr")
    void deleteAll();
}
