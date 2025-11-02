package com.hoppinzq.search.blog.dao;

import com.hoppinzq.mapper.BaseMapperX;
import com.hoppinzq.search.blog.po.BlogPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyBlogMapper extends BaseMapperX<BlogPO> {

}
