package com.hoppinzq.service.knowledge;

import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.cache.KnowledgeCache;
import com.hoppinzq.dal.dao.*;
import com.hoppinzq.dal.po.*;
import com.hoppinzq.dto.*;
import com.hoppinzq.query.LambdaQueryWrapperX;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.bean.ErrorCode;
import com.hoppinzq.service.bean.PageParam;
import com.hoppinzq.service.bean.PageResult;
import com.hoppinzq.service.util.StringUtil;
import com.hoppinzq.service.util.object.BeanUtils;
import com.hoppinzq.service.utils.UserUtil;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.lkeap.v20240522.LkeapClient;
import com.tencentcloudapi.lkeap.v20240522.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.hoppinzq.service.util.ServiceExceptionUtil.exception;

@Slf4j
@ApiServiceMapping(title = "知识库", roleType = ApiServiceMapping.RoleType.RIGHT)
public class KnowledgeService {

    private static final String knowledgeBaseId = "";
    private static final String secretId = "";
    private static final String secretKey = "";
    private static final String region = "ap-guangzhou";
    private static final String endpoint = "lkeap.tencentcloudapi.com";

    @Autowired
    private KnowledgeMapper knowledgeMapper;
    @Autowired
    private KnowledgeAttrMapper knowledgeAttrMapper;
    @Autowired
    private KnowledgeAttrLabelMapper knowledgeAttrLabelMapper;
    @Autowired
    private KnowledgeQAMapper knowledgeQAMapper;
    @Autowired
    private KnowledgeDocMapper knowledgeDocMapper;

    private static LkeapClient getClient() {
        Credential cred = new Credential(secretId, secretKey);
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(endpoint);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        LkeapClient client = new LkeapClient(cred, region, clientProfile);
        return client;
    }

    @ApiMapping(value = "queryKnowledge", title = "查询知识库", description = "会查询属性和标签",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.GET)
    public KnowledgePO queryKnowledge() {
        return knowledgeMapper.queryKnowledge(knowledgeBaseId);
    }

    @ApiMapping(value = "queryQA", title = "查询QA", description = "查询QA",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.GET)
    public PageResult<KnowledgeQAPageResultDTO> queryQA(PageParam pageParam) {
        PageResult<KnowledgeQAPO> knowledgeQAPOPageResult = knowledgeQAMapper.selectPage(pageParam,
                new LambdaQueryWrapperX<KnowledgeQAPO>().eq(KnowledgeQAPO::getKnowledge_id, knowledgeBaseId));
        List<KnowledgeQAPO> list = knowledgeQAPOPageResult.getList();
        List<KnowledgeQAPageResultDTO> knowledgeQAPageResultDTOS = list.stream().map(knowledgeQAPO -> {
            KnowledgeQAPageResultDTO knowledgeQAPageResultDTO = BeanUtils.toBean(knowledgeQAPO, KnowledgeQAPageResultDTO.class);
            if (StringUtil.isNotEmpty(knowledgeQAPO.getAttr_id()) && StringUtil.isNotEmpty(knowledgeQAPO.getLabel_id())) {
                Set<String> attr_ids = parseSet(knowledgeQAPO.getAttr_id());
                Set<String> label_ids = parseSet(knowledgeQAPO.getLabel_id());
                String attr_str = attr_ids.stream()
                        .map(attr_id -> KnowledgeCache.attrPOS.stream()
                                .filter(tempAttr -> attr_id.equals(tempAttr.getKnowledge_attr_id()))
                                .findFirst()
                                .map(KnowledgeAttrPO::getKnowledge_attr_name)
                                .orElse(""))
                        .collect(Collectors.joining(" "));
                String label_str = label_ids.stream()
                        .map(label_id -> KnowledgeCache.labelPOS.stream()
                                .filter(tempLabel -> label_id.equals(tempLabel.getKnowledge_attr_label_id()))
                                .findFirst()
                                .map(KnowledgeAttrLabelPO::getKnowledge_attr_label_name)
                                .orElse(""))
                        .collect(Collectors.joining(" "));
                knowledgeQAPageResultDTO.setAttr_str(attr_str);
                knowledgeQAPageResultDTO.setLabel_str(label_str);
            }
            return knowledgeQAPageResultDTO;
        }).collect(Collectors.toList());
        PageResult<KnowledgeQAPageResultDTO> knowledgeQAPageResultDTOPageResult = new PageResult<>();
        knowledgeQAPageResultDTOPageResult.setTotal(knowledgeQAPOPageResult.getTotal());
        knowledgeQAPageResultDTOPageResult.setList(knowledgeQAPageResultDTOS);
        return knowledgeQAPageResultDTOPageResult;
    }

