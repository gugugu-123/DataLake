package com.example.demo.entity;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@ToString
@Document("table")
public class Table {

    @Id
    private String id;

    private String name;

    private String path;

    private String fileType;

    private List<String> columns;
}
