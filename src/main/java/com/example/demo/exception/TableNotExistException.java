package com.example.demo.exception;

public class TableNotExistException extends Exception{

    //异常信息
    private String message;

    //构造函数
    public TableNotExistException(){

    }

    public TableNotExistException(String message){
        super(message);
    }

}
