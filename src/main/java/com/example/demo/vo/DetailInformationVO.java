package com.example.demo.vo;

import com.example.demo.common.validator.TimeCheck;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@ApiOperation(value = "条件查询VO")
public class DetailInformationVO {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("text")
    private String text;

    @ApiModelProperty("起始时间")
    @TimeCheck
    private String startTime;

    @ApiModelProperty("结束时间")
    @TimeCheck
    private String endTime;

    @ApiModelProperty("source")
    private String source;

    @ApiModelProperty("symbols")
    private String symbols;

    @ApiModelProperty("公司名")
    private String companyName;

    @ApiModelProperty(value = "sql,条件查询时为空", required = false)
    private String sql;

    @ApiModelProperty(value = "表名,自定义sql时为空", required = false)
    private String table;

    @ApiModelProperty(value = "当前页", required = true)
    private Integer page = 1;

    @ApiModelProperty(value = "一页限制数", required = true)
    private Integer pageSize = 20;
}
