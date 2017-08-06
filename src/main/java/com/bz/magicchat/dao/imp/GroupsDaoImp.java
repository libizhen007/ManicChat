package com.bz.magicchat.dao.imp;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.bz.magicchat.dao.inter.GroupsDaoInter;
import com.bz.magicchat.domain.Groups;
import com.bz.magicchat.domain.Users;
import com.bz.magicchat.util.CommonUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class GroupsDaoImp implements GroupsDaoInter {
	@Autowired
	private QueryRunner queryRunner;

	
	@Override
	public String addFriendGroup(String groupName, Users affiliation_user,Connection connection) {
		String sql = CommonUtils.getSqlStatement("groups_sql_insert");
		String group_id = CommonUtils.getUUID();
		try {
			queryRunner.update(connection, sql,group_id ,groupName,affiliation_user.getUserid());
			
			return group_id;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int updateGroup(String group_id, String newName,Connection connection) {
		
		String sql = CommonUtils.getSqlStatement("groups_sql_update");
		try {
			return queryRunner.update(connection, sql, newName,group_id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Groups> findFriendGroupsById(int userid,Connection connection) {
		
		String sql = CommonUtils.getSqlStatement("groups_sql_select");
		try {
			return queryRunner.query(connection, sql, new BeanListHandler<Groups>(Groups.class), userid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	public int deleteFriendGroup(String group_id,Connection connection){
		
		String sql = CommonUtils.getSqlStatement("groups_sql_delete");
		try {
			return queryRunner.update(connection, sql, group_id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
