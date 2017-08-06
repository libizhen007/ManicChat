package com.bz.magicchat.domain;

import java.io.Serializable;

public class Services implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int service_id;
	private String spassword;
	private String nickname;
	private String regdate;
	private String affiliation_group;
	public String getAffiliation_group() {
		return affiliation_group;
	}
	public void setAffiliation_group(String affiliation_group) {
		this.affiliation_group = affiliation_group;
	}

	private int affiliation_user;//数据库中对于的是一个id,应该写生domain
	public int getService_id() {
		return service_id;
	}
	public void setService_id(int service_id) {
		this.service_id = service_id;
	}
	public String getSpassword() {
		return spassword;
	}
	public void setSpassword(String spassword) {
		this.spassword = spassword;
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
	public int getAffiliation_user() {
		return affiliation_user;
	}
	public void setAffiliation_user(int affiliation_user) {
		this.affiliation_user = affiliation_user;
	}
	@Override
	public String toString() {
		return "Services [service_id=" + service_id + ", spassword="
				+ spassword + ", nickname=" + nickname + ", regdate=" + regdate
				+ ", affiliation_group=" + affiliation_group
				+ ", affiliation_user=" + affiliation_user + "]";
	}
	
	
}
