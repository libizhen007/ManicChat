package com.bz.magicchat.dao.imp;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.bz.magicchat.Exception.UsersException;
import com.bz.magicchat.dao.inter.ServicesDaoInter;
import com.bz.magicchat.domain.Services;
import com.bz.magicchat.domain.Users;
import com.bz.magicchat.util.CommonUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ServicesDaoImp  implements ServicesDaoInter{
	@Autowired
	private QueryRunner queryRunner;
	@Autowired
	private ComboPooledDataSource dataSource;
	@Override
	public int registerService(Services service) {
		String sql_service = CommonUtils.getSqlStatement("service_sql_insert");
		String sql_friend = CommonUtils.getSqlStatement("friends_sql_insert");
		Connection connnection = null;
		int success_service = 0;
		int success_friend = 0;
		try {
			 connnection = dataSource.getConnection();
			 connnection.setAutoCommit(false);
			 success_service = queryRunner.update(connnection, sql_service,service.getService_id(),service.getSpassword(),service.getNickname(),service.getAffiliation_group(),service.getRegdate(),service.getAffiliation_user() );
			 success_friend = queryRunner.update(connnection, sql_friend, service.getService_id(),service.getNickname(),service.getAffiliation_group(),service.getAffiliation_user());
			 
			 if(success_friend==1 && success_service==1){
				 connnection.commit();
			 }else{
				 connnection.rollback();
				 throw new RuntimeException("出现内部错误");
			 }
		} catch (SQLException e) {
			
			throw new RuntimeException(e);
		}
		
		return 0;
	}

	@Override
	public Services findSerivceById(int service_id) {
		Services service = null;
		String sql = CommonUtils.getSqlStatement("service_sql_select");
		try {
			service = queryRunner.query(dataSource.getConnection(), sql, new BeanHandler<Services>(Services.class), service_id);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return service;
	}

}
