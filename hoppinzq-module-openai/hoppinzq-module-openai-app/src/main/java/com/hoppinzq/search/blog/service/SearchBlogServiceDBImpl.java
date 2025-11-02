package com.hoppinzq.search.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoppinzq.search.SearchService;
import com.hoppinzq.search.blog.dao.MyBlogMapper;
import com.hoppinzq.search.blog.po.BlogPO;
import com.hoppinzq.service.annotation.ApiMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchBlogServiceDBImpl implements SearchService {

    @Autowired
    private MyBlogMapper myBlogMapper;

    @Override
    public void init() {

    }

    @Override
    @ApiMapping(value = "queryBlog1", title = "搜索博客", description = "搜索博客")
    public List<BlogPO> query(String searchContent) {
        return myBlogMapper.selectList(new LambdaQueryWrapper<BlogPO>()
                .likeRight(BlogPO::getTitle, searchContent)
                .or()
                .likeRight(BlogPO::getAuthorName, searchContent)
                .or()
                .likeRight(BlogPO::getDescription, searchContent)
                .or()
                .likeRight(BlogPO::getText, searchContent)
        );
    }
}
