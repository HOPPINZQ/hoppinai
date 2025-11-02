package com.hoppinzq.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 */
@Data
@Builder
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private String username;
    private String phone;
    private String email;
    private String user_description;
    private Date user_create;
    private Date user_update;
    private String user_image;
    private int user_right;
    private int user_state;
    private String login_type;
    private String extra_message;
}
