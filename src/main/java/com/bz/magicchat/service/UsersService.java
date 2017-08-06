package com.bz.magicchat.service;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bz.magicchat.Exception.UsersException;
import com.bz.magicchat.dao.inter.UsersDaoInter;
import com.bz.magicchat.domain.Services;
import com.bz.magicchat.domain.Users;
import com.bz.magicchat.util.CommonUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class UsersService {
	@Autowired
	private UsersDaoInter usersDaoInter;
	@Autowired
	private ComboPooledDataSource dataSource;
	public Users findUserById(int userid) throws UsersException{		
		Users user = null;
		try {
			user = usersDaoInter.findUserById(userid,dataSource.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		return user;
	}
	
	public Users login(Users user)  throws UsersException{
		if(user==null){
			throw new UsersException("用户不存在，请核实用户名。");
		}
		user.setUpassword(CommonUtils.encrypForMD5(user.getUpassword()));
		Users newUser =findUserById(user.getUserid());
		if(newUser == null){
			throw new UsersException("用户不存在，请核实用户名。");
		}else if(!user.getUpassword().equals(newUser.getUpassword())){
			throw new UsersException("用户密码错误，请核实用户密码。");
		}
		return newUser;
	}
	
	public void registerUser(Users user) throws UsersException{
		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		String dateStr = ft.format(date);
		
		Users newUser =findUserById(user.getUserid());
		
		user.setRegdate(dateStr);

		if(newUser == null){
			user.setUpassword(CommonUtils.encrypForMD5(user.getUpassword()));
			try {
				usersDaoInter.registerUser(user,dataSource.getConnection());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}else{
			throw new UsersException("用已存在，请重新输入用户名。");
		}
	}
	
	public void updatePassword(int userid, String newPassword) throws UsersException {
		newPassword = CommonUtils.encrypForMD5(newPassword);
		int success;
		try {
			success = usersDaoInter.updatePassword(userid, newPassword,dataSource.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		if(success!=1) throw new UsersException("内部错误！更新密码失败！");
	}
	
	public void updateNickname(int userid, String newNickname) throws UsersException {
		int success;
		try {
			success = usersDaoInter.updateNickname(userid, newNickname,dataSource.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		if(success!=1) throw new UsersException("内部错误！更新昵称失败！");
	}
	
	public void registerService(Users affiliation_user,Users service) throws UsersException, UnsupportedEncodingException{
		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
		String dateStr = ft.format(date);
		
		service.setUpassword(CommonUtils.encrypForMD5(service.getUpassword()));
		service.setAffiliation_user(affiliation_user.getUserid());
		service.setRegdate(dateStr);
		service.setNickname(new String(service.getNickname().getBytes("ISO8859-1"),"utf-8"));
		
		Users newService =findUserById(service.getUserid());
		if(newService == null){
			try {
				usersDaoInter.registerUser(service,dataSource.getConnection());
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}else{
			throw new UsersException("用户账号已存在，请重新输入！");
		}
	}
	
	
}
