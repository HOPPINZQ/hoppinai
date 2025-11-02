package com.hoppinzq.service.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoppinzq.dal.dao.UserMapper;
import com.hoppinzq.dal.po.UserPO;
import com.hoppinzq.dto.UserResponseDTO;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.bean.ErrorCode;
import com.hoppinzq.service.util.object.BeanUtils;
import com.hoppinzq.service.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.hoppinzq.service.util.ServiceExceptionUtil.exception;

@Slf4j
@ApiServiceMapping(title = "用户设置服务", description = "用户设置服务", roleType = ApiServiceMapping.RoleType.RIGHT)
public class UserProfileService {

    @Autowired
    private UserMapper userMapper;

    @ApiMapping(value = "getUserLinkedAccount", title = "获取登录用户关联账号", description = "获取登录用户关联账号")
    public List<UserResponseDTO> getUserLinkedAccount() {
        Long userId = UserUtil.getUserId();
        if (userId == null) {
            throw exception(new ErrorCode(400, "用户未登录"));
        }
        UserPO userPO = userMapper.selectById(userId);
        if (userPO.getPhone() == null) {
            throw exception(new ErrorCode(401, "用户未绑定手机号"));
        } else {
            List<UserPO> userPOS = userMapper.selectList(new LambdaQueryWrapper<UserPO>().eq(UserPO::getPhone, userPO.getPhone()));
            return BeanUtils.toBean(userPOS, UserResponseDTO.class);
        }
    }
}

