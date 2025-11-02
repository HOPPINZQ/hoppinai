package com.hoppinzq.service.modification;


import com.hoppinzq.service.common.ModificationList;

/**
 * @author:ZhangQi 修改行为接口
 **/
public interface ModificationManager {

    /**
     * 行为规则
     *
     * @param objects
     * @return
     */
    Object[] applyModificationScheme(Object[] objects);

    /**
     * 修改列表
     *
     * @return
     */
    ModificationList[] getModifications();
}