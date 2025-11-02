package com.hoppinzq.dal.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 */
@Data
@Builder
@TableName("user")
public class UserPO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT, value = "id")
    private String id;
    @TableField("username")
    private String username;
    @TableField("password")
    private String password;
    @TableField("phone")
    private String phone;
    @TableField("email")
    private String email;
    @TableField("user_description")
    private String user_description;
    @TableField("user_create")
    private Date user_create;
    @TableField("user_update")
    private Date user_update;

    @TableField("user_image")
    private String user_image;

    @TableField("user_right")
    private int user_right;

    @TableField("user_state")
    private int user_state;
    @TableField("login_type")
    private String login_type;
    @TableField("extra_message")
    private String extra_message;
    @TableField("user_extra_id")
    private String user_extra_id;
}
