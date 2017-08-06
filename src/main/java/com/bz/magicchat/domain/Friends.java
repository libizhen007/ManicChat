package com.bz.magicchat.domain;

import java.io.Serializable;

public class Friends implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int friend_id;
	private String friend_cname;
	private String affiliation_group;
	private String affiliation_user;//这个应该做成一个具体的对象，这里到后面再想想需不需要做
	private int friend_type;
	public int getFriend_type() {
		return friend_type;
	}
	public void setFriend_type(int friend_type) {
		this.friend_type = friend_type;
	}
	public String getAffiliation_user() {
		return affiliation_user;
	}
	public void setAffiliation_user(String affiliation_user) {
		this.affiliation_user = affiliation_user;
	}
	public int getFriend_id() {
		return friend_id;
	}
	public void setFriend_id(int friend_id) {
		this.friend_id = friend_id;
	}
	public String getFriend_cname() {
		return friend_cname;
	}
	public void setFriend_cname(String friend_cname) {
		this.friend_cname = friend_cname;
	}
	public String getAffiliation_group() {
		return affiliation_group;
	}
	public void setAffiliation_group(String affiliation_group) {
		this.affiliation_group = affiliation_group;
	}
	@Override
	public String toString() {
		return "Friends [friend_id=" + friend_id + ", friend_cname="
				+ friend_cname + ", affiliation_group=" + affiliation_group
				+ ", affiliation_user=" + affiliation_user + ", friend_type="
				+ friend_type + "]";
	}
	
	
}
