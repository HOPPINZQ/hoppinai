package com.hoppinzq.dal.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("gptmodel")
public class GPTModelPO {

    @TableId(type = IdType.AUTO)
    private int id;

    @TableField("model_id")
    private String modelId;

    @TableField("setting_id")
    private Long settingId;

    @TableField("_object")
    private String _object;

    @TableField("owned_by")
    private String ownedBy;

    @TableField("root")
    private String root;

    @TableField("permission")
    private String permission;
}

