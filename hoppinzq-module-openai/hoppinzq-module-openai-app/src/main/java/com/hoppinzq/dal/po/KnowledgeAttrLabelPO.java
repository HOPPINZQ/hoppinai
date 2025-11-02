package com.hoppinzq.dal.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName(value = "knowledge_attr_label", autoResultMap = true)
public class KnowledgeAttrLabelPO {

    @TableId(value = "knowledge_attr_label_id", type = IdType.ASSIGN_ID)
    private String knowledge_attr_label_id;

    @TableField("attr_id")
    private String attr_id;

    @TableField("knowledge_attr_label_name")
    private String knowledge_attr_label_name;
}

