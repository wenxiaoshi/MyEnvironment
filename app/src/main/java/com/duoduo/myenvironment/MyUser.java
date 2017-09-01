package com.duoduo.myenvironment;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {
	private String userName;
	private String passWord;
	private String phoneNumber;
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public void setDeviceType(String string) {
		// TODO �Զ����ɵķ������
		
	}
		
	   

}
