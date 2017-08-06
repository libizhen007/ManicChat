package com.bz.magicchat.service;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.bz.magicchat.Exception.UsersException;
import com.bz.magicchat.dao.inter.GroupsDaoInter;
import com.bz.magicchat.domain.Groups;
import com.bz.magicchat.domain.Users;
import com.bz.magicchat.util.CommonUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;


public class GroupsService {
	@Autowired
	private GroupsDaoInter groupsDaoInter;
	@Autowired
	private ComboPooledDataSource dataSource;
	public String addFriendGroup(String groupName, Users affiliation_user) throws UsersException {
		String group_id = null;
		try{
			group_id = groupsDaoInter.addFriendGroup(groupName, affiliation_user,dataSource.getConnection());
			if(group_id.length() <32){
				throw new UsersException("分组添加错误");
			}
			return group_id;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public List<Groups> findFriendGroupsById(Users affiliation_user) throws UsersException {
		
		List<Groups> groups = null;
		try {
			groups = groupsDaoInter.findFriendGroupsById(affiliation_user.getUserid(),dataSource.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		if(groups == null) throw new UsersException("获取分组出现错误");
		return groups;
	}

	public void deleteFriendGroup(String group_id) throws UsersException {
		int success = 0;
		try {
			success = groupsDaoInter.deleteFriendGroup(group_id,dataSource.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		if(success!=1){
			throw new UsersException("分组删除错误");
		}
	}

	public void updateGroup(String group_id, String newGroupName) throws UsersException {
		int success;
		try {
			success = groupsDaoInter.updateGroup(group_id,newGroupName,dataSource.getConnection());
			if(success!=1){
				throw new UsersException("分组删除错误");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
}
