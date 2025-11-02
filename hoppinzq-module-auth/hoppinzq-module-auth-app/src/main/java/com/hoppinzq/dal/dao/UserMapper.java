package com.hoppinzq.dal.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hoppinzq.dal.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {

}

