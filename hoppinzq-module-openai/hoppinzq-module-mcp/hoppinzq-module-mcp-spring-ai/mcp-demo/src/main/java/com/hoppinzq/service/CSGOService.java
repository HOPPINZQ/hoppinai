package com.hoppinzq.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoppinzq.dao.CSGOMapper;
import com.hoppinzq.dao.CSGOPO;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 演示用AI分析数据
 * 需要配合提示词食用，示例如下：
 */
//     ## 你是一个CSGO游戏数据总结大师，现在有一些CSGO的数据需要你来总结
//     1. 统计玩家在所有地图上的比赛场数、胜场、负场、平局、胜率、总时长、总击杀、平均击杀、总首杀、平均首杀、总死亡、平均死亡、总助攻、平均助攻、总爆头击杀、平均爆头率、平均rating、平均伤害、首死、狙杀次数、双杀、三杀、四杀、五杀、1v1、1v2、1v3、1v4、1v5的数据，并总结这些数据
//     2. 统计玩家的总场数、胜场和胜率，并总结这些数据
//     3. 关注玩家的总时长、总击杀、平均击杀、总首杀、平均首杀、总死亡、平均死亡、总助攻、平均助攻、总爆头击杀、平均爆头率、平均rating、平均伤害、首死、狙杀次数、双杀、三杀、四杀、五杀、1v1、1v2、1v3、1v4、1v5的数据，并总结这些数据
@Service
public class CSGOService {

    private final List<CSGOPlayer> csgoPlayers = new ArrayList<>();
    String zqId = "76561198139937186";
    String xxId = "76561198355733127";
    @Resource
    private CSGOMapper csgoMapper;

    @Tool(name = "get_player_id_by_name", description = "通过名字获取CSGO的playerId")
    public CSGOPlayer getPlayerId(@ToolParam(required = true, description = "姓名") String name) {
        return csgoPlayers.stream().filter(csgoPlayer -> csgoPlayer.name().equals(name)).findFirst().orElse(null);
    }

    @Tool(name = "get_csgo_data", description = "获取指定player的CSGO数据库数据。playerID必须通过get_player_id_by_name获取")
    public JSONObject getMatch(@ToolParam(required = true, description = "playerID，必须通过get_player_id_by_name获取") String playerId) {
        Long matchCount = csgoMapper.selectCount(new LambdaQueryWrapper<CSGOPO>().eq(CSGOPO::getPlayerId, playerId));
        Long matchWinCount = csgoMapper.selectCount(new LambdaQueryWrapper<CSGOPO>().eq(CSGOPO::getPlayerId, playerId).eq(CSGOPO::getWin, "win"));
        Long matchLoseCount = csgoMapper.selectCount(new LambdaQueryWrapper<CSGOPO>().eq(CSGOPO::getPlayerId, playerId).eq(CSGOPO::getWin, "lose"));
        Long matchDrawCount = csgoMapper.selectCount(new LambdaQueryWrapper<CSGOPO>().eq(CSGOPO::getPlayerId, playerId).eq(CSGOPO::getWin, "draw"));
        Map<String, String> matchInfo = csgoMapper.getMatchInfo(playerId);
        List<Map<String, String>> mapInfo = csgoMapper.getMapInfo(playerId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("总场数", matchCount);
        jsonObject.put("胜场数", matchWinCount);
        jsonObject.put("胜率", (Math.round((double) matchWinCount / (matchCount - matchDrawCount) * 10000) / 100.0) + "%");
        jsonObject.put("负场数", matchLoseCount);
        jsonObject.put("平局场数", matchDrawCount);
        jsonObject.put("全部比赛数据统计", JSON.toJSON(matchInfo));
        jsonObject.put("地图表现", JSON.toJSON(mapInfo));
        return jsonObject;
    }

    @PostConstruct
    public void init() {
        csgoPlayers.addAll(List.of(
                new CSGOPlayer("张祺", zqId),
                new CSGOPlayer("范狗", xxId)
        ));
    }
}

record CSGOPlayer(String name, String player) {
}
