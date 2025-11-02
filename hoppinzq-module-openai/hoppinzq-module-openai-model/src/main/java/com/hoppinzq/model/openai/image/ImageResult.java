package com.hoppinzq.model.openai.image;

import lombok.Data;

import java.util.List;

@Data
public class ImageResult {
    Long created;
    List<Image> data;
}
