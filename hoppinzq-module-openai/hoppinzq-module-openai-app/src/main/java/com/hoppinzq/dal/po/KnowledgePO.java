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
@TableName("knowledge")
public class KnowledgePO {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @TableField("knowledge_name")
    private String knowledge_name;

    @TableField("knowledge_creator")
    private String knowledge_creator;

    @TableField("knowledge_date")
    private String knowledge_date;

    @TableField(exist = false)
    private List<KnowledgeAttrPO> knowledgeAttrList;
}

