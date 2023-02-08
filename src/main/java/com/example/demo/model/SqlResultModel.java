package com.example.demo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SqlResultModel {

    @ApiModelProperty("sql")
    private String sqlText;

    @ApiModelProperty("文件类型")
    private String fileType;

    @ApiModelProperty("数据总量")
    private Integer total;

    @ApiModelProperty("执行结果")
    private String resultTable;
}
