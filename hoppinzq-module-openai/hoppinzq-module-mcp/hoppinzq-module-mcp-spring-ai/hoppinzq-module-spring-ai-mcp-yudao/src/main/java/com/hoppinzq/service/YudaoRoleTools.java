package com.hoppinzq.service;

import com.hoppinzq.bean.PermissionAssignUserRoleReqVO;
import com.hoppinzq.bean.RoleRespVO;
import com.hoppinzq.model.CommonResult;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class YudaoRoleTools {

    @Autowired
    private YudaoService yudaoService;

    @Tool(name = "get_role_list", description = "获取应用的所有角色列表")
    public CommonResult<List<RoleRespVO>> getRoleList() {
        return yudaoService.getRoleSimpleList();
    }

    @Tool(name = "assign_user_role", description = "用户和角色必须是ID，你可以通过“get_user_page_by_username”工具，来根据用户名去获取人员的ID，通过“get_role_list”工具，来获取角色信息")
    public CommonResult<Boolean> assignUserRole(@ToolParam(description = "用户的ID") Long userId,
                                                @ToolParam(description = "角色的ID") Set<Long> roleIds) {
        return yudaoService.assignUserRole(PermissionAssignUserRoleReqVO.builder()
                .userId(userId).roleIds(roleIds)
                .build());
    }
}

