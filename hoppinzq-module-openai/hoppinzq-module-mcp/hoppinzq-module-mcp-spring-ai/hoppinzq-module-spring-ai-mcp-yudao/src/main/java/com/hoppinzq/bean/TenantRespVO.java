package com.hoppinzq.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 租户 Response VO")
@Data
public class TenantRespVO {

    private Long id;

    private String name;

    private String contactName;

    private String contactMobile;

    private Integer status;

    private String website;

    private Long packageId;

    private LocalDateTime expireTime;

    private Integer accountCount;

    private LocalDateTime createTime;

}
