package com.hoppinzq.dal.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@TableName("gptsetting")
@NoArgsConstructor
@AllArgsConstructor
public class GPTSettingPO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("gpt_url")
    private String gptUrl;

    @TableField("gpt_apikey")
    private String gptApikey;

    @TableField("user_id")
    private String userId;

    @TableField("create_time")
    private Date createTime;

    @TableField("timeout")
    private Integer timeout;

    @TableField("model")
    private String model;
}