    @ApiMapping(value = "queryDoc", title = "查询文档", description = "查询文档",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.GET)
    public PageResult<KnowledgeDocPageResultDTO> queryDoc(PageParam pageParam) {
        PageResult<KnowledgeDocPO> knowledgeDocPOPageResult = knowledgeDocMapper.selectPage(pageParam,
                new LambdaQueryWrapperX<KnowledgeDocPO>().eq(KnowledgeDocPO::getKnowledge_id, knowledgeBaseId));
        List<KnowledgeDocPO> list = knowledgeDocPOPageResult.getList();
        List<KnowledgeDocPageResultDTO> knowledgeDocPageResultDTOS = list.stream().map(knowledgeDocPO -> {
            KnowledgeDocPageResultDTO knowledgeDocPageResultDTO = BeanUtils.toBean(knowledgeDocPO, KnowledgeDocPageResultDTO.class);
            if (StringUtil.isNotEmpty(knowledgeDocPO.getAttr_id()) && StringUtil.isNotEmpty(knowledgeDocPO.getLabel_id())) {
                Set<String> attr_ids = parseSet(knowledgeDocPO.getAttr_id());
                Set<String> label_ids = parseSet(knowledgeDocPO.getLabel_id());
                String attr_str = attr_ids.stream()
                        .map(attr_id -> KnowledgeCache.attrPOS.stream()
                                .filter(tempAttr -> attr_id.equals(tempAttr.getKnowledge_attr_id()))
                                .findFirst()
                                .map(KnowledgeAttrPO::getKnowledge_attr_name)
                                .orElse(""))
                        .collect(Collectors.joining(" "));
                String label_str = label_ids.stream()
                        .map(label_id -> KnowledgeCache.labelPOS.stream()
                                .filter(tempLabel -> label_id.equals(tempLabel.getKnowledge_attr_label_id()))
                                .findFirst()
                                .map(KnowledgeAttrLabelPO::getKnowledge_attr_label_name)
                                .orElse(""))
                        .collect(Collectors.joining(" "));
                knowledgeDocPageResultDTO.setAttr_str(attr_str);
                knowledgeDocPageResultDTO.setLabel_str(label_str);
            }
            return knowledgeDocPageResultDTO;
        }).collect(Collectors.toList());
        PageResult<KnowledgeDocPageResultDTO> knowledgeQAPageResultDTOPageResult = new PageResult<>();
        knowledgeQAPageResultDTOPageResult.setTotal(knowledgeDocPOPageResult.getTotal());
        knowledgeQAPageResultDTOPageResult.setList(knowledgeDocPageResultDTOS);
        return knowledgeQAPageResultDTOPageResult;
    }

