package com.hoppinzq.dal.dao;

import com.hoppinzq.dal.po.GPTSettingPO;
import com.hoppinzq.dto.GPTSettingQueryDTO;
import com.hoppinzq.mapper.BaseMapperX;
import com.hoppinzq.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GPTSettingMapper extends BaseMapperX<GPTSettingPO> {

    default List<GPTSettingPO> selectList(GPTSettingQueryDTO gptSettingQueryDTO) {
        return selectList(new LambdaQueryWrapperX<GPTSettingPO>()
                .eqIfPresent(GPTSettingPO::getGptUrl, gptSettingQueryDTO.getGptUrl())
                .eqIfPresent(GPTSettingPO::getGptApikey, gptSettingQueryDTO.getGptApikey())
                .eqIfPresent(GPTSettingPO::getUserId, gptSettingQueryDTO.getUserId())
        );
    }

    default GPTSettingPO selectOne(GPTSettingQueryDTO gptSettingQueryDTO) {
        return selectOne(new LambdaQueryWrapperX<GPTSettingPO>()
                .eqIfPresent(GPTSettingPO::getGptUrl, gptSettingQueryDTO.getGptUrl())
                .eqIfPresent(GPTSettingPO::getGptApikey, gptSettingQueryDTO.getGptApikey())
                .eqIfPresent(GPTSettingPO::getUserId, gptSettingQueryDTO.getUserId())
        );
    }
}

