package com.example.demo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileUploadOptionVO {

    @ApiModelProperty(name = "上传csv文件时有无表头")
    private String header = "true";

    @ApiModelProperty(name = "是否需要推断Schema")
    private String inferSchema = "true";
}
