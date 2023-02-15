package com.example.demo.common.commonEnum;

public enum SqlTypeEnum {

    SELECT("select"),
    UPDATE("update"),
    DELETE("delete"),
    INSERT("insert");
    String type;

    SqlTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
