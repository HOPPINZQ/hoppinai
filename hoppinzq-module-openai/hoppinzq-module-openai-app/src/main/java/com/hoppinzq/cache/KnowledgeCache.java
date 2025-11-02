package com.hoppinzq.cache;

import com.hoppinzq.dal.dao.KnowledgeAttrLabelMapper;
import com.hoppinzq.dal.dao.KnowledgeAttrMapper;
import com.hoppinzq.dal.po.KnowledgeAttrLabelPO;
import com.hoppinzq.dal.po.KnowledgeAttrPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 知识库缓存
 */
@Component
@Slf4j
public class KnowledgeCache implements InitializingBean {
    public static List<KnowledgeAttrPO> attrPOS = Collections.synchronizedList(new ArrayList<KnowledgeAttrPO>());
    public static List<KnowledgeAttrLabelPO> labelPOS = Collections.synchronizedList(new ArrayList<KnowledgeAttrLabelPO>());

    @Autowired
    private KnowledgeAttrMapper knowledgeAttrMapper;
    @Autowired
    private KnowledgeAttrLabelMapper knowledgeAttrLabelMapper;

    @Override
    public void afterPropertiesSet() {
        log.info("-----------------------------------------");
        log.info("初始化知识库");
        attrPOS = knowledgeAttrMapper.selectList();
        labelPOS = knowledgeAttrLabelMapper.selectList();
        log.info("-----------------------------------------");
    }
}
