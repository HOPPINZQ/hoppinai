package com.hoppinzq.service.mcp;

import com.hoppinzq.dal.dao.PublicMCPTypeMapper;
import com.hoppinzq.dal.po.PublicMCPTypePO;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@ApiServiceMapping(title = "提示词")
public class PublicMCPService {

    @Autowired
    private PublicMCPTypeMapper publicMCPTypeMapper;
    @Autowired
    private RedisUtils redisUtils;

    @PostConstruct
    public synchronized void initLocalCache() {
        List<PublicMCPTypePO> publicMCPTypePOS = publicMCPTypeMapper.queryMCP();
        redisUtils.set("mcp_ai", publicMCPTypePOS);
    }

    @ApiMapping(value = "publicMcp", title = "获取公开的MCP服务", description = "获取公开的MCP服务", returnType = false)
    public List<PublicMCPTypePO> publicPrompt() {
        Object publicMCPTypePOS = redisUtils.get("mcp_ai");
        return (List<PublicMCPTypePO>) publicMCPTypePOS;
    }
}

