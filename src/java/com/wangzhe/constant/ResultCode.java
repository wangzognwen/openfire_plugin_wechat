package com.wangzhe.constant;

public enum ResultCode {
	SUCCESS(0), USER_NOT_FOUND(1001), TO_JID_WRONG(1002);
	
	private int value;
	
	private ResultCode(int value) {
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	@Override
	public String toString() {
		switch(value){
			case 0:
				return "success";
			case 1001:
				return "user not found";
			case 1002:
				return "to jid attribute is wrong";
			default:
				return super.toString();
		}
	}
}
