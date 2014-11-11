package com.zale.exception;

public class RequestDataException extends Exception{

	public RequestDataException()
	{
		super();
	}
	public RequestDataException(String msg)
	{
		super(msg);
	}
	
	public String toString()
	{
		return "服务器回复数据不正确，解析错误";
	}
}
