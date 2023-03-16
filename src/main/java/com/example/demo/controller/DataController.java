package com.example.demo.controller;

import com.example.demo.exception.SqlException;
import com.example.demo.exception.SparkServerException;
import com.example.demo.exception.TableNotExistException;
import com.example.demo.model.DoScalaResultModel;
import com.example.demo.vo.ScalaVO;
import com.example.demo.model.SqlModel;
import com.example.demo.model.SqlResultModel;
import com.example.demo.service.DataService;
import com.example.demo.vo.DetailInformationVO;
import com.example.demo.vo.FileUploadVO;
import com.example.demo.vo.Result;
import com.example.demo.vo.SqlInformationVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.util.List;

@Api(tags = "数据管理")
@RestController
@RequestMapping("data")
public class DataController {

    @Autowired
    private DataService dataService;

    @ApiOperation("文件上传")
    @PostMapping("uploadFile")
    public Result<String> uploadFile(@RequestPart MultipartFile file, @Validated FileUploadVO fileUploadVO) throws IOException, SparkServerException {
        if (file == null) {
            return Result.error("请上传文件");
        }
        return dataService.upload(file, fileUploadVO);
    }

    @ApiOperation("数据下载")
    @PostMapping("download")
    public void download(@Validated @RequestBody DetailInformationVO detailInformationVO, HttpServletResponse response) throws IOException, SparkServerException, TableNotExistException, ParseException {
        dataService.download(detailInformationVO.getSql(), response, detailInformationVO);
    }

    @ApiOperation("条件查询")
    @GetMapping("selectCondition")
    public Result<List<SqlResultModel>> selectCondition(@Validated DetailInformationVO detailInformationVO) throws SqlException, SparkServerException, ParseException {
        String selectSql = dataService.createSelectSql(detailInformationVO);
        SqlInformationVO sqlInformationVO = new SqlInformationVO();
        sqlInformationVO.setPage(detailInformationVO.getPage());
        sqlInformationVO.setPageSize(detailInformationVO.getPageSize());
        sqlInformationVO.setSqls(new SqlModel(selectSql));
        return dataService.getSqlResult(sqlInformationVO);
    }

    @ApiOperation("sql查询")
    @GetMapping("sql")
    public Result<List<SqlResultModel>> getSqlResult(SqlInformationVO sqlInformationVO) throws SqlException, SparkServerException {
        return dataService.getSqlResult(sqlInformationVO);
    }

    @ApiOperation("获取表集合")
    @GetMapping("getTables")
    public Result<List<String>> getTables() {
        return dataService.getTables();
    }

    @ApiOperation("执行Scala代码")
    @PostMapping("doScala")
    public Result<DoScalaResultModel> doScala(@RequestBody ScalaVO scalaModel) throws SparkServerException {
        return dataService.doScala(scalaModel);
    }

}
