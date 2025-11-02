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
import java.util.List;

@Data
@TableName("public_mcp_type")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicMCPTypePO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO, value = "id")
    private int id;

    @TableField("type_name")
    private String typeName;

    @TableField("type_description")
    private String typeDescription;

    @TableField(exist = false)
    private List<PublicMCPPO> publicMCPPOList;
}

