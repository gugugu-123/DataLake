package com.example.demo.controller;


import com.example.demo.exception.SqlException;
import com.example.demo.exception.SparkServerException;
import com.example.demo.exception.TableNotExistException;
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
import java.util.List;

@Api(tags = "数据管理")
@RestController
@RequestMapping("data")
public class DataController {

    @Autowired
    private DataService dataService;

    @ApiOperation("文件上传")
    @PostMapping("uploadFile")
    public Result<Void> uploadFile(@RequestPart MultipartFile file, FileUploadVO fileUploadVO) throws IOException, SparkServerException {
        return dataService.upload(file, fileUploadVO);
    }

    @ApiOperation("数据下载")
    @PostMapping("download")
    public void download(@Validated DetailInformationVO detailInformationVO, String sql, HttpServletResponse response) throws IOException, SparkServerException, TableNotExistException {
        dataService.download(sql, response, detailInformationVO);
    }

    @ApiOperation("条件查询")
    @GetMapping("selectCondition")
    public Result<List<SqlResultModel>> selectCondition(@Validated DetailInformationVO detailInformationVO) throws SqlException, SparkServerException {
        String selectSql = dataService.createSelectSql(detailInformationVO);
        SqlInformationVO sqlInformationVO = new SqlInformationVO();
        sqlInformationVO.setPage(detailInformationVO.getPage());
        sqlInformationVO.setPageSize(detailInformationVO.getPageSize());
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

}
