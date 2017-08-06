package com.bz.magicchat.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bz.magicchat.domain.Users;
import com.bz.magicchat.service.GroupsService;

@Controller
public class GroupsHandler {
	
	@Autowired
	private GroupsService groupsService;
	
	@RequestMapping("/addFriendGroup.do")
	public void addFriendGroup(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		String newGroupName = null;
		Users user = (Users) request.getSession().getAttribute("user");
		response.setCharacterEncoding("utf-8");
		try {
			newGroupName = new String(request.getParameter("newGroupName").getBytes("ISO8859-1"),"utf-8");
			String group_id = groupsService.addFriendGroup(newGroupName, user);
			session.setAttribute("groups", groupsService.findFriendGroupsById(user));
			response.getWriter().write(group_id);
		} catch (Exception e) {
			try {
				response.getWriter().write(e.getMessage());
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
		
	}
	
	@RequestMapping("/deleteFriendGroup.do")
	public void deleteFriendGroup(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		String group_id = null;
		Users user = (Users) request.getSession().getAttribute("user");
		response.setCharacterEncoding("utf-8");
		try {
			group_id = request.getParameter("group_id");
			groupsService.deleteFriendGroup(group_id);
			session.setAttribute("groups", groupsService.findFriendGroupsById(user));
			response.getWriter().write("删除分组成功!");
		} catch (Exception e) {
			try {
				response.getWriter().write(e.getMessage());
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
		
	}
	
	
	@RequestMapping("/updateGroup.do")
	public void updateGroup(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session){

		Users user = (Users) request.getSession().getAttribute("user");
		response.setCharacterEncoding("utf-8");
		try {
			String group_id = request.getParameter("group_id");
			String newGroupName = new String(request.getParameter("newGroupName").getBytes("ISO8859-1"),"utf-8");
			groupsService.updateGroup(group_id,newGroupName);
			session.setAttribute("groups", groupsService.findFriendGroupsById(user));
			response.getWriter().write("更新分组成功!");
			
		} catch (Exception e) {
			try {
				response.getWriter().write(e.getMessage());
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
		
	}
}
