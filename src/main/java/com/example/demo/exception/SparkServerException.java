package com.example.demo.exception;

public class SparkServerException extends Exception{

    //异常信息
    private String message;

    //构造函数
    public SparkServerException(){

    }

    public SparkServerException(String message){
        super(message);
    }

}
