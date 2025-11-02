package com.hoppinzq.dal.dao;

import com.hoppinzq.dal.po.PublicMCPTypePO;
import com.hoppinzq.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PublicMCPTypeMapper extends BaseMapperX<PublicMCPTypePO> {

    List<PublicMCPTypePO> queryMCP();
}
