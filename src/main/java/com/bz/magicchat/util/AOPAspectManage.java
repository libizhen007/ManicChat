package com.bz.magicchat.util;

import java.sql.Connection;
import java.sql.SQLException;

public class AOPAspectManage {
	
	public void beforeAdvice() { 
        

        System.out.println("===========1前置通知测试==========="); 
	}
	
	public void closeConnection(Connection connection){
//		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

		if(connection!=null){
			
			try {
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
//		System.out.println("连接：" + connection);
	}
}