    @ApiMapping(value = "qa", title = "提问", description = "提问",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.POST)
    public List<QAResponseDTO> qa(QARequestDTO qaRequestDTO) throws Exception {
        if (!StringUtil.isNotEmpty(qaRequestDTO.getQuestion())) {
            throw exception(new ErrorCode(400, "问题不能为空"));
        }
        LkeapClient client = getClient();
        RetrieveKnowledgeRequest req = new RetrieveKnowledgeRequest();
        req.setKnowledgeBaseId(knowledgeBaseId);
        req.setQuery(qaRequestDTO.getQuestion());
        req.setRetrievalMethod(qaRequestDTO.getMethod());//FULL_TEXT：全文检索，HYBRID：混合检索，SEMANTIC：语义检索
        if (!StringUtil.isNotEmpty(qaRequestDTO.getType()) && !"ALL".equals(qaRequestDTO.getType())) {
            RetrievalSetting retrievalSetting = new RetrievalSetting();
            retrievalSetting.setType(qaRequestDTO.getType());// QA DOC
            retrievalSetting.setTopK((long) qaRequestDTO.getResultNum());// 个数
            retrievalSetting.setScoreThreshold(qaRequestDTO.getThreshold()); // 相似度
            req.setRetrievalSetting(retrievalSetting);
        }
        if (StringUtil.isNotEmpty(qaRequestDTO.getAttrKey())) {
            LabelItem[] labelItems = new LabelItem[1];
            LabelItem labelItem = new LabelItem();
            labelItem.setName(qaRequestDTO.getAttrKey());
            String[] values = qaRequestDTO.getLabelName().toArray(new String[0]);
            labelItem.setValues(values);
            labelItems[0] = labelItem;
        }

        RetrieveKnowledgeResponse resp = client.RetrieveKnowledge(req);
        List<QAResponseDTO> qaResponseDTOList = new ArrayList<QAResponseDTO>();
        if (resp.getRecords() != null) {
            Long totalCount = resp.getTotalCount();
            RetrievalRecord[] records = resp.getRecords();
            for (RetrievalRecord record : records) {
                String content = record.getContent();
                String title = record.getTitle();
                RetrievalRecordMetadata metadata = record.getMetadata();
                String resultSource = metadata.getResultSource();
                String type = metadata.getType();
                qaResponseDTOList.add(QAResponseDTO.builder()
                        .content(content)
                        .title(title)
                        .type(type)
                        .source(resultSource)
                        .build());
            }
        }
        return qaResponseDTOList;
    }

    @ApiMapping(value = "deleteQA", title = "删除QA", description = "删除QA",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.GET)
    @Transactional(rollbackFor = Exception.class)
    public void deleteQA(String qaId) throws Exception {
        if (knowledgeQAMapper.selectOne(new LambdaQueryWrapperX<KnowledgeQAPO>()
                .eq(KnowledgeQAPO::getCreator, String.valueOf(UserUtil.getUserId()))
                .eq(KnowledgeQAPO::getKnowledge_id, knowledgeBaseId)
                .eq(KnowledgeQAPO::getQa_id, qaId)) == null) {
            throw exception(new ErrorCode(400, "问答对不存在，或者不属于你"));
        }
        LkeapClient client = getClient();
        DeleteQAsRequest req = new DeleteQAsRequest();
        req.setKnowledgeBaseId(knowledgeBaseId);
        String[] qaIds = {qaId};
        req.setQaIds(qaIds);
        client.DeleteQAs(req);
        knowledgeQAMapper.delete(new LambdaQueryWrapperX<KnowledgeQAPO>()
                .eq(KnowledgeQAPO::getQa_id, qaId));
    }

