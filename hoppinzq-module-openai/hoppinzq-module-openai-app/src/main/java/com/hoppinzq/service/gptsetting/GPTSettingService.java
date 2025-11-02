package com.hoppinzq.service.gptsetting;

import com.alibaba.fastjson.JSON;
import com.hoppinzq.dal.dao.GPTModelMapper;
import com.hoppinzq.dal.dao.GPTSettingMapper;
import com.hoppinzq.dal.po.GPTModelPO;
import com.hoppinzq.dal.po.GPTSettingPO;
import com.hoppinzq.dto.GPTSettingInsertDTO;
import com.hoppinzq.dto.GPTSettingQueryDTO;
import com.hoppinzq.dto.GPTSettingResDTO;
import com.hoppinzq.dto.ModelQueryDTO;
import com.hoppinzq.model.exception.OpenaiException;
import com.hoppinzq.model.openai.model.Model;
import com.hoppinzq.openai.service.OpenAiService;
import com.hoppinzq.query.LambdaQueryWrapperX;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.annotation.RateLimit;
import com.hoppinzq.service.annotation.Self;
import com.hoppinzq.service.util.object.BeanUtils;
import com.hoppinzq.service.utils.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ApiServiceMapping(title = "gpt设置", roleType = ApiServiceMapping.RoleType.RIGHT)
public class GPTSettingService {

    GPTSettingService service;
    @Autowired
    private GPTSettingMapper gptSettingMapper;
    @Autowired
    private GPTModelMapper gptModelMapper;

    /**
     * po转模型
     *
     * @param gptModelPO
     * @return
     */
    private static Model convert(GPTModelPO gptModelPO) {
        Model model = new Model();
        model.setId(gptModelPO.getModelId());
        model.setObject(gptModelPO.get_object());
        model.setOwnedBy(gptModelPO.getOwnedBy());
        model.setRoot(gptModelPO.getRoot());
        return model;
    }

    /**
     * 检查apikey格式
     *
     * @param apiKey
     */
    private static void check(String apiKey) {
        if (StringUtils.isEmpty(apiKey)) {
            throw new OpenaiException("apikey必填");
        }
        if (!apiKey.startsWith("sk-")) {
            throw new OpenaiException("apikey必须以sk-开头");
        }
    }

    /**
     * 注入自己
     *
     * @param gptSettingService
     */
    @Self
    public void setSelf(GPTSettingService gptSettingService) {
        this.service = gptSettingService;
    }

    @ApiMapping(value = "isUrlAvailable", title = "url是否可用", description = "url是否可用",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.POST)
    public boolean isUrlAvailable(String proxyUrl) {
//        try {
//            URL url = new URL(proxyUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("HEAD");
//            int responseCode = connection.getResponseCode();
//            return (responseCode == HttpURLConnection.HTTP_OK);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
        if (OpenAiService.BASE_URL.equals(proxyUrl)) {
            return false;
        }
        return true;
    }

    /**
     * 新增gpt设置
     *
     * @param gptSettingInsertDTO
     * @return
     */
    @ApiMapping(value = "insertOrUpdateGptSetting", title = "新增或修改gpt设置", description = "新增或修改gpt设置",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.POST)
    @RateLimit
    public long insertOrUpdateGptSetting(GPTSettingInsertDTO gptSettingInsertDTO) {
        check(gptSettingInsertDTO.getApikey());
        GPTSettingPO dbGPTSetting = gptSettingMapper.selectOne(GPTSettingQueryDTO.builder()
                .userId(UserUtil.getUserId())
                .gptUrl(gptSettingInsertDTO.getProxyUrl())
                .gptApikey(gptSettingInsertDTO.getApikey())
                .build());
        if (dbGPTSetting != null) {
            if (!dbGPTSetting.getModel().equals(gptSettingInsertDTO.getModel())) {
                dbGPTSetting.setModel(gptSettingInsertDTO.getModel());
                gptSettingMapper.updateById(dbGPTSetting);
            }
            return dbGPTSetting.getId();
        }
        GPTSettingPO gptSettingPO = GPTSettingPO.builder()
                .userId(String.valueOf(UserUtil.getUserId()))
                .model(gptSettingInsertDTO.getModel())
                .gptApikey(gptSettingInsertDTO.getApikey())
                .gptUrl(gptSettingInsertDTO.getProxyUrl())
                .build();
        gptSettingMapper.insert(gptSettingPO);
        return gptSettingPO.getId();
    }

    /**
     * 查询gpt设置
     *
     * @param gptSettingQueryDTO
     * @return
     */
    @ApiMapping(value = "queryGptSetting", title = "查询gpt设置", description = "查询gpt设置",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.POST)
    public List<GPTSettingPO> queryGptSetting(GPTSettingQueryDTO gptSettingQueryDTO) {
        return gptSettingMapper.selectList(gptSettingQueryDTO);
    }

