package com.example.demo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileDownloadVO {

    @ApiModelProperty(value = "表名")
    private String deltaTablePath;

    @ApiModelProperty(value = "条件")
    private String whereClause;

}
