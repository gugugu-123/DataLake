package com.example.demo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FileInformationVO {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("text")
    private String text;

    @ApiModelProperty("起始时间")
    private String timeStamp;

    @ApiModelProperty("source")
    private String source;

    @ApiModelProperty("symbols")
    private String symbols;

    @ApiModelProperty("公司名")
    private String companyName;

    @ApiModelProperty("当前页数")
    private Integer page = 1;

    @ApiModelProperty("一页数据条数")
    private Integer pageSize;
}
