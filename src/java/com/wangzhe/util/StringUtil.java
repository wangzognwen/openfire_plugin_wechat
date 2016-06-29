package com.wangzhe.util;

public class StringUtil {
	
	public static boolean isEmpty(CharSequence charSequence){
		if(charSequence == null || "".equals(charSequence)){
			return true;
		}
		return false;
	}
}
