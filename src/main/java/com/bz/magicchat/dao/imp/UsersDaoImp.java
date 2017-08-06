package com.bz.magicchat.dao.imp;

import java.sql.Connection;
import java.sql.SQLException;



import org.apache.commons.dbutils.QueryRunner;


import org.apache.commons.dbutils.handlers.BeanHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.bz.magicchat.dao.inter.UsersDaoInter;
import com.bz.magicchat.domain.Users;
import com.bz.magicchat.util.CommonUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class UsersDaoImp implements UsersDaoInter {
	@Autowired
	private QueryRunner queryRunner;
	
	@Override
	public Users findUserById(int userid,Connection connection) {
		Users user = null;
		String sql = CommonUtils.getSqlStatement("users_sql_select");
		try {
			user = queryRunner.query(connection, sql, new BeanHandler<Users>(Users.class), userid);
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		return user;
	}
	@Override
	public void registerUser(Users user,Connection connection) {
		String sql = CommonUtils.getSqlStatement("users_sql_insert");
		
		try {
			queryRunner.update(connection, sql, user.getUserid(),user.getUpassword(),user.getNickname(),user.getRegdate(),user.getUsertype(),user.getAffiliation_user());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public int updatePassword(int userid, String newPassword,Connection connection) {
		String sql = CommonUtils.getSqlStatement("users_sql_update_upassword");
		try {
			return queryRunner.update(connection, sql, newPassword,userid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public int updateNickname(int userid, String newNickname,Connection connection) {
		String sql = CommonUtils.getSqlStatement("users_sql_update_nickname");
		try {
			return queryRunner.update(connection, sql, newNickname,userid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
