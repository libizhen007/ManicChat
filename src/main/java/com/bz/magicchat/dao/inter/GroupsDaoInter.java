package com.bz.magicchat.dao.inter;

import java.sql.Connection;
import java.util.List;

import com.bz.magicchat.domain.Groups;
import com.bz.magicchat.domain.Users;

public interface GroupsDaoInter {
	public List<Groups> findFriendGroupsById(int userid,Connection connection);
	
	public String addFriendGroup(String groupName,Users affiliation_user,Connection connection);
	
	public int updateGroup(String group_id,String newName,Connection connection);
	
	public int deleteFriendGroup(String group_id,Connection connection);
}
