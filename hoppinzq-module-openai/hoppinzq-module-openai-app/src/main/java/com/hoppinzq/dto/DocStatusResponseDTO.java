package com.hoppinzq.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocStatusResponseDTO {
    private String fileName;
    private String fileStatus;

    public DocStatusResponseDTO(String fileName, String fileStatus) {
        this.fileName = fileName;
        this.fileStatus = transformStatus(fileStatus);
    }


    public String transformStatus(String status) {
        switch (status) {
            case "Uploading":
                return "上传中";
            case "Auditing":
                return "解析中";
            case "Parsing":
                return "解析中";
            case "ParseFailed":
                return "解析失败";
            case "Indexing":
                return "创建索引中";
            case "IndexFailed":
                return "创建索引失败";
            case "Success":
                return "发布成功";
            case "Failed":
                return "失败";
            default:
                return "未知状态";
        }
    }
}