    @ApiMapping(value = "editQA", title = "修改QA", description = "修改QA",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.POST)
    @Transactional(rollbackFor = Exception.class)
    public void editQA(QAUpdateDTO qaUpdateDTO) throws Exception {
        if (knowledgeQAMapper.selectOne(new LambdaQueryWrapperX<KnowledgeQAPO>()
                .eq(KnowledgeQAPO::getCreator, String.valueOf(UserUtil.getUserId()))
                .eq(KnowledgeQAPO::getKnowledge_id, knowledgeBaseId)
                .eq(KnowledgeQAPO::getQa_id, qaUpdateDTO.getQaId())) == null) {
            throw exception(new ErrorCode(400, "问答对不存在，或者不属于你"));
        }
        LkeapClient client = getClient();
        ModifyQARequest req = new ModifyQARequest();
        List<String> attrId = new ArrayList<>(), labelId = new ArrayList();
        if (qaUpdateDTO.getKnowledgeAttrUpdateDTOS() != null && !qaUpdateDTO.getKnowledgeAttrUpdateDTOS().isEmpty()) {
            List<KnowledgeAttrDTO> knowledgeAttrUpdateDTOS = qaUpdateDTO.getKnowledgeAttrUpdateDTOS();
            AttributeLabelReferItem[] attributeLabelReferItems = new AttributeLabelReferItem[knowledgeAttrUpdateDTOS.size()];
            for (int i = 0; i < knowledgeAttrUpdateDTOS.size(); i++) {
                KnowledgeAttrDTO knowledgeAttrInsertDTO = knowledgeAttrUpdateDTOS.get(i);
                AttributeLabelReferItem attributeLabelReferItem = new AttributeLabelReferItem();
                attributeLabelReferItem.setAttributeId(knowledgeAttrInsertDTO.getAttrId());
                String[] labelIds = knowledgeAttrInsertDTO.getLabelIds();
                attributeLabelReferItem.setLabelIds(labelIds);
                attributeLabelReferItems[i] = attributeLabelReferItem;
                attrId.add(knowledgeAttrInsertDTO.getAttrId());
                for (String l : labelIds) {
                    labelId.add(l);
                }
            }
            req.setAttributeLabels(attributeLabelReferItems);
        }
        req.setKnowledgeBaseId(knowledgeBaseId);
        req.setQaId(qaUpdateDTO.getQaId());
        req.setQuestion(qaUpdateDTO.getQuestion());
        req.setAnswer(qaUpdateDTO.getAnswer());
        client.ModifyQA(req);
        KnowledgeQAPO knowledgeQAPO = BeanUtils.toBean(qaUpdateDTO, KnowledgeQAPO.class);
        knowledgeQAPO.setAttr_id(attrId.toString());
        knowledgeQAPO.setLabel_id(labelId.toString());
        knowledgeQAMapper.updateBatch(knowledgeQAPO);
    }

    @ApiMapping(value = "reloadKnowledge", title = "重载知识库", description = "重载知识库",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.GET)
    @Transactional(rollbackFor = Exception.class)
    public void reloadKnowledge() throws Exception {
        LkeapClient client = getClient();
        ListAttributeLabelsRequest req = new ListAttributeLabelsRequest();
        req.setKnowledgeBaseId(knowledgeBaseId);
        ListAttributeLabelsResponse resp = client.ListAttributeLabels(req);
        JSONObject resJson = JSONObject.parseObject(AbstractModel.toJsonString(resp));
        List<KnowledgeAttrPO> knowledgeAttrPOList = new ArrayList<>();
        List<KnowledgeAttrLabelPO> knowledgeAttrLabelPOList = new ArrayList<>();
        resJson.getJSONArray("List").forEach(item -> {
            JSONObject attrJSON = (JSONObject) item;
            KnowledgeAttrPO knowledgeAttrPO = KnowledgeAttrPO.builder()
                    .knowledge_attr_id(attrJSON.getString("AttributeId"))
                    .knowledge_id(knowledgeBaseId)
                    .knowledge_attr_key(attrJSON.getString("AttributeKey"))
                    .knowledge_attr_name(attrJSON.getString("AttributeName"))
                    .build();
            knowledgeAttrPOList.add(knowledgeAttrPO);
            attrJSON.getJSONArray("Labels").forEach(labelItem -> {
                JSONObject labelJSON = (JSONObject) labelItem;
                KnowledgeAttrLabelPO knowledgeAttrLabelPO = KnowledgeAttrLabelPO.builder()
                        .attr_id(attrJSON.getString("AttributeId"))
                        .knowledge_attr_label_id(labelJSON.getString("LabelId"))
                        .knowledge_attr_label_name(labelJSON.getString("LabelName"))
                        .build();
                knowledgeAttrLabelPOList.add(knowledgeAttrLabelPO);
            });
        });
        knowledgeAttrMapper.deleteAll();
        knowledgeAttrLabelMapper.deleteAll();
        knowledgeAttrMapper.insertBatch(knowledgeAttrPOList);
        knowledgeAttrLabelMapper.insertBatch(knowledgeAttrLabelPOList);
        KnowledgeCache.attrPOS = knowledgeAttrPOList;
        KnowledgeCache.labelPOS = knowledgeAttrLabelPOList;
    }

