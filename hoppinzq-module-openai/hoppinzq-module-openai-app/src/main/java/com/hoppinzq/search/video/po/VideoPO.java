package com.hoppinzq.search.video.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hoppinzq.search.SearchPO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("video")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoPO extends SearchPO {

    @TableId
    private String videoId;

    private String videoName;

    private String videoMiaoshu;
}
