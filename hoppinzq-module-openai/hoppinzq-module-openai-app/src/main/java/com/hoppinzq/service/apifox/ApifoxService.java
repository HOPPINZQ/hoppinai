package com.hoppinzq.service.apifox;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.dal.dao.ApifoxAiDocMapper;
import com.hoppinzq.dal.po.ApifoxAiDocPO;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.bean.PageParam;
import com.hoppinzq.service.bean.PageResult;
import com.hoppinzq.service.util.object.BeanUtils;
import com.hoppinzq.utils.RedisUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@ApiServiceMapping(title = "apifox服务", description = "apifox服务", roleType = ApiServiceMapping.RoleType.NO_RIGHT)
public class ApifoxService {

    @Autowired
    private ApifoxAiDocMapper apifoxAiDocMapper;

    @Autowired
    private RedisUtils redisUtils;

    @PostConstruct
    public synchronized void initLocalCache() {
        List<ApifoxAiDocPO> apifoxAiDocPOS = apifoxAiDocMapper.selectList();
        redisUtils.set("apifox_ai_doc", JSON.toJSONString(apifoxAiDocPOS));
    }

    @ApiMapping(value = "publicProject", title = "获取公开的AI项目", description = "获取公开的AI项目", returnType = false)
    public List<ApifoxAiDocPO> publicProject() {
        Object apifoxAiDoc = redisUtils.get("apifox_ai_doc");
        JSONArray jsonArray = JSONArray.parseArray(apifoxAiDoc.toString() == null ? "[]" : apifoxAiDoc.toString());
        return BeanUtils.toBean(jsonArray, ApifoxAiDocPO.class);
    }

    @ApiMapping(value = "refresh", title = "刷新公开的AI项目", description = "刷新公开的AI项目")
    @Transactional(rollbackFor = Exception.class)
    public void refreshPublicProject() throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request.Builder builder = new Request.Builder().url("https://api.apifox.com/api/v1/public-projects?" +
                "categoryId=9&pageSize=200&page=1&order=default&locale=zh-CN")
                .method("GET", null);
        Response execute = client.newCall(builder.build()).execute();
        String string = execute.body().string();
        JSONObject jsonObject = JSONObject.parseObject(string);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("data");
        List<ApifoxAiDocPO> apifoxAiDocPOS = jsonArray.toJavaList(ApifoxAiDocPO.class);
        apifoxAiDocMapper.deleteAll();
        apifoxAiDocMapper.insertBatch(apifoxAiDocPOS);
    }

    @ApiMapping(value = "queryPublicProject", title = "获取公开的AI项目分页", description = "获取公开的AI项目分页")
    public PageResult<ApifoxAiDocPO> queryPublicProject(PageParam pageParam) throws IOException {
        PageResult<ApifoxAiDocPO> apifoxAiDocPOPageResult = apifoxAiDocMapper.selectPage(pageParam, null);
        return apifoxAiDocPOPageResult;
    }

}

