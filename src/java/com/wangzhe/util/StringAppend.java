package com.wangzhe.util;


public class StringAppend {
	private StringBuilder sBuilder;
	
	public StringAppend(StringBuilder sb){
		sBuilder = sb;
	}
	
	public StringAppend append(String value){
		sBuilder.append(value);
		return this;
	}
	
	public StringAppend append(String key, int value){
		sBuilder.append(key + " = " + "\"" + value + "\" ");
		return this;
	}
	
	public StringAppend append(String key, double value){
		sBuilder.append(key + " = " + "\"" + value + "\" ");
		return this;
	}
	
	public StringAppend append(String key, String value){
		if(value == null){
			value = "";
		}
		sBuilder.append(key + " = " + "\"" + value + "\" ");
		return this;
	}
	
	public StringAppend append(String key, Object value){
		if(value == null){
			value = "";
		}
		sBuilder.append(key + " = " + "\"" + value.toString() + "\" ");
		return this;
	}
	
	@Override
	public String toString() {
		if(sBuilder != null){
			return sBuilder.toString();
		}
		return super.toString();
	}

}
