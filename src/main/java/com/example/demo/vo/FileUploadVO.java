package com.example.demo.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiOperation(value = "文件上传VO")
public class FileUploadVO {

    @ApiModelProperty(value = "上传模式,(append、overwrite、error、errorifexists、ignore)", required = true)
    private String mode;

    @ApiModelProperty(value = "最终路径", required = true)
    @NotBlank(message = "请选择最终存储路径")
    private String dstDeltaTablePath;

    @ApiModelProperty(value = "临时路径", required = false)
    private String srcFilePath;
}
