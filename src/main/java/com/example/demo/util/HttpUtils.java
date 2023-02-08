package com.example.demo.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.demo.exception.SparkServerException;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXParseException;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class HttpUtils {

    public static String httpGet(String url, Map<String, Object> params) {

        HttpResponse response = HttpRequest.get(url)
                .form(params)
                .timeout(20000)
                .execute();

        String body = response.body();

        return "";
    }


    public static String httpPost(String url, Object body) throws SparkServerException {
        HttpResponse response = HttpRequest.post(url)
                .body(JSONUtil.toJsonStr(body))
                .timeout(20000)
                .execute();
        if (!response.isOk()) {
            JSONObject errorJson = JSONUtil.parseObj(response.body());
            log.info((String) errorJson.get("reason"));
            throw new SparkServerException((String) errorJson.get("reason"));
        }
        return response.isOk() ? response.body() : "";
    }

    public static InputStream httpPostStream(String url, Object body) throws SparkServerException {
        HttpResponse response = HttpRequest.post(url)
                .body(JSONUtil.toJsonStr(body))
                .timeout(20000)
                .execute();
        if (!response.isOk()) {

//            JSONObject errorJson = JSONUtil.parseObj(response.body().replace("\\", ""));
//            log.info((String) errorJson.get("reason"));
//            System.out.println(errorJson.toString());
            throw new SparkServerException();
        }
        return response.isOk() ? response.bodyStream() : null;
    }

}
