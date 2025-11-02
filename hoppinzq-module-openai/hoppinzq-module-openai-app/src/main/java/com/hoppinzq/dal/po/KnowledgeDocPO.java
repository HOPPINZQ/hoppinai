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
@TableName(value = "knowledge_doc", autoResultMap = true)
public class KnowledgeDocPO {

    @TableId(value = "doc_id", type = IdType.ASSIGN_ID)
    private String doc_id;

    @TableField("doc_name")
    private String doc_name;

    @TableField("doc_type")
    private String doc_type;

    private String creator;

    private String date;

    @TableField("doc_url")
    private String doc_url;

    @TableField("attr_id")
    private String attr_id;

    @TableField("label_id")
    private String label_id;

    @TableField("knowledge_id")
    private String knowledge_id;
}

