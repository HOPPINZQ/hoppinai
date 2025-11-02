package com.hoppinzq.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hoppinzq.bean.PageResult;
import com.hoppinzq.bean.UserRespVO;
import com.hoppinzq.bean.UserSaveReqVO;
import com.hoppinzq.model.CommonResult;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YudaoUserTools {

    @Autowired
    private YudaoService yudaoService;

    @Tool(name = "get_user_by_id", description = "根据人员ID，获取人员信息，不能传用户名。你需要通过“get_user_page_by_username”工具，来根据用户名去获取人员的ID")
    public CommonResult<UserRespVO> getUserById(@ToolParam(description = "用户的ID") Long userId) {
        return yudaoService.getUserById(userId);
    }

    @Tool(name = "get_user_page_by_username", description = "通过用户名，模糊查询人员信息")
    public CommonResult<PageResult<UserRespVO>> getUserPageByUserName(@ToolParam(description = "人员的用户名或姓名") String userName) {
        return yudaoService.getUserPageByUserName(userName);
    }

    @Tool(name = "create_user", description = "创建一个新的人员，并不需要分配部门和角色。人员的ID会自动生成，用户名是必填的，性别默认为男1，密码默认为admin123，你可以自由发挥备注remark的内容")
    public CommonResult<Long> createUser(
            @ToolParam(description = "人员的用户名") String userName,
            @ToolParam(description = "人员的密码，默认admin123", required = false) String password,
            @ToolParam(description = "人员的昵称") String nickName,
            @ToolParam(description = "人员的性别,男1，女2,", required = false) Integer sex,
            @ToolParam(description = "人员的备注,AI你可以对这个字段自由发挥", required = false) String remark
    ) {
        return yudaoService.createUser(UserSaveReqVO.builder()
                .username(userName).nickname(nickName).remark(remark).sex(sex).password(password)
                .build());
    }

    @Tool(name = "login", description = "登录，这个功能可能会返回一个链接，请提示用户打开")
    public String login(@ToolParam(description = "根据人员ID，登录系统，不能传用户名。" +
            "你需要通过“get_user_page_by_username”工具，来根据用户名去获取人员的ID。") Long userId) {
        CommonResult<ObjectNode> token = yudaoService.loginById(userId);
        ObjectNode data = token.getData();
        String link = "http://localhost/index?accessToken=" + data.get("accessToken").asText() + "&refreshToken=" + data.get("refreshToken").asText() + "&tenantId=" + data.get("tenantId").asText();
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                Runtime.getRuntime().exec("cmd /c start " + link);
                return "登录成功";
            } else {
                return link;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "登录失败：" + e.getMessage();
        }
    }
}

