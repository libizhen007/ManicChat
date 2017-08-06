package com.bz.magicchat.dao.inter;

import java.sql.Connection;
import java.util.List;

import com.bz.magicchat.domain.Friends;


public interface FriendsDaoInter {
	public List<Friends> findFriendsById(int affiliation_user,Connection connection);
	
	public int addFriendByid(int friend_id, String friend_cname,String affiliation_group,int affiliation_user,int friend_type,Connection connection);
	
	public Friends addQueryFriendByid(int friend_id,int affiliation_user,Connection connection);
	
	public int updateFriendCname(int friend_id,String friend_cname,int affiliation_user,Connection connection);
	
	public int deleteFriendById(int friend_id,int affiliation_user,Connection connection);
	
	public int updateFriendGroup(String affiliation_group,int friend_id,int affiliation_user,Connection connection);

	public void updateFriendGroupList(String sql,String affiliation_group,int affiliation_user,Connection connection);

	public int deleteService(int service_id,Connection connection);

	
}
