package com.example.demo.service.Impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.demo.common.commonEnum.FileTypeEnum;
import com.example.demo.common.commonEnum.SqlTypeEnum;
import com.example.demo.common.conversion.DataFormatConversion;
import com.example.demo.common.conversion.Impl.CsvDataConversion;
import com.example.demo.common.commonEnum.ModelEnum;
import com.example.demo.common.commonEnum.ResponseEnum;
import com.example.demo.common.conversion.Impl.JsonDataConversion;
import com.example.demo.common.conversion.Impl.PictureDataConversion;
import com.example.demo.common.processor.Impl.DeleteSqlProcessor;
import com.example.demo.common.processor.Impl.InsertSqlProcessor;
import com.example.demo.common.processor.Impl.SelectSqlProcessor;
import com.example.demo.common.processor.Impl.UpdateSqlProcessor;
import com.example.demo.common.processor.SqlProcessor;
import com.example.demo.entity.FileInfo;
import com.example.demo.entity.Table;
import com.example.demo.exception.SparkServerException;
import com.example.demo.exception.SqlException;
import com.example.demo.exception.TableNotExistException;
import com.example.demo.model.FileDownloadVO;
import com.example.demo.model.SqlModel;
import com.example.demo.model.SqlResultModel;
import com.example.demo.model.TableModel;
import com.example.demo.service.DataService;
import com.example.demo.util.HttpUtils;
import com.example.demo.vo.DetailInformationVO;
import com.example.demo.vo.FileUploadVO;
import com.example.demo.vo.Result;
import com.example.demo.vo.SqlInformationVO;
import lombok.extern.slf4j.Slf4j;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataServiceImpl implements DataService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${dataLake.sparkServer}")
    private String url;

    @Value("${dataLake.table}")
    private String table;

    @Value("${dataLake.hdfsServer}")
    private String hdfsServer;

    @Value("${dataLake.tablePath}")
    private String tablePath;

    @Autowired
    private Executor executor;

    @Override
    public void generateDataMap(FileInfo fileMap) {
        mongoTemplate.save(fileMap);
    }

    @Override
    public String createSelectSql(DetailInformationVO detailInformationVO) throws ParseException {
        StringBuffer originSql = new StringBuffer("select * from " + detailInformationVO.getTable() + " where 1 = 1");
        String id = detailInformationVO.getId();
        Table table = mongoTemplate.findOne(Query.query(Criteria.where("name").is(detailInformationVO.getTable())), Table.class);
        List<String> columns = new ArrayList<>();
        if (table.getColumns() != null) {
            columns = table.getColumns();
        }
        if (StrUtil.isNotBlank(id) && columns.contains("id")) {
            originSql.append(" and id = ");
            originSql.append(id);
        }

        String startTime = detailInformationVO.getStartTime();
        String endTime = detailInformationVO.getEndTime();

        if (startTime != null && columns.contains("timestamp")) {
            long start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime).getTime();
            originSql.append(" and timestamp >= ");
            originSql.append(start);
        }
        if (endTime != null && columns.contains("timestamp")) {
            long end = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime).getTime();
            originSql.append(" and timestamp <= ");
            originSql.append(end);
        }
        String symbols = detailInformationVO.getSymbols();
        if (StrUtil.isNotBlank(symbols) && columns.contains("symbols")) {
            addLikeCondition("symbols", symbols, originSql);
        }
        String text = detailInformationVO.getText();
        if (StrUtil.isNotBlank(text) && columns.contains("text")) {
            addLikeCondition("text", text, originSql);
        }
        String companyName = detailInformationVO.getCompanyName();
        if (StrUtil.isNotBlank(companyName) && columns.contains("company_name")) {
            addLikeCondition("company_name", companyName, originSql);
        }
        String source = detailInformationVO.getSource();
        if (StrUtil.isNotBlank(source) && columns.contains("source")) {
            addLikeCondition("source", source, originSql);
        }
        return originSql.toString();
    }

    @Override
    public Result<List<SqlResultModel>> getSqlResult(SqlInformationVO sqlInformationVO) throws SqlException, SparkServerException {
        Result checkSqlResult = checkSql(sqlInformationVO.getSqls().getSqlQueries());
        if (checkSqlResult.isError()) {
            return checkSqlResult;
        }
        if (sqlInformationVO.getSqls().getSqlQueries().startsWith("show")) {
            SqlResultModel showSqlResult = showSqlExecute(sqlInformationVO.getSqls().getSqlQueries());
            List<SqlResultModel> list = new ArrayList<>();
            list.add(showSqlResult);
            return Result.OK(list);
        }

        String result = HttpUtils.httpPost(url + "/spark/sql", sqlInformationVO.getSqls());
        if (StrUtil.isBlankIfStr(result)) {
            throw new SqlException();
        }
        List<SqlResultModel> sqlResultModels = JSONUtil.toList(result, SqlResultModel.class);
        for (SqlResultModel sqlResultModel : sqlResultModels) {
            DataFormatConversion conversion = getDataConversion(getTables(sqlResultModel.getSqlText()), sqlResultModel);
            formatData(sqlResultModel, conversion, sqlInformationVO.getPage(), sqlInformationVO.getPageSize());
        }
        return Result.OK(sqlResultModels);
    }

    private SqlResultModel showSqlExecute(String sql) {
        List<Table> tableList = mongoTemplate.findAll(Table.class);
        List<TableModel> tableModelList = tableList.stream().map(item -> new TableModel(item.getName())).collect(Collectors.toList());
        SqlResultModel sqlResultModel = new SqlResultModel();
        sqlResultModel.setSqlText("sql");
        sqlResultModel.setTotal(tableList.size());
        sqlResultModel.setResultTable(JSONUtil.toJsonStr(tableModelList));
        return sqlResultModel;
    }

    @Override
    public Result checkSql(String sqls) {
        List<String> tableList = getTables(sqls);
        List<String> errorList = checkTableExist(tableList);
        if (errorList.size() != 0) {
            StringBuffer buffer = new StringBuffer();
            for (String errorTable : errorList) {
                buffer.append(errorTable);
                buffer.append("、");
            }
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.append(ResponseEnum.TABLE_NOT_EXIST.getMessage());
            return Result.error(ResponseEnum.TABLE_NOT_EXIST.getCode(), buffer.toString());
        }
        return Result.OK();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> upload(MultipartFile uploadFile, FileUploadVO fileUploadVO) throws SparkServerException, IOException {
        String md5Hex = DigestUtil.md5Hex(uploadFile.getInputStream());
        String filename = uploadFile.getOriginalFilename();
        String dstDeltaTablePath = fileUploadVO.getTable();;
        if (!StrUtil.isNotBlank(dstDeltaTablePath)) {
            dstDeltaTablePath = this.tablePath + filename;
            fileUploadVO.setDstDeltaTablePath(dstDeltaTablePath);
        }
        FileInfo fileInfo = this.checkFileIsExist(md5Hex, dstDeltaTablePath);
        String table = this.table + ".`" + dstDeltaTablePath + "`";
        if (fileInfo != null && ModelEnum.Append.getMode().equals(fileUploadVO.getMode())) {
            return Result.OK(table);
        }
        String suffix = filename.substring(filename.indexOf("."));
        FileInfo fileMap = new FileInfo();
        fileMap.setFileName(filename);
        fileMap.setMd5Hex(md5Hex);
        fileMap.setSuffix(suffix);
        fileMap.setFileType(suffix.substring(1));
        fileMap.setTimestamp(System.currentTimeMillis());
        String pathTemp = hdfsServer + UUID.randomUUID() + suffix;
        fileMap.setDstDeltaTablePath(dstDeltaTablePath);
        uploadTempFile(uploadFile.getInputStream(), pathTemp, uploadFile.getOriginalFilename());
        fileUploadVO.setSrcFilePath(pathTemp);
        try{
            HttpUtils.httpPost(url + "/upload", fileUploadVO);
        } catch (Exception e) {
            String mode = fileUploadVO.getMode();
            if (ModelEnum.IGNORE.getMode().equals(mode)) {
                return Result.OK();
            } else {
                throw e;
            }
        }
        this.generateDataMap(fileMap);
        deleteTempFile(pathTemp);
        if (ModelEnum.Append.getMode().equals(fileUploadVO.getMode()) && tableIsExist(dstDeltaTablePath)) {

        } else {
            if (ModelEnum.OVERWRITE.equals(fileUploadVO.getMode())) {
                this.deleteOldTable(dstDeltaTablePath);
            }
            table = generateTableMetadata(dstDeltaTablePath, fileMap.getSuffix());
        }
        return Result.OK(table);
    }

    @Override
    public Result<List<String>> getTables() {
        List<Table> table = mongoTemplate.findAll(Table.class);
        List<String> tables = table.stream().map(Table::getName).collect(Collectors.toList());
        return Result.OK(tables);
    }

    @Override
    public void download(String sql, HttpServletResponse response, DetailInformationVO detailInformationVO) throws SparkServerException, IOException, TableNotExistException, ParseException {
        if (StrUtil.isNotBlank(sql)) {
            Result checkSqlResult = checkSql(sql);
            if (checkSqlResult.isError()) {
                throw new TableNotExistException(checkSqlResult.getMessage());
            }
        }
        String tableName = detailInformationVO.getTable();
        if (StrUtil.isBlankIfStr(tableName)) {
            tableName = getTables(sql).get(0);
        }
        Table table = getTableByName(tableName);

        if (StrUtil.isBlankIfStr(sql)) {
            detailInformationVO.setTable(tableName);
            sql = createSelectSql(detailInformationVO);
        }
        InputStream inputStream = null;
        if (FileTypeEnum.JPG.getType().equals(table.getFileType()) || FileTypeEnum.PNG.getType().equals(table.getFileType())) {
            FileDownloadVO fileDownloadVO = new FileDownloadVO();
            fileDownloadVO.setDeltaTablePath(table.getPath());
            String whereClause = sql.indexOf("where") == -1 ? "" : sql.substring(sql.indexOf("where") + 6);
            fileDownloadVO.setWhereClause(whereClause);
            inputStream = HttpUtils.httpPostStream(url + "/download", fileDownloadVO);
        } else {
            SqlModel sqlModel = new SqlModel();
            sqlModel.setSqlQueries(sql);
            String result = HttpUtils.httpPost(url + "/spark/sql", sqlModel);
            List<SqlResultModel> sqlResultModels = JSONUtil.toList(result, SqlResultModel.class);
            DataFormatConversion conversion = getDataConversion(table.getFileType());
            for (SqlResultModel sqlResultModel : sqlResultModels) {
                String originFormat = conversion.formatToOrigin(sqlResultModel.getResultTable());
                inputStream = new ByteArrayInputStream(originFormat.getBytes());
            }
        }
        response.setHeader("Access-Control-Expose-Headers", "fileName");
        response.setHeader("fileName", table.getPath().substring(table.getPath().lastIndexOf("/") + 1));
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] bytes = new byte[1024];
        int length = - 1;
        try{
            while ((length = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0 , length);
            }
        } finally {
            outputStream.flush();
        }
    }

    private List<String> checkTableExist(List<String> tableList) {
        List<String> errorList = new ArrayList<>();
        for (String table : tableList) {
            Table tableInfo = mongoTemplate.findOne(Query.query(Criteria.where("name").is(table)), Table.class);
            if (tableInfo == null) {
                errorList.add(table);
            }
        }
        return errorList;
    }

    private List<String> getColumn(String sql) {

        List<String> columnList = new ArrayList<>();
        int start = sql.indexOf("select");
        int end = sql.indexOf("from");
        String[] columns = sql.substring(start + 6, end).split(",");
        for (String column : columns) {
            String[] s = column.trim().split(" ");
            if (s[0].indexOf("*") == -1) {
                columnList.add(s[0].trim());
            }
        }
        return columnList;
    }

    private List<String> getTables(String sqls) {
        List<String> tableList = new ArrayList<>();
        for (String sql : sqls.split(",")) {
            SqlProcessor sqlProcessor = getSqlProcessor(sql);
            if (sqlProcessor == null) {
                continue;
            }
            tableList.addAll(sqlProcessor.getTable(sql));
        }
        return tableList;
    }

    private void addLikeCondition(String column, String value, StringBuffer originSql) {
        originSql.append(" ");
        originSql.append("and");
        originSql.append(" ");
        originSql.append(column);
        originSql.append(" like '%");
        originSql.append(value);
        originSql.append("%");
        originSql.append("'");
    }

    private void uploadTempFile(InputStream inputStream, String hdfsPath, String fileName) throws IOException {
        Long fileLength = Long.valueOf(inputStream.available() * 8);
        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(hdfsPath), config);

        long fileSize = fileLength > 65536 ? fileLength / 65536 : 1;
        FSDataOutputStream out = fs.create(new Path(hdfsPath), new Progressable() {
            long fileCount = 0;

            public void progress() {
                log.info("文件: {} 总进度{}%", fileName, (fileCount / fileSize) * 100);
                fileCount++;
            }
        });
        IOUtils.copyBytes(inputStream, out, 2048, true);
    }

    private void deleteTempFile(String hdfsPath) throws IOException {
        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(hdfsPath), config);
        fs.delete(new Path(hdfsPath), true);
    }

    private FileInfo checkFileIsExist(String md5Hex, String dstDeltaTablePath) {
        Query query = new Query();
        query.addCriteria(Criteria.where("md5Hex").is(md5Hex).and("dstDeltaTablePath").is(dstDeltaTablePath));
        FileInfo fileMap = mongoTemplate.findOne(query, FileInfo.class);
        return fileMap;
    }

    private Table getTableByName(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is(name));
        return mongoTemplate.findOne(query, Table.class);
    }

    private void deleteOldTable(String dstDeltaTablePath) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dstDeltaTablePath").is(dstDeltaTablePath));
        mongoTemplate.remove(query, Table.class);
    }

    private boolean tableIsExist(String dstDeltaTablePath) {
        Query query = new Query();
        query.addCriteria(Criteria.where("path").is(dstDeltaTablePath));
        Table table = mongoTemplate.findOne(query, Table.class);
        return table != null;
    }

    private String generateTableMetadata(String path, String suffix) throws SparkServerException {
        Table tableMetadata = new Table();
        tableMetadata.setPath(path);
        tableMetadata.setName(this.table + ".`" + path + "`");
        tableMetadata.setFileType(suffix);

        SqlModel sqlModel = new SqlModel();
        sqlModel.setSqlQueries("select * from " + tableMetadata.getName() + "limit 1");
        String result = HttpUtils.httpPost(url + "/spark/sql", sqlModel);
        DataFormatConversion conversion = getDataConversion(suffix);

        List<SqlResultModel> sqlResultModels = JSONUtil.toList(result, SqlResultModel.class);

        List<String> columns = conversion.getColumn(sqlResultModels.get(0).getResultTable());
        tableMetadata.setColumns(columns);
        mongoTemplate.save(tableMetadata);
        return tableMetadata.getName();
    }

    private SqlResultModel formatData(SqlResultModel sqlResultModel, DataFormatConversion dataConversion, Integer page, Integer pageSize) {
        String formatResultTable = dataConversion.format(sqlResultModel.getResultTable());
        JSONArray jsonArray = JSONUtil.parseArray(formatResultTable);

        int total = jsonArray.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = (fromIndex + pageSize) > jsonArray.size() ? jsonArray.size() : (fromIndex + pageSize);
        sqlResultModel.setTotal(total);
        if (fromIndex > jsonArray.size()) {
            sqlResultModel.setResultTable("[]");
        } else {
            List<Object> pageData = jsonArray.subList(fromIndex, toIndex);
            sqlResultModel.setResultTable(pageData.toString());
        }

        return sqlResultModel;
    }

    private DataFormatConversion getDataConversion(List<String> tables, SqlResultModel sqlResultModel) {
        DataFormatConversion conversion = null;
        String fileType = getTableByName(tables.get(0)).getFileType();
        if (fileType.indexOf(FileTypeEnum.JSON.getType()) != -1) {
            conversion = new JsonDataConversion();
        } else if (fileType.indexOf(FileTypeEnum.CSV.getType()) != -1) {
            conversion = new CsvDataConversion();
        } else if (fileType.indexOf(FileTypeEnum.JPG.getType()) != -1 || fileType.indexOf(FileTypeEnum.PNG.getType()) != -1) {
            conversion = new PictureDataConversion();
        }
        sqlResultModel.setFileType(fileType);
        return conversion;
    }

    private DataFormatConversion getDataConversion(String fileType) {
        DataFormatConversion conversion = null;
        if (fileType.indexOf(FileTypeEnum.JSON.getType()) != -1) {
            conversion = new JsonDataConversion();
        } else if (fileType.indexOf(FileTypeEnum.CSV.getType()) != -1) {
            conversion = new CsvDataConversion();
        } else if (fileType.indexOf(FileTypeEnum.JPG.getType()) != -1 || fileType.indexOf(FileTypeEnum.PNG.getType()) != -1) {
            conversion = new PictureDataConversion();
        }
        return conversion;
    }

    private SqlProcessor getSqlProcessor(String sql) {
        SqlProcessor processor = null;
        sql = sql.toLowerCase();
        if (sql.indexOf(SqlTypeEnum.SELECT.getType()) != -1) {
            return new SelectSqlProcessor();
        } else if (sql.indexOf(SqlTypeEnum.UPDATE.getType()) != -1) {
            return new UpdateSqlProcessor();
        } else if (sql.indexOf(SqlTypeEnum.DELETE.getType()) != -1) {
            return new DeleteSqlProcessor();
        } else if (sql.indexOf(SqlTypeEnum.INSERT.getType()) != -1) {
            return new InsertSqlProcessor();
        }
        return processor;
    }
}
