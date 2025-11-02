package com.hoppinzq.dal.dao;

import com.hoppinzq.dal.po.GPTModelPO;
import com.hoppinzq.mapper.BaseMapperX;
import com.hoppinzq.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GPTModelMapper extends BaseMapperX<GPTModelPO> {

    default void deleteModelBySettingId(Long settingId) {
        delete(new LambdaQueryWrapperX<GPTModelPO>()
                .eq(GPTModelPO::getSettingId, settingId));
    }

    default List<GPTModelPO> selectList(Long settingId) {
        return selectList(new LambdaQueryWrapperX<GPTModelPO>()
                .eq(GPTModelPO::getSettingId, settingId)
        );
    }
}

