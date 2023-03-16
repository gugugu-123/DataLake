package com.example.demo;


import com.example.demo.common.constant.RegularExpressionConstant;
import com.example.demo.entity.Table;
import com.example.demo.exception.SparkServerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.net.URI;
import cn.hutool.json.JSONUtil;
import com.example.demo.model.SqlResultModel;
import com.example.demo.service.DataService;
import com.example.demo.util.HttpUtils;
import com.example.demo.vo.SqlInformationVO;

import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private Map<String, MongoTemplate> mongoTemplateMap;

    @Autowired
    private DataService dataService;

    @Test
    public void test() throws FileNotFoundException {

        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(Criteria.where("columns").is(new ArrayList<>()), Criteria.where("name").is("delta.`111D:/dataLake/1.png`"));
        //query.addCriteria(Criteria.where("columns").is(new ArrayList<>()));
        Table table = mongoTemplate.findOne(Query.query(criteria), Table.class);
        System.out.println(123);
    }

    @Test
    public void testSql() throws SparkServerException {

        String result = HttpUtils.httpPost("localhost:18403/spark/sql", "{\"sqlQueries\":\"select 9 + 1\"}");
        List<SqlResultModel> sqlResultModels = JSONUtil.toList(result, SqlResultModel.class);

        for (SqlResultModel sqlResultModel : sqlResultModels) {
            System.out.println(sqlResultModel);
        }

    }


    static FileSystem fs = null;



    @Test
    public void testAddFileToHdfs() throws IOException {
        // 要上传的文件所在本地路径
        Path src = new Path("D:\\test.json");
        // 要上传到hdfs的目标路径
        Path dst = new Path("/testFile");
        // 上传文件方法
        fs.copyFromLocalFile(src, dst);
        // 关闭资源
        fs.close();
    }

    @Test
    public void write2() throws Exception {
        System.out.println(Pattern.compile("").matcher(RegularExpressionConstant.TIME_1).matches());

//        File localPath = new File("D:\\test.json");
//        String hdfsPath = "hdfs://localhost:9000/user/tmp/test.json";
//        InputStream in = new BufferedInputStream(new FileInputStream(localPath));// 获取输入流对象
//        Configuration config = new Configuration();
//        FileSystem fs = FileSystem.get(URI.create(hdfsPath), config);
//        long fileSize = localPath.length() > 65536 ? localPath.length() / 65536 : 1;// 待上传文件大小
//        FSDataOutputStream out = fs.create(new Path(hdfsPath), new Progressable() {
//            //方法在每次上传了64KB字节大小的文件之后会自动调用一次
//            long fileCount = 0;
//            public void progress() {
//                System.out.println("总进度" + (fileCount / fileSize) * 100 + "%");
//                fileCount++;
//            }
//        });
//        IOUtils.copyBytes(in, out, 2048, true);//最后一个参数的意思是使用完之后是否关闭流
//        System.out.println("上传成功");
    }

}
