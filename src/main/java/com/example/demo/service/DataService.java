package com.example.demo.service;

import com.example.demo.entity.FileInfo;
import com.example.demo.exception.SqlException;
import com.example.demo.exception.SparkServerException;
import com.example.demo.exception.TableNotExistException;
import com.example.demo.model.SqlResultModel;
import com.example.demo.vo.DetailInformationVO;
import com.example.demo.vo.FileUploadVO;
import com.example.demo.vo.Result;
import com.example.demo.vo.SqlInformationVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface DataService {
    void generateDataMap(FileInfo fileMap);

    String createSelectSql(DetailInformationVO detailInformationVO);

    Result<List<SqlResultModel>> getSqlResult(SqlInformationVO sqlInformationVO) throws SqlException, SparkServerException;

    Result checkSql(String selectSql);

    Result<Void> upload(MultipartFile uploadFile, FileUploadVO fileUploadVO) throws SparkServerException, IOException;

    Result<List<String>> getTables();

    void download(String sql, HttpServletResponse response, DetailInformationVO detailInformationVO) throws SparkServerException, IOException, TableNotExistException;
}
