package com.hoppinzq.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CSGOMapper extends BaseMapper<CSGOPO> {

    @Select("SELECT SUM(duration) '总时长（分钟）' ,SUM(kill_num) '总击杀',ROUND(AVG(kill_num), 2) '平均击杀',\n" +
            "\tSUM(entry_kill) '总首杀',ROUND(AVG(entry_kill), 2) '平均首杀',SUM(death) '总死亡',ROUND(AVG(death), 2) '平均死亡',\n" +
            "\tSUM(assist) '总助攻',ROUND(AVG(assist), 2) '平均助攻',SUM(head_shot) '总爆头击杀',ROUND(AVG(head_shot_ratio), 2) '平均爆头率',\n" +
            "\tROUND(AVG(rating), 2) '平均rating',ROUND(AVG(dmg_health), 2) '平均伤害',\n" +
            "\tSUM(snipe_num) '狙杀次数',SUM(two_kill) '双杀次数',SUM(three_kill) '三杀次数',SUM(four_kill) '四杀次数',SUM(five_kill) '五杀次数',\n" +
            "\tSUM(vs1) '残局1v1次数',SUM(vs2) '残局1v2次数',SUM(vs3) '残局1v3次数',SUM(vs4) '残局1v4次数',SUM(vs5) '残局1v5次数'\n" +
            " FROM csgo_match_copy c WHERE player_id = #{playerId}")
    Map<String, String> getMatchInfo(String playerId);

    @Select("SELECT \n" +
            "map '地图',\n" +
            "  COUNT(*) AS '比赛场数',\n" +
            "  SUM(CASE WHEN win = 'win' THEN 1 ELSE 0 END) AS '胜场',\n" +
            "  SUM(CASE WHEN win = 'lose' THEN 1 ELSE 0 END) AS '负场',\n" +
            "  SUM(CASE WHEN win = 'draw' THEN 1 ELSE 0 END) AS '平局',\n" +
            "  ROUND(SUM(CASE WHEN win = 'win' THEN 1 ELSE 0 END) / (SELECT COUNT(*) FROM csgo_match_copy c1 WHERE c1.win!='draw' AND c1.map = c.map AND c1.player_id = #{playerId}) * 100, 2) AS '胜率',\n" +
            "SUM(score1) '总局数' ,SUM(kill_num) '总击杀',ROUND(AVG(kill_num), 2) '平均击杀',\n" +
            "\tSUM(entry_kill) '总首杀',ROUND(AVG(entry_kill), 2) '平均首杀',SUM(death) '总死亡',ROUND(AVG(death), 2) '平均死亡',\n" +
            "\tSUM(assist) '总助攻',ROUND(AVG(assist), 2) '平均助攻',SUM(head_shot) '总爆头击杀',ROUND(AVG(head_shot_ratio), 2) '平均爆头率',\n" +
            "\tROUND(AVG(rating), 2) '平均rating',ROUND(AVG(dmg_health), 2) '平均伤害',ROUND(AVG(first_death), 2) '首死',\n" +
            "\tSUM(snipe_num) '狙杀次数',SUM(two_kill) '双杀次数',SUM(three_kill) '三杀次数',SUM(four_kill) '四杀次数',SUM(five_kill) '五杀次数',\n" +
            "\tSUM(vs1) '残局1v1次数',SUM(vs2) '残局1v2次数',SUM(vs3) '残局1v3次数',SUM(vs4) '残局1v4次数',SUM(vs5) '残局1v5次数',\n" +
            "\tROUND(AVG(fire_count), 2) '燃烧弹伤害'\n" +
            " FROM csgo_match_copy c WHERE player_id = #{playerId} GROUP BY\n" +
            " map;")
    List<Map<String, String>> getMapInfo(String playerId);
}

