package com.hoppinzq.service;

import com.hoppinzq.bean.DeptSimpleRespVO;
import com.hoppinzq.model.CommonResult;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YudaoDeptTools {

    @Autowired
    private YudaoService yudaoService;

    @Tool(name = "get_dept_list", description = "获取应用的所有组织架构列表")
    public CommonResult<List<DeptSimpleRespVO>> getDeptList() {
        return yudaoService.getSimpleDeptList();
    }

    @Tool(name = "assign_user_dept", description = "用户和组织架构必须是ID，你可以通过“get_user_page_by_username”工具，来根据用户名去获取人员的ID，通过“get_dept_list”工具，来获取组织架构信息")
    public CommonResult<Boolean> assignUserDept(@ToolParam(description = "用户的ID") Long userId,
                                                @ToolParam(description = "部门的ID") Long deptId) {
        return yudaoService.assignUserDept(userId, deptId);
    }
}

