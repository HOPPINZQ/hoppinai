package com.hoppinzq.mcp.lr;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;


@Service
public class LrService {

    @Tool(name = "wolf_kill", description = "狼人杀人，注意，只有狼人在夜晚才能使用该工具")
    public String kill(@ToolParam(description = "狼人的编号") String wolfNo,
                        @ToolParam(description = "要杀的玩家的编号") String targetNo) {
        return wolfNo+":"+targetNo;
    }

    @Tool(name = "prophet_check", description = "预言家检查人的身份，注意，只有预言家在预言家阶段才能使用该工具")
    public String prophet(@ToolParam(description = "预言家的编号") String prophetNo,
                            @ToolParam(description = "要检查的玩家的编号") String targetNo) {
        return prophetNo+":"+targetNo;
    }

    @Tool(name = "witch_action", description = "女巫行动，可以使用该工具提供毒药或者解药，解药是antidote，毒药是poison。注意，只有女巫在女巫阶段，且有毒药或者解药时才能使用该工具")
    public String witch(@ToolParam(description = "女巫的编号") String witchNo,
                        @ToolParam(description = "要操作的玩家的编号") String playerNo
                         , @ToolParam(description = "毒药或是解药，解药传antidote，毒药传poison") String poison
    ) {
        if("antidote".equalsIgnoreCase(poison))
            return witchNo+":antidote:"+playerNo;
        if("poison".equalsIgnoreCase(poison))
            return witchNo+"poison:"+playerNo;
        else throw new RuntimeException("没有提供正确的毒药或者解药，行动失败！");
    }

    @Tool(name = "hunter_shoot", description = "猎人射击，注意，只有猎人在猎人阶段，且猎人被杀死后才能使用该工具")
    public String shoot(@ToolParam(description = "猎人的编号") String hunterNo
                        , @ToolParam(description = "被射击者的编号") String targetNo
    ) {
        return hunterNo + ":" + targetNo;
    }

    @Tool(name = "vote", description = "投票，所有玩家都能在投票阶段使用该工具")
    public String save(@ToolParam(description = "投票玩家的编号") String playerNo
                       , @ToolParam(description = "被投票玩家的编号") String targetNo
    ) {
        return playerNo + ":" + targetNo;
    }

}
