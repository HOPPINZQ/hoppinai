package com.hoppinzq.service.util.object;

import com.hoppinzq.service.bean.PageParam;

/**
 * {@link PageParam} 工具类
 */
public class PageUtils {

    public static int getStart(PageParam pageParam) {
        return (pageParam.getPageNo() - 1) * pageParam.getPageSize();
    }

}