    @ApiMapping(value = "queryCurrentUserGptSetting", title = "查询gpt设置", description = "查询gpt设置",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.POST)
    public GPTSettingResDTO queryCurrentUserGptSetting() {
        List<GPTSettingPO> gptSettingPOS = gptSettingMapper.selectList(new LambdaQueryWrapperX<GPTSettingPO>()
                .eqIfPresent(GPTSettingPO::getUserId, UserUtil.getUserId())
                .orderByDesc(GPTSettingPO::getCreateTime));
        if (gptSettingPOS.size() > 0) {
            GPTSettingResDTO gptSettingResDTO = BeanUtils.toBean(gptSettingPOS.get(0), GPTSettingResDTO.class);
            return gptSettingResDTO;
        }
        return null;
    }

    /**
     * 新增gpt模型
     *
     * @param gptSettingInsertDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @ApiMapping(value = "insertGptModel", title = "新增gpt模型", description = "新增gpt模型",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.POST)
    public void insertGptModal(GPTSettingInsertDTO gptSettingInsertDTO) {
        check(gptSettingInsertDTO.getApikey());
        GPTSettingPO gptSettingPO = gptSettingMapper.selectOne(GPTSettingQueryDTO.builder()
                .gptApikey(gptSettingInsertDTO.getApikey())
                .gptUrl(gptSettingInsertDTO.getProxyUrl())
                .userId(UserUtil.getUserId())
                .build());
        if (gptSettingPO == null) {
            gptSettingPO = GPTSettingPO.builder()
                    .userId(String.valueOf(UserUtil.getUserId()))
                    .gptApikey(gptSettingInsertDTO.getApikey())
                    .gptUrl(gptSettingInsertDTO.getProxyUrl())
                    .model(gptSettingInsertDTO.getModel())
                    .build();
            gptSettingMapper.insert(gptSettingPO);
        } else {
            if (!gptSettingPO.getModel().equals(gptSettingInsertDTO.getModel())) {
                gptSettingPO.setModel(gptSettingInsertDTO.getModel());
                gptSettingMapper.updateById(gptSettingPO);
            }
            gptModelMapper.deleteModelBySettingId(gptSettingPO.getId());
        }

        List<Model> modals = getModel(ModelQueryDTO.builder()
                .apikey(gptSettingInsertDTO.getApikey())
                .proxyUrl(gptSettingInsertDTO.getProxyUrl())
                .build());
        List<GPTModelPO> modalPOList = new ArrayList<>(modals.size());
        for (Model modal : modals) {
            modalPOList.add(GPTModelPO.builder()
                    .modelId(modal.getId())
                    .ownedBy(modal.getOwnedBy())
                    ._object(modal.getObject())
                    .settingId(gptSettingPO.getId())
                    .permission(JSON.toJSONString(modal.getPermission()))
                    .build());
        }
        gptModelMapper.insertBatch(modalPOList);
    }

    /**
     * 获取模型列表
     *
     * @param modelQueryDTO
     * @return
     */
    @ApiMapping(value = "getModel", title = "获取模型列表", description = "获取模型列表",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.POST)
    public List<Model> getModel(ModelQueryDTO modelQueryDTO) {
        check(modelQueryDTO.getApikey());
        if (StringUtils.isEmpty(modelQueryDTO.getProxyUrl())) {
            modelQueryDTO.setProxyUrl(OpenAiService.BASE_URL);
        }
        GPTSettingPO gptSettingPO = gptSettingMapper.selectOne(GPTSettingQueryDTO.builder()
                .gptApikey(modelQueryDTO.getApikey())
                .gptUrl(modelQueryDTO.getProxyUrl())
                .userId(UserUtil.getUserId())
                .build());
        List<Model> models = new ArrayList<>();
        if (gptSettingPO == null) {
            OpenAiService service = new OpenAiService(modelQueryDTO.getApikey(), Duration.ofSeconds(60), modelQueryDTO.getProxyUrl());
            models = service.listModels();
            service.shutdownExecutor();
        } else {
            List<GPTModelPO> gptModelPOS = gptModelMapper.selectList(gptSettingPO.getId());
            if (gptModelPOS.isEmpty()) {
                OpenAiService service = new OpenAiService(modelQueryDTO.getApikey(), Duration.ofSeconds(60), modelQueryDTO.getProxyUrl());
                models = service.listModels();
                service.shutdownExecutor();
            } else {
                for (GPTModelPO gptModelPO : gptModelPOS) {
                    models.add(convert(gptModelPO));
                }
            }
        }
        return models;
    }

    public GPTSettingPO getSettingById(Long id) {
        return gptSettingMapper.selectById(id);
    }

    public GPTSettingPO getSettingById(Long id, Long userId) {
        return gptSettingMapper.selectOne(new LambdaQueryWrapperX<GPTSettingPO>()
                .eq(GPTSettingPO::getId, id)
                .eq(GPTSettingPO::getUserId, userId)
        );
    }

}

