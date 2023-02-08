package com.example.demo.common.commonEnum;

import lombok.Data;

public enum ModelEnum {

    Append("append"),
    OVERWRITE("overwrite"),
    ERROR("error"),
    ERROR_IF_EXISTS("errorifexists"),
    IGNORE("ignore");


    private String mode;


    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    ModelEnum(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return mode;
    }
}
