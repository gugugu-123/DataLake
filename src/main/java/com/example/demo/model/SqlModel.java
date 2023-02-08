package com.example.demo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SqlModel {

    @ApiModelProperty("sql")
    private String sqlQueries;

}
