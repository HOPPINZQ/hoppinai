package com.hoppinzq.dal.dao;

import com.hoppinzq.dal.po.ApifoxAiDocPO;
import com.hoppinzq.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApifoxAiDocMapper extends BaseMapperX<ApifoxAiDocPO> {

    @Delete("DELETE FROM apifox_ai_doc")
    void deleteAll();
}
