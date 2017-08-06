package com.bz.magicchat.interceptor;
import java.util.Map;






import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.bz.magicchat.domain.Users;

public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor{  
	/**
	 * 连接之前执行
	 */
    @Override  
    public boolean beforeHandshake(ServerHttpRequest request,  
            ServerHttpResponse response, WebSocketHandler wsHandler,  
            Map<String, Object> attributes) throws Exception {  
    	if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                //使用userName区分WebSocketHandler，以便定向发送消息
               Users user =  (Users) session.getAttribute("user");
               if(user != null){
            	   attributes.put("user", user);
               }else{
            	   attributes.put("clientId", session.getAttribute("clientId"));
               }
               
            }else{
            	System.out.println("error");
            }
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);  
    }  
    /**
     * 连接之后执行
     */
    @Override  
    public void afterHandshake(ServerHttpRequest request,  
            ServerHttpResponse response, WebSocketHandler wsHandler,  
            Exception ex) {  
    	super.afterHandshake(request, response, wsHandler, ex);  
    }  
  
} 