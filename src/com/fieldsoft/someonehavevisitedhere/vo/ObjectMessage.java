package com.fieldsoft.someonehavevisitedhere.vo;

public class ObjectMessage {
	Object object;
	boolean result;
	String messageString;
	
	public ObjectMessage(Object obj,boolean result,String message) {
		this.object = obj;
		this.result = result;
		this.messageString = message;
	}
	
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getMessageString() {
		return messageString;
	}
	public void setMessageString(String messageString) {
		this.messageString = messageString;
	}

	
}
