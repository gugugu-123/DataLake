package com.example.demo.vo;

import com.example.demo.common.commonEnum.ModelEnum;
import com.example.demo.common.validator.Model;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiOperation(value = "文件上传VO")
public class FileUploadVO {

    @ApiModelProperty(value = "上传模式,(append、overwrite、error、errorifexists、ignore)", required = true)
    @Model(message = "mode异常", target = ModelEnum.class)
    private String mode;

    @ApiModelProperty(value = "最终路径", required = false)
    private String dstDeltaTablePath;

    @ApiModelProperty(value = "临时路径", required = false)
    private String srcFilePath;

    @ApiModelProperty(value = "表", required = false)
    private String table;

    @ApiModelProperty(value = "表", required = false)
    private FileUploadOptionVO options;
}
