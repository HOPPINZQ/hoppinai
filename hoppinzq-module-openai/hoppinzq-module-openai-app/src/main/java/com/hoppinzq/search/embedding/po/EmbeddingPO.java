package com.hoppinzq.search.embedding.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("embedding")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmbeddingPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String embedding;
    private String tableName;
    private String tableId;
    private String data;

    @TableField(exist = false)
    private Double similarity;
}
