package com.hoppinzq.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("csgo_match_copy")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CSGOPO {

    @TableId
    private int id;

    private String map;
    private String mapEn;
    private String mapUrl;
    private String mapLogo;
    private String startTime;
    private String endTime;
    private String duration;
    private String win;
    private String score1;
    private String score2;
    private String halfScore1;
    private String halfScore2;
    private String mode;
    private String playerId;
    private String highlightsData;
    private String nickName;
    private String avatar;
    private String killNum;
    private String negKill;
    private String handGunKill;
    private String entryKill;
    private String awpKill;
    private String death;
    private String entryDeath;
    private String assist;
    private String headShot;
    private String headShotRatio;
    private String rating;
    private String pwRating;
    private String itemThrow;
    private String flash;
    private String flashTeammate;
    private String flashSuccess;
    private String twoKill;
    private String threeKill;
    private String fourKill;
    private String fiveKill;
    private String vs1;
    private String vs2;
    private String vs3;
    private String vs4;
    private String vs5;
    private String headShotCount;
    private String dmgArmor;
    private String dmgHealth;
    private String adpr;
    private String fireCount;
    private String hitCount;
    private String rws;
    private String firstDeath;
    private String snipeNum;
    private String mvp;
    private String matchId;
    private String isXiuxiu;
    private String isLiuyucheng;
    private String isZq;
    private String json;
}

