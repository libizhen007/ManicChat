package com.bz.magicchat.handler;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bz.magicchat.domain.Friends;
import com.bz.magicchat.domain.Users;
import com.bz.magicchat.service.FriendsService;
import com.bz.magicchat.service.GroupsService;
import com.bz.magicchat.service.UsersService;

@Controller
public class FriendsHandler {

	@Autowired
	private FriendsService friendsService;
	@Autowired
	private UsersService usersService;
	@Autowired
	private GroupsService groupsService;
	
	@RequestMapping("/updateFriendCname.do")
	public void updateFriendCname(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session){

		response.setCharacterEncoding("utf-8");
		Users user = (Users) request.getSession().getAttribute("user");
		String friend_cname = null;
		int friend_id = 0;
		try {
				friend_cname = new String(request.getParameter("friend_cname").getBytes("ISO8859-1"),"utf-8");
				friend_id = Integer.parseInt(request.getParameter("friend_id"));
				friendsService.updateFriendCname(friend_id, friend_cname, user.getUserid());
				response.getWriter().write("昵称更新成功！");
				request.getSession().setAttribute("friends", friendsService.findFriendsById(user));
		} catch (Exception e) {
			try {
				response.getWriter().write(e.getMessage());
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
	}
	@RequestMapping("/updateFriendGroup.do")
	public void updateFriendGroup(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		response.setCharacterEncoding("utf-8");
		Users user = (Users) request.getSession().getAttribute("user");
		String affiliation_group = null;
		int friend_id = 0;
		try {
			affiliation_group = request.getParameter("affiliation_group");
			friend_id = Integer.parseInt(request.getParameter("friend_id"));
			friendsService.updateFriendGroup(affiliation_group, friend_id, user.getUserid());
			session.setAttribute("friends", friendsService.findFriendsById(user));
			response.getWriter().write("好友移动成功！");
		} catch (Exception e) {
			try {
				response.getWriter().write(e.getMessage());
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
	}
	@RequestMapping("/findFriendById.do")
	public void findFriendById(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		try {
			response.setCharacterEncoding("utf-8");
			Users friend = usersService.findUserById(Integer.parseInt(request.getParameter("friend_id")));
			response.getWriter().write(friend.getNickname());
			session.setAttribute("temp-add-friend", friend);
		} catch (Exception e) {
			try {
				response.getWriter().write("error:" + e.getMessage());
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
	}
	@RequestMapping("/deleteFriend.do")
	public void deleteFriend(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		response.setCharacterEncoding("utf-8");
		Users user = (Users) request.getSession().getAttribute("user");

		int friend_id = 0;
		try {
			friend_id = Integer.parseInt(request.getParameter("friend_id"));
			friendsService.deleteFriendById(friend_id, user.getUserid());
			session.setAttribute("friends", friendsService.findFriendsById(user));
			response.getWriter().write("好友删除成功！");
			
		} catch (Exception e) {
			try {
				response.getWriter().write(e.getMessage());
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
	}
	
	@RequestMapping("/addFriend.do")
	public void addFriend(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session,int friend_type){
		response.setCharacterEncoding("utf-8");
		Users user = (Users) request.getSession().getAttribute("user");
		String affiliation_group = request.getParameter("affiliation_group");
		Users friend = (Users) session.getAttribute("temp-add-friend");
		try {
			friendsService.addFriendByid(friend.getUserid(), friend.getNickname(), affiliation_group, user.getUserid(),friend_type);
			List<Friends> friends = friendsService.findFriendsById(user);
			session.setAttribute("friends", friends);
			session.removeAttribute("temp-add-friend");
			//开始反向添加好友，这里不做详细处理，默认添加到第一个分组。不做通知
			String group_id = groupsService.findFriendGroupsById(friend).get(0).getGroup_id();
			friendsService.addFriendByid(user.getUserid(), user.getNickname(), group_id, friend.getUserid(), friend_type);
			response.getWriter().write("添加好友成功！");
		} catch (Exception e) {
			try {
				response.getWriter().write("error:" + e.getMessage());
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
	}

	@RequestMapping("/updateFriendGroupList.do")
	public void updateFriendGroupList(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		String friendsId = request.getParameter("friendsId");
		String affiliation_group = request.getParameter("affiliation_group");
		Users user = (Users) request.getSession().getAttribute("user");
		try {
			friendsService.updateFriendGroupList(friendsId, affiliation_group, user.getUserid());
			response.getWriter().write("success");
			List<Friends> friends = friendsService.findFriendsById(user);
			request.getSession().setAttribute("friends", friends);
		} catch (Exception e) {
			try {
				response.getWriter().write("执行删除错误");
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
	}
	
	@RequestMapping("/updateFriendList.do")
	public void updateFriendList(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		Users user = (Users) request.getSession().getAttribute("user");
		try {
			List<Friends> friends = friendsService.findFriendsById(user);
			request.getSession().setAttribute("friends", friends);
		} catch (Exception e) {
			try {
				response.getWriter().write("执行更新错误");
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
	}
	
	
	@RequestMapping("/deleteService.do")
	public void deleteService(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session){
		response.setCharacterEncoding("utf-8");
		Users user = (Users) request.getSession().getAttribute("user");

		int service_id = 0;
		try {
		
			service_id = Integer.parseInt(request.getParameter("friend_id"));
			friendsService.deleteService(service_id);
			session.setAttribute("friends", friendsService.findFriendsById(user));
			response.getWriter().write("客服删除成功！");
			
		} catch (Exception e) {
			try {
				response.getWriter().write(e.getMessage());
			} catch (Exception e2) {
				throw new RuntimeException(e2.getMessage());
			}
		}
	}
}
