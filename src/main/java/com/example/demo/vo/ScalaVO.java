package com.example.demo.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScalaVO {

    @ApiModelProperty(value = "scala表达式", required = true)
    @NotBlank(message = "表达式不能为空")
    private String script;

}
