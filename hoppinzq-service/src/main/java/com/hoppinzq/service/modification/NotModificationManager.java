package com.hoppinzq.service.modification;


import com.hoppinzq.service.annotation.InterfaceImplName;
import com.hoppinzq.service.common.ModificationList;

import java.io.Serializable;

/**
 * @author:ZhangQi
 */
@InterfaceImplName("不跟踪参数")
public class NotModificationManager implements ModificationManager, Serializable {
    private static final long serialVersionUID = 2783377098145240357L;

    @Override
    public Object[] applyModificationScheme(Object[] objects) {
        return objects;
    }

    @Override
    public ModificationList[] getModifications() {
        return null;
    }
}
