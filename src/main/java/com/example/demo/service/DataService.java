package com.example.demo.service;

import com.example.demo.entity.FileInfo;
import com.example.demo.exception.SqlException;
import com.example.demo.exception.SparkServerException;
import com.example.demo.exception.TableNotExistException;
import com.example.demo.model.DoScalaResultModel;
import com.example.demo.model.SqlResultModel;
import com.example.demo.vo.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface DataService {
    void generateDataMap(FileInfo fileMap);

    String createSelectSql(DetailInformationVO detailInformationVO) throws ParseException;

    Result<List<SqlResultModel>> getSqlResult(SqlInformationVO sqlInformationVO) throws SqlException, SparkServerException;

    Result checkSql(String selectSql);

    Result<String> upload(MultipartFile uploadFile, FileUploadVO fileUploadVO) throws SparkServerException, IOException;

    Result<List<String>> getTables();

    void download(String sql, HttpServletResponse response, DetailInformationVO detailInformationVO) throws SparkServerException, IOException, TableNotExistException, ParseException;

    Result<DoScalaResultModel> doScala(ScalaVO scalaVO) throws SparkServerException;
}
