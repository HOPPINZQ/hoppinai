package com.hoppinzq.dal.dao;

import com.hoppinzq.dal.po.KnowledgeAttrLabelPO;
import com.hoppinzq.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeAttrLabelMapper extends BaseMapperX<KnowledgeAttrLabelPO> {

    @Delete("DELETE FROM knowledge_attr_label")
    void deleteAll();
}
