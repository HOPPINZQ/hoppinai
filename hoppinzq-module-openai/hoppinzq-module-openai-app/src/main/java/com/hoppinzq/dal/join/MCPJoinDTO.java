package com.hoppinzq.dal.join;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

public class MCPJoinDTO {

    @TableId(type = IdType.AUTO, value = "id")
    private int id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;
}

