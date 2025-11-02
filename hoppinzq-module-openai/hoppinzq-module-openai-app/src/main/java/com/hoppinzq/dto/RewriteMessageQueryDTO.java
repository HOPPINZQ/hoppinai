package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RewriteMessageQueryDTO {

    private List<RewriteMessageDTO> messageDTOList;
    private String model;
}

