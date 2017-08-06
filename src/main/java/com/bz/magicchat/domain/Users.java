package com.bz.magicchat.domain;

import java.io.Serializable;

public class Users implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int userid;
	private String upassword;
	private String nickname;
	private String regdate;
	private int usertype;
	private int affiliation_user;
	public int getAffiliation_user() {
		return affiliation_user;
	}
	public void setAffiliation_user(int affiliation_user) {
		this.affiliation_user = affiliation_user;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public String getUpassword() {
		return upassword;
	}
	public void setUpassword(String upassword) {
		this.upassword = upassword;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getRegdate() {
		return regdate;
	}
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}
	public int getUsertype() {
		return usertype;
	}
	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}
	@Override
	public String toString() {
		return "Users [userid=" + userid + ", upassword=" + upassword
				+ ", nickname=" + nickname + ", regdate=" + regdate
				+ ", usertype=" + usertype + ", affiliation_user="
				+ affiliation_user + "]";
	}
	
	
}
