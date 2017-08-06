package com.bz.magicchat.service;


import java.sql.SQLException;
import java.util.List;











import org.omg.CORBA.UserException;
import org.springframework.beans.factory.annotation.Autowired;


import com.bz.magicchat.Exception.UsersException;
import com.bz.magicchat.dao.inter.FriendsDaoInter;
import com.bz.magicchat.domain.Friends;
import com.bz.magicchat.domain.Users;
import com.mchange.v2.c3p0.ComboPooledDataSource;


public class FriendsService {
	@Autowired
	private FriendsDaoInter friendsDaoInter;
	@Autowired
	private ComboPooledDataSource dataSource;
	public List<Friends> findFriendsById(Users user) {
		List<Friends> friends;
		try {
			friends = friendsDaoInter.findFriendsById(user.getUserid(),dataSource.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return friends;
	}
	

	
	public void updateFriendCname(int friend_id, String friend_cname,int affiliation_user) throws UsersException {
		int success;
		try {
			success = friendsDaoInter.updateFriendCname(friend_id, friend_cname, affiliation_user,dataSource.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		if(success !=1){
			throw new UsersException("更改备注出现错误！");
		}
	}
	public void updateFriendGroup(String affiliation_group,int friend_id, int affiliation_user) throws UsersException {
		int success;
		try {
			success = friendsDaoInter.updateFriendGroup(affiliation_group, friend_id, affiliation_user,dataSource.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		if(success !=1){
			throw new UsersException("转移分组出现错误！");
		}
	}
	
	public void deleteFriendById(int friend_id, int affiliation_user) throws UsersException {
		int success;
		try {
			success = friendsDaoInter.deleteFriendById(friend_id, affiliation_user,dataSource.getConnection());
			if(success !=2){
				throw new UsersException("删除好友出现错误！");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	public void addFriendByid(int friend_id, String friend_cname,String affiliation_group,int affiliation_user,int friend_type) throws UsersException {
		Friends friend;
		try {
			friend = friendsDaoInter.addQueryFriendByid(friend_id, affiliation_user,dataSource.getConnection());
			if(friend==null){
				int success = friendsDaoInter.addFriendByid(friend_id, friend_cname, affiliation_group, affiliation_user,friend_type,dataSource.getConnection());
				if(success !=1){
					throw new UsersException("添加好友出现错误！");
				}
			}else{
				throw new UsersException("好友已经存在！");
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	//
	/**
	 * 
	 * @param friendsId 多个好有id
	 * @param affiliation_group 新分组id
	 * @param affiliation_user 用户id
	 */
	public void updateFriendGroupList(String friendsId,String affiliation_group,int affiliation_user){
		String sql = "UPDATE friends SET affiliation_group=? WHERE friend_id IN (";
		String[] ids = friendsId.split(",");
		for(int i=0; i<ids.length; i++){
			if(i != ids.length-1){
				sql = sql + ids[i] + ",";
			}else{
				sql = sql + ids[i] + ") AND affiliation_user=?";
			}
		}
		try {
			friendsDaoInter.updateFriendGroupList(sql, affiliation_group, affiliation_user,dataSource.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}



	public void deleteService(int service_id) throws Exception {
		int success;
		try {
			success = friendsDaoInter.deleteService(service_id,dataSource.getConnection());
			if(success!=1)throw new UsersException("删除客服出现错误！");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		
		
	}
}
