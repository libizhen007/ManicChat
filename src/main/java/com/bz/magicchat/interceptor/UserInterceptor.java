package com.bz.magicchat.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.bz.magicchat.domain.Users;

public class UserInterceptor implements HandlerInterceptor{

	/**
	 * handle之前
	 */
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		Users user = (Users) request.getSession().getAttribute("user");
	//	System.out.println("请求的地址:\t"+request.getRequestURI()+"\t当前登陆的用户:\t" + user);
		if(user==null){
			request.getSession().setAttribute("userErrorMsg","请先登录后再进行此操作");
			response.sendRedirect(request.getContextPath()+"/index.do");
			return false;
		}else{
			return true;
		}
		
		
		
		
		
	}

	/**
	 * 调用Handle之后返回modelView之前
	 */
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 执行handler完成之后调用
	 */
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
