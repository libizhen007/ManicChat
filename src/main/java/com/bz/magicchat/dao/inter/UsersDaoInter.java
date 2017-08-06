package com.bz.magicchat.dao.inter;

import java.sql.Connection;

import com.bz.magicchat.domain.Users;

public interface UsersDaoInter {
	public Users findUserById(int userid,Connection connection);
	
	public void registerUser(Users user,Connection connection);
	
	public int updatePassword(int userid,String newPassword,Connection connection);
	
	public int updateNickname(int userid,String newNickname,Connection connection);
}
