package com.example.demo.common.commonEnum;

public enum ResponseEnum {

	SUCCESS(200,"成功"),
	SQL_HAS_ERROR(100001,"sql异常，请检查sql"),
	SPARK_SERVER_ERROR(100002,"远程调用spark服务异常"),
	TABLE_NOT_EXIST(100003, "表不存在");
	;
	private Integer code;
	private String message;


	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	ResponseEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

}
