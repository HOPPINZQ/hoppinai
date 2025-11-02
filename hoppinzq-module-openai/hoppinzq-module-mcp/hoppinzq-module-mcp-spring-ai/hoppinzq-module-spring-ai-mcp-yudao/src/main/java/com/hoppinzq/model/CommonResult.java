package com.hoppinzq.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回
 *
 * @param <T> 数据泛型
 */
@Data
public class CommonResult<T> implements Serializable {
    private Integer code;
    private T data;
    private String msg;
}
