package com.example.demo.model;

import cn.hutool.json.JSONArray;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DoScalaResultModel {

    private String schemaString;

    private JSONArray rows;
}
