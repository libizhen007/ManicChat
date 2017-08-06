package com.bz.magicchat.dao.imp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.bz.magicchat.Exception.UsersException;
import com.bz.magicchat.dao.inter.FriendsDaoInter;
import com.bz.magicchat.domain.Friends;
import com.bz.magicchat.util.CommonUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class FriendsDaoImp implements FriendsDaoInter{
	@Autowired
	private QueryRunner queryRunner;

	@Override
	public List<Friends> findFriendsById(int affiliation_user,Connection connection) {
		String sql = CommonUtils.getSqlStatement("friends_sql_select");
		try {
			return queryRunner.query(connection, sql, new BeanListHandler<Friends>(Friends.class), affiliation_user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}


	}
	@Override
	public int updateFriendCname(int friend_id, String friend_cname,int affiliation_user,Connection connection) {
		String sql = CommonUtils.getSqlStatement("friends_sql_update_cname");
		try {
			return queryRunner.update(connection, sql,friend_cname, friend_id,affiliation_user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int updateFriendGroup(String affiliation_group,int friend_id, int affiliation_user,Connection connection) {
		
		String sql = CommonUtils.getSqlStatement("friends_sql_update_group");
		try {
			return queryRunner.update(connection, sql,affiliation_group, friend_id,affiliation_user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public int deleteFriendById(int friend_id, int affiliation_user,Connection connection) {
		String sql = CommonUtils.getSqlStatement("friends_sql_delete");
		try {
			return queryRunner.update(connection, sql,friend_id, affiliation_user,affiliation_user,friend_id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public int addFriendByid(int friend_id, String friend_cname,String affiliation_group,int affiliation_user,int friend_type,Connection connection) {
		
		String sql = CommonUtils.getSqlStatement("friends_sql_insert");
		try {
			return queryRunner.update(connection, sql,friend_id,friend_cname,affiliation_group,affiliation_user,friend_type);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public Friends addQueryFriendByid(int friend_id, int affiliation_user,Connection connection) {
		String sql = CommonUtils.getSqlStatement("friends_sql_add_select");
		try {
			return queryRunner.query(connection, sql,new BeanHandler<Friends>(Friends.class),affiliation_user, friend_id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void updateFriendGroupList(String sql,String affiliation_group,int affiliation_user ,Connection connection){
		try {
			queryRunner.update(connection, sql,affiliation_group,affiliation_user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public int deleteService(int service_id,Connection connection) {
		String sql = CommonUtils.getSqlStatement("users_sql_delete_service");
		try {
			return queryRunner.update(connection, sql,service_id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
