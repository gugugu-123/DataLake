package com.example.demo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;


public class FileSystemUpload {
    public static void main(String[] args) throws IOException {

        File localPath = new File("D:\\test.json");
        String hdfsPath = "hdfs://114.132.185.184:9000/user/tmp/hello.txt";
        InputStream in = new BufferedInputStream(new FileInputStream(localPath));// 获取输入流对象
        Configuration config = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(hdfsPath), config);
        long fileSize = localPath.length() > 65536 ? localPath.length() / 65536 : 1;// 待上传文件大小
        FSDataOutputStream out = fs.create(new Path(hdfsPath), new Progressable() {
            //方法在每次上传了64KB字节大小的文件之后会自动调用一次
            long fileCount = 0;

            public void progress() {
                System.out.println("总进度" + (fileCount / fileSize) * 100 + "%");
                fileCount++;
            }
        });
        IOUtils.copyBytes(in, out, 2048, true);//最后一个参数的意思是使用完之后是否关闭流
        System.out.println("上传成功");
    }
}