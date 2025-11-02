package com.hoppinzq.search.blog.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hoppinzq.search.SearchPO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("blog")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlogPO extends SearchPO {

    @TableId
    private String id;

    private String title;

    private String description;

    private String text;

    private String authorName;

    private Integer type;
}
