package com.hoppinzq.dal.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName(value = "knowledge_attr", autoResultMap = true)
public class KnowledgeAttrPO {

    @TableId(value = "knowledge_attr_id", type = IdType.ASSIGN_ID)
    private String knowledge_attr_id;

    @TableField("knowledge_id")
    private String knowledge_id;

    @TableField("knowledge_attr_key")
    private String knowledge_attr_key;

    @TableField("knowledge_attr_name")
    private String knowledge_attr_name;

    @TableField(exist = false)
    private List<KnowledgeAttrPO> knowledgeAttrLabelList;
}