    @ApiMapping(value = "addKnowledge", title = "新增知识库标签", description = "新增知识库标签,需要传入标签列表",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.POST)
    @Transactional(rollbackFor = Exception.class)
    public void addKnowledge(KnowledgeInsertDTO knowledgeInsertDTO) throws Exception {
        List<String> labels = knowledgeInsertDTO.getLabels();
        if (labels.size() == 0) {
            throw exception(new ErrorCode(400, "标签列表不能为空"));
        }

        LkeapClient client = getClient();
        CreateAttributeLabelRequest req = new CreateAttributeLabelRequest();
        req.setKnowledgeBaseId(knowledgeBaseId);
        req.setAttributeKey(knowledgeInsertDTO.getKey());
        req.setAttributeName(knowledgeInsertDTO.getName());
        KnowledgeAttrPO knowledgeAttrPO = KnowledgeAttrPO.builder()
                .knowledge_attr_key(knowledgeInsertDTO.getKey())
                .knowledge_id(knowledgeBaseId)
                .knowledge_attr_name(knowledgeInsertDTO.getName())
                .build();
        knowledgeAttrMapper.insert(knowledgeAttrPO);
        KnowledgeCache.attrPOS = knowledgeAttrMapper.selectList();
        AttributeLabelItem[] attributeLabelItems = new AttributeLabelItem[labels.size()];
        List<KnowledgeAttrLabelPO> knowledgeAttrLabelPOList = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            AttributeLabelItem attributeLabelItem = new AttributeLabelItem();
            attributeLabelItem.setLabelName(labels.get(i));
            attributeLabelItems[i] = attributeLabelItem;
            knowledgeAttrLabelPOList.add(KnowledgeAttrLabelPO.builder()
                    .attr_id(knowledgeAttrPO.getKnowledge_attr_id())
                    .knowledge_attr_label_name(labels.get(i))
                    .build());
        }
        req.setLabels(attributeLabelItems);
        CreateAttributeLabelResponse resp = client.CreateAttributeLabel(req);
        knowledgeAttrLabelMapper.insertBatch(knowledgeAttrLabelPOList);
        KnowledgeCache.labelPOS = knowledgeAttrLabelMapper.selectList();
    }

    @ApiMapping(value = "addQA", title = "新增问题对", description = "新增问题对,可传入标签和属性",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.POST)
    @Transactional(rollbackFor = Exception.class)
    public void addQA(QAInsertDTO qaInsertDTO) throws Exception {
        LkeapClient client = getClient();
        CreateQARequest req = new CreateQARequest();
        req.setKnowledgeBaseId(knowledgeBaseId);
        req.setQuestion(qaInsertDTO.getQuestion());
        req.setAnswer(qaInsertDTO.getAnswer());
        List<String> attrId = new ArrayList<>(), labelId = new ArrayList();
        if (qaInsertDTO.getKnowledgeAttrInsertDTOS() != null && !qaInsertDTO.getKnowledgeAttrInsertDTOS().isEmpty()) {
            List<KnowledgeAttrDTO> knowledgeAttrInsertDTOS = qaInsertDTO.getKnowledgeAttrInsertDTOS();
            AttributeLabelReferItem[] attributeLabelReferItems = new AttributeLabelReferItem[knowledgeAttrInsertDTOS.size()];
            for (int i = 0; i < knowledgeAttrInsertDTOS.size(); i++) {
                KnowledgeAttrDTO knowledgeAttrInsertDTO = knowledgeAttrInsertDTOS.get(i);
                AttributeLabelReferItem attributeLabelReferItem = new AttributeLabelReferItem();
                attributeLabelReferItem.setAttributeId(knowledgeAttrInsertDTO.getAttrId());
                String[] labelIds = knowledgeAttrInsertDTO.getLabelIds();
                attributeLabelReferItem.setLabelIds(labelIds);
                attributeLabelReferItems[i] = attributeLabelReferItem;
                attrId.add(knowledgeAttrInsertDTO.getAttrId());
                for (String l : labelIds) {
                    labelId.add(l);
                }
            }
            req.setAttributeLabels(attributeLabelReferItems);
        }
        CreateQAResponse resp = client.CreateQA(req);

        KnowledgeQAPO knowledgeQAPO = KnowledgeQAPO.builder()
                .qa_id(resp.getQaId())
                .answer(qaInsertDTO.getAnswer())
                .question(qaInsertDTO.getQuestion())
                .creator(String.valueOf(UserUtil.getUserId()))
                .knowledge_id(knowledgeBaseId)
                .attr_id(attrId.toString())
                .label_id(labelId.toString())
                .creator(String.valueOf(UserUtil.getUserId()))
                .build();
        knowledgeQAMapper.insert(knowledgeQAPO);
    }

    @ApiMapping(value = "addDoc", title = "上传文件解析", description = "上传文件解析,到知识库",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.POST)
    @Transactional(rollbackFor = Exception.class)
    public String addDoc(DocInsertDTO docInsertDTO) throws Exception {
        String fileType = docInsertDTO.getFileType();
        if (StringUtil.isEmpty(fileType)) {
            throw exception(new ErrorCode(400, "文件类型不能为空"));
        }
        String[] enableFileType = {"PDF", "DOC", "DOCX", "XLS", "XLSX", "PPTX", "PPT", "MD", "PNG", "TXT", "JPG", "JPEG", "CSV"};
        List<String> list = Arrays.asList(enableFileType);
        if (!list.contains(fileType.toUpperCase(Locale.ROOT))) {
            throw exception(new ErrorCode(400, "不支持文件类型，" + fileType + ",只支持：" + Arrays.toString(enableFileType)));
        }
        LkeapClient client = getClient();
        UploadDocRequest req = new UploadDocRequest();
        req.setKnowledgeBaseId(knowledgeBaseId);
        req.setFileName(docInsertDTO.getFileName());
        req.setFileType(fileType);
        req.setFileUrl(docInsertDTO.getFileUrl());
        List<String> attrId = new ArrayList<>(), labelId = new ArrayList();
        if (docInsertDTO.getKnowledgeAttrInsertDTOS() != null && !docInsertDTO.getKnowledgeAttrInsertDTOS().isEmpty()) {
            List<KnowledgeAttrDTO> knowledgeAttrInsertDTOS = docInsertDTO.getKnowledgeAttrInsertDTOS();
            AttributeLabelReferItem[] attributeLabelReferItems = new AttributeLabelReferItem[knowledgeAttrInsertDTOS.size()];
            for (int i = 0; i < knowledgeAttrInsertDTOS.size(); i++) {
                KnowledgeAttrDTO knowledgeAttrInsertDTO = knowledgeAttrInsertDTOS.get(i);
                AttributeLabelReferItem attributeLabelReferItem = new AttributeLabelReferItem();
                attributeLabelReferItem.setAttributeId(knowledgeAttrInsertDTO.getAttrId());
                String[] labelIds = knowledgeAttrInsertDTO.getLabelIds();
                attributeLabelReferItem.setLabelIds(labelIds);
                attributeLabelReferItems[i] = attributeLabelReferItem;
                attrId.add(knowledgeAttrInsertDTO.getAttrId());
                for (String l : labelIds) {
                    labelId.add(l);
                }
            }
            req.setAttributeLabels(attributeLabelReferItems);
        }
        UploadDocResponse resp = client.UploadDoc(req);

        KnowledgeDocPO knowledgeDocPO = KnowledgeDocPO.builder()
                .doc_id(resp.getDocId())
                .doc_name(docInsertDTO.getFileName())
                .doc_type(fileType)
                .doc_url(docInsertDTO.getFileUrl())
                .knowledge_id(knowledgeBaseId)
                .attr_id(attrId.toString())
                .label_id(labelId.toString())
                .build();
        knowledgeDocMapper.insert(knowledgeDocPO);
        return resp.getDocId();
    }

    @ApiMapping(value = "getDocStatus", title = "获取文件解析状态", description = "获取文件解析状态",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.GET)
    public DocStatusResponseDTO getDocStatus(String docId) throws Exception {
        LkeapClient client = getClient();
        DescribeDocRequest req = new DescribeDocRequest();
        req.setKnowledgeBaseId(knowledgeBaseId);
        req.setDocId(docId);
        DescribeDocResponse resp = client.DescribeDoc(req);
        DocStatusResponseDTO docStatusResponseDTO = new DocStatusResponseDTO(resp.getFileName(), resp.getStatus());
        return docStatusResponseDTO;
    }

    @ApiMapping(value = "reloadQA", title = "重载问答", description = "重载问答",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.GET)
    @Transactional(rollbackFor = Exception.class)
    public void reloadQA() throws Exception {
        LkeapClient client = getClient();
        ListQAsRequest req = new ListQAsRequest();
        req.setKnowledgeBaseId(knowledgeBaseId);
        ListQAsResponse resp = client.ListQAs(req);
        List<KnowledgeQAPO> knowledgeQAPOList = new ArrayList<>();
        for (QaItem qaItem : resp.getList()) {
            KnowledgeQAPO.KnowledgeQAPOBuilder knowledgeQAPOBuilder = KnowledgeQAPO.builder()
                    .qa_id(qaItem.getQaId())
                    .answer(qaItem.getAnswer())
                    .question(qaItem.getQuestion())
                    .date(qaItem.getCreateTime())
                    .creator(String.valueOf(UserUtil.getUserId()))
                    .knowledge_id(knowledgeBaseId);
            List<String> attrId = new ArrayList<>(), labelId = new ArrayList();
            if (qaItem.getAttributeLabels() != null) {
                for (AttributeLabelReferItem r : qaItem.getAttributeLabels()) {
                    attrId.add(r.getAttributeId());
                    for (String l : r.getLabelIds()) {
                        labelId.add(l);
                    }
                }
                knowledgeQAPOBuilder.attr_id(attrId.toString()).label_id(labelId.toString());
            }
            knowledgeQAPOList.add(knowledgeQAPOBuilder.build());
        }
        knowledgeQAMapper.deleteAll();
        knowledgeQAMapper.insertBatch(knowledgeQAPOList);
    }

    @ApiMapping(value = "rerank", title = "重排序", description = "重排序",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.POST)
    public Float[] rerank(String input, String[] querys) throws Exception {
        LkeapClient client = getClient();
        RunRerankRequest req = new RunRerankRequest();
        req.setQuery(input);
        req.setDocs(querys);
        req.setModel("lke-reranker-base");
        RunRerankResponse resp = client.RunRerank(req);
        Float[] scoreList = resp.getScoreList();
        return Arrays.stream(scoreList).map(f -> (f + 10) / 10).toArray(Float[]::new);
    }

    @ApiMapping(value = "rewrite", title = "多轮改写", description = "多轮改写",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.POST)
    public String rewrite(RewriteMessageQueryDTO messageQueryDTO) throws Exception {
        LkeapClient client = getClient();
        QueryRewriteRequest req = new QueryRewriteRequest();
        if (messageQueryDTO.getModel() != null) {
            req.setModel(messageQueryDTO.getModel());
        }
        List<RewriteMessageDTO> messageDTOList = messageQueryDTO.getMessageDTOList();
        Message[] messages = new Message[messageDTOList.size()];
        for (int i = 0; i < messageDTOList.size(); i++) {
            RewriteMessageDTO messageDTO = messageDTOList.get(i);
            Message message = new Message();
            message.setRole(messageDTO.getRole());
            message.setContent(messageDTO.getContent());
            message.setReasoningContent(messageDTO.getReasoningContent());
            messages[i] = message;
        }
        req.setMessages(messages);
        QueryRewriteResponse resp = client.QueryRewrite(req);
        return resp.getContent();
    }


    private Set<String> parseSet(String str) {
        String[] arr = str.substring(1, str.length() - 1).split(",\\s*");
        Set<String> numbersList = Arrays.stream(arr)
                .map(String::trim)
                .collect(Collectors.toSet());
        return numbersList;
    }

}

