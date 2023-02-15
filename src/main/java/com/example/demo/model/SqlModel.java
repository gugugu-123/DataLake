package com.example.demo.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class SqlModel {

    @ApiModelProperty("sql")
    private String sqlQueries;

    public SqlModel() {
    }

    public SqlModel(String sqlQueries) {
        this.sqlQueries = sqlQueries;
    }
}
