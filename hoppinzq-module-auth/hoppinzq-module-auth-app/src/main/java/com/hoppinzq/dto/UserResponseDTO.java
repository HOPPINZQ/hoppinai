package com.hoppinzq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private String id;
    private String username;
    private String user_description;
    private String user_image;
    private Integer user_right;
    private String login_type;
}
