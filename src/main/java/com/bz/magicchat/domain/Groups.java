package com.bz.magicchat.domain;

import java.io.Serializable;

public class Groups implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String group_id;
	private String group_name;
	private int affiliation_user;//这里有个问题：这个应该是要需要将int改为users对象，不过在这个项目中用不到所以先这样
	public int getAffiliation_user() {
		return affiliation_user;
	}
	public void setAffiliation_user(int affiliation_user) {
		this.affiliation_user = affiliation_user;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	
	@Override
	public String toString() {
		return "Group [group_id=" + group_id + ", group_name=" + group_name
				+ ", affiliation_user=" + affiliation_user + "]";
	}
	
}
