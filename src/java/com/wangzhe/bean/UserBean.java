package com.wangzhe.bean;

import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jivesoftware.openfire.user.User;

import com.wangzhe.util.StringAppend;

import sun.nio.cs.ext.ISCII91;

public class UserBean {
	public static final String UID = "id";
	public static final String USERNAME = "userName";
	public static final String NICKNAME = "nickName";
	public static final String EMAIL =  "email";
	public static final String HEADURL = "headUrl";
	public static final String SIGNATURE = "signature";
	public static final String SEX = "sex";
	public static final String LOCATION = "location";
	public static final String BIRTHDAY = "birthday";
	public static final String TYPE ="type";
	public static final String TELEPHONE = "telephone";

	private String uid;//
	private String userName;// 
    private String nickName;
    private String email;
    private String telephone;// 
    private String headUrl;// 
    private String signature;// 
    private String sex;// 
    private String location;// 
    private String birthday;// 
    private String type;// 

    public UserBean(){

    }

    public UserBean(User user){
    	uid = user.getUID();
    	userName = user.getUsername();
    	nickName = user.getName();
    	email = user.getEmail();
    	Map<String, String> properties = user.getProperties();
    	telephone = properties.get(TELEPHONE);
    	headUrl = properties.get(HEADURL);
    	signature = properties.get(SIGNATURE);
    	sex = properties.get(SEX);
    	location = properties.get(LOCATION);
    	birthday = properties.get(BIRTHDAY);
    	type = properties.get(TYPE);
    }
    
    public String toXML(){
    	StringBuilder sb = new StringBuilder();
    	StringAppend stringAppend = new StringAppend(sb);
    	
    	stringAppend.append("<userinfo xmlns=\"xmpp:custom:userinfo\" ");
    	stringAppend.append(UID, uid).append(USERNAME, userName)
    		.append(NICKNAME, nickName).append(EMAIL, email).append(TELEPHONE, telephone)
    		.append(HEADURL, headUrl).append(SIGNATURE, signature)
    		.append(SEX, sex).append(LOCATION, location).append(BIRTHDAY, birthday)
    		.append(TYPE, type);
    	stringAppend.append("/>");
    	
    	return stringAppend.toString();   
    }
    
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHeadUrl() {
		return headUrl;
	}

	public void setHeadUrl(String headUrl) {
		this.headUrl = headUrl;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getId() {
		return uid;
	}

	public void setId(String id) {
		this.uid = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	

}
