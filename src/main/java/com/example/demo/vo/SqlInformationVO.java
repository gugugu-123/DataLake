package com.example.demo.vo;

import com.example.demo.model.SqlModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiOperation(value = "sql查询VO")
public class SqlInformationVO {

    @NotNull(message = "sql不能为空")
    @ApiModelProperty(value = "sqls,以分号(;)分割", required = true)
    private SqlModel sqls;

    @ApiModelProperty(value = "当前页", required = true)
    private Integer page = 1;

    @ApiModelProperty(value = "一页限制数", required = true)
    private Integer pageSize = 15;
}
