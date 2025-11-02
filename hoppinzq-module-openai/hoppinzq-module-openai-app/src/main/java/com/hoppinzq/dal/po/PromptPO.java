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
@TableName("prompt")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PromptPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO, value = "id")
    private int id;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("label")
    private String label;

    @TableField("content")
    private String content;

    @TableField("token")
    private String token;
}

