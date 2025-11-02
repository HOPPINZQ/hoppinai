package com.hoppinzq.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hoppinzq.bean.*;
import com.hoppinzq.model.CommonResult;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

/**
 * yudao的 API 接口定义
 */
public interface YudaoApi {

    @GET("system/tenant/simple-list")
    Single<CommonResult<List<TenantRespVO>>> tenantSimpleList();

    @GET("system/user/get")
    Single<CommonResult<UserRespVO>> getUserById(@Query("id") Long id);

    @GET("system/user/page")
    Single<CommonResult<PageResult<UserRespVO>>> getUserPageByUserName(@Query("username") String username,
                                                                       @Query("pageNo") Integer pageNo,
                                                                       @Query("pageSize") Integer pageSize);

    /**
     * 获取部门精简信息列表
     */
    @GET("system/dept/simple-list")
    Single<CommonResult<List<DeptSimpleRespVO>>> getSimpleDeptList();

    @POST("system/user/create")
    Single<CommonResult<Long>> createUser(@Body UserSaveReqVO userSaveReqVO);

    @GET("system/user/assign-user-dept")
    Single<CommonResult<Boolean>> assignUserDept(@Query("userId") Long userId,
                                                 @Query("deptId") Long deptId);

    @GET("system/role/simple-list")
    Single<CommonResult<List<RoleRespVO>>> roleSimpleList();

    @POST("system/permission/assign-user-role")
    Single<CommonResult<Boolean>> assignUserRole(@Body ObjectNode params);

    @GET("system/auth/loginById")
    Single<CommonResult<ObjectNode>> loginById(@Query("userId") Long userId);

}
