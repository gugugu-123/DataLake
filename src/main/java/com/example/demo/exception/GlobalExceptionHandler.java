package com.example.demo.exception;

import com.example.demo.common.commonEnum.ResponseEnum;
import com.example.demo.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 异常处理器
 * 
 * @Author scott
 * @Date 2019
 */
@Slf4j
//@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(SqlException.class)
	public Result<?> sqlException(SqlException e) {
		return Result.error(ResponseEnum.SQL_HAS_ERROR.getCode(), e.getMessage());
	}

	@ExceptionHandler(TableNotExistException.class)
	public Result<?> tableNotExistException(TableNotExistException e) {
		return Result.error(ResponseEnum.TABLE_NOT_EXIST.getCode(), e.getMessage());
	}

	@ExceptionHandler(SparkServerException.class)
	public Result<?> SparkServerException(SparkServerException e) {
		e.printStackTrace();
		return Result.error(ResponseEnum.SPARK_SERVER_ERROR.getCode(), e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error(e.getMessage(), e);
		List<ObjectError> errors = e.getBindingResult().getAllErrors();
		String errorMessage = "";
		if (!errors.isEmpty()) {
			errorMessage = errors.get(0).getDefaultMessage();
		}
		return Result.error(errorMessage);
	}

	@ExceptionHandler(BindException.class)
	public Result<?> handleBindException(BindException e) {
		log.error(e.getMessage(), e);
		return Result.error(e.getBindingResult().getFieldError().getDefaultMessage());
	}

	@ExceptionHandler(Exception.class)
	public Result<?> handleException(Exception e){
		log.error(e.getMessage(), e);
		return Result.error("操作失败，"+e.getMessage());
	}


}
