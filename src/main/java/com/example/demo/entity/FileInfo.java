package com.example.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("file_map")
public class FileInfo {

    @Id
    private String md5Hex;

    private String dstDeltaTablePath;

    private String fileName;

    private String suffix;

    private String fileType;

    private Long timestamp;

    private Date createTime;

    private Date updateTime;
}
