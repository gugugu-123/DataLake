package com.example.demo.common.commonEnum;

public enum FileTypeEnum {

    JSON(".json"),
    CSV(".csv"),
    JPG(".jpg"),
    PNG(".PNG");
    String type;

    FileTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
