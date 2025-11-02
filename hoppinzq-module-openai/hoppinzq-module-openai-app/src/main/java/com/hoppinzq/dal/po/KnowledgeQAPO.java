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
@TableName(value = "knowledge_qa", autoResultMap = true)
public class KnowledgeQAPO {

    @TableId(value = "qa_id", type = IdType.ASSIGN_ID)
    private String qa_id;

    private String answer;

    private String question;

    private String creator;

    private String date;

    @TableField("knowledge_id")
    private String knowledge_id;

    @TableField("attr_id")
    private String attr_id;

    @TableField("label_id")
    private String label_id;
}

