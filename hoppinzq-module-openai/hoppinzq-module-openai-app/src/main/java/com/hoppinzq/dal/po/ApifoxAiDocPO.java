package com.hoppinzq.dal.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("apifox_ai_doc")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApifoxAiDocPO {

    @TableId(type = IdType.INPUT, value = "id")
    private int id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("icon")
    private String icon;

    @TableField("views")
    private int views;

    @TableField("collections")
    private int collections;

    @TableField("sysDomain")
    private String sysDomain;

    @TableField("domainName")
    private String domainName;
}

