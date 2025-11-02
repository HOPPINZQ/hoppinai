package com.hoppinzq.dal.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName(value = "public_mcp", autoResultMap = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicMCPPO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO, value = "id")
    private int mcp_id;

    @TableField("type_id")
    private int type_id;

    @TableField("description")
    private String description;

    @TableField("name")
    private String name;

    @TableField("url")
    private String url;

    @TableField("remark")
    private String remark;

    @TableField("is_office")
    private boolean is_office;
}

