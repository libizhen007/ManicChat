package com.bz.magicchat.handler;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bz.magicchat.Exception.UsersException;
import com.bz.magicchat.domain.Friends;
import com.bz.magicchat.domain.Groups;
import com.bz.magicchat.domain.Users;
import com.bz.magicchat.service.FriendsService;
import com.bz.magicchat.service.GroupsService;
import com.bz.magicchat.service.UsersService;
import com.bz.magicchat.util.CommonUtils;
import com.bz.magicchat.util.VerifyCode;

@Controller
public class UsersHandler{
	
	@Autowired
	private UsersService usersService;
	@Autowired
	private GroupsService groupsService;
	@Autowired
	private FriendsService friendsService;
	@RequestMapping("/index.do")
	public String index(Model model,HttpServletRequest request,HttpServletResponse response){
		/*if(request.getServerName().indexOf("www.")==-1){
			return "redirect:http://www.ageuu.top/ManicChat";
		}*/
		return "/jsp/index.jsp";
	}
	
	@RequestMapping("/login.do")
	public String login(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session,Users user){
		Users users = null;
		List<Groups> groups = null;
		List<Friends> friends = null;
		try {
			users = usersService.login(user);
			groups = groupsService.findFriendGroupsById(users);
			friends = friendsService.findFriendsById(users);
		} catch (UsersException e) {
			session.setAttribute("userErrorMsg", e.getMessage());
			session.setAttribute("echoUserid",user.getUserid());
			return "redirect:/index.do";
		}
		session.setAttribute("user", users);
		session.setAttribute("groups", groups);
		session.setAttribute("friends", friends);
		session.removeAttribute("userErrorMsg");
		Cookie wsUserid =new Cookie("wsUserid", users.getUserid()+"");
		Cookie bossId =new Cookie("bossId", users.getAffiliation_user()+"");
		response.addCookie(wsUserid);
		response.addCookie(bossId);
		String savePath = session.getServletContext().getRealPath("uploadFile");
		savePath = savePath.replace("\\", "/");

		session.setAttribute("savePath", savePath);
		
		return "redirect:/main.do";
	}
	@RequestMapping("/main.do")
	public String main(Model model){
		return "/jsp/main.jsp";
	}
	
	@RequestMapping("/logout.do")
	public String logout(Model model,HttpServletRequest request){
		request.getSession().invalidate();
		return "redirect:/index.do";
	}
	
	@RequestMapping("/registerUser.do")
	public void registerUser(Model model,HttpServletRequest request,Users user,HttpServletResponse response){
		response.setCharacterEncoding("utf-8");
		try {
			if(!request.getParameter("vcode").equalsIgnoreCase((String) request.getSession().getAttribute("vcode"))){
				response.getWriter().write("验证码错误");
			}else{
				usersService.registerUser(user);
				groupsService.addFriendGroup("我的好友", user);
				response.getWriter().write("注册成功！");
				request.getSession().removeAttribute("vcode");
			}
		} catch (Exception e) {
			try {
				response.getWriter().write(e.getMessage());
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
		}

	}
	@RequestMapping("/getVCode.do")
	public void getVCode(HttpServletRequest request,HttpServletResponse response){
		BufferedImage img = VerifyCode.getImage();
		String vcode = VerifyCode.getText();
		try {
			request.getSession().setAttribute("vcode", vcode);
			VerifyCode.output(img, response.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException("验证码获取错误");
		}
	}
	
	@RequestMapping("/updateNickname.do")
	public void updateNickname(Model model,HttpServletRequest request,HttpServletResponse response) throws IOException{
		response.setCharacterEncoding("utf-8");
		Users user = (Users) request.getSession().getAttribute("user");
		String nickname = new String(request.getParameter("newNickname").getBytes("ISO8859-1"),"utf-8");
		try {

			usersService.updateNickname(user.getUserid(), nickname);
			user.setNickname(nickname);
			request.getSession().setAttribute("user",user);
			response.getWriter().write("更新昵称成功！");
		} catch (Exception e) {
			response.getWriter().write(e.getMessage());
		}
	}
	
	@RequestMapping("/updatePassword.do")
	public void updatePassword(Model model,HttpServletRequest request,HttpServletResponse response) throws IOException{
		Users user = (Users) request.getSession().getAttribute("user");
		response.setCharacterEncoding("utf-8");
		try {
			usersService.updatePassword(user.getUserid(), request.getParameter("newPassword"));
			response.getWriter().write("更新密码成功,即将退出本次登录！");
		} catch (Exception e) {
			response.getWriter().write(e.getMessage());
		}
		
	}
	
	
	@RequestMapping("/registerSerivce.do")
	public void registerSerivce(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session,Users service,String affiliation_group,int friend_type){
		try {
			response.setCharacterEncoding("utf-8");
			Users user = (Users) session.getAttribute("user");
			usersService.registerService(user, service);
			//以下两个动作可能会出现异常
			groupsService.addFriendGroup("我的好友", service);
			friendsService.addFriendByid(service.getUserid(), service.getNickname(), affiliation_group, user.getUserid(),friend_type);
			response.getWriter().write("注册客服成功！");
			List<Friends> friends = friendsService.findFriendsById(user);
			request.getSession().setAttribute("friends", friends);
			//反向添加：这里是客服，注册之后强制添加到客服的好友列表。
			String group_id = groupsService.findFriendGroupsById(service).get(0).getGroup_id();
			friendsService.addFriendByid(user.getUserid(), user.getNickname(), group_id, service.getUserid(),2);
			
		} catch (Exception e) {
			try {
				response.getWriter().write(e.getMessage());
			} catch (Exception e2) {
				throw new RuntimeException(e2);
			}
		}
	}
	
	//客户端连接
	@RequestMapping("/clientConnect.do")
	public String clientConnect(Model model,HttpSession session,HttpServletRequest request,String phone,String email) throws UnsupportedEncodingException{
		String clientId = CommonUtils.getUUID().substring(0,10);

		if(Pattern.compile("[a-zA-Z]").matcher(clientId).find() == false){
    		/**
    		 * 当前id不存在英文，需要修改一下
    		 */
			clientId = clientId.replace(clientId.substring(0, 1), "C");
    	}
		session.setAttribute("clientId",clientId );
		session.setAttribute("bossId", request.getParameter("bossId"));
		String nickname = new String(request.getParameter("nickname").getBytes("ISO8859-1"),"utf-8");
		session.setAttribute("nickname", nickname);
		session.setAttribute("phone", request.getParameter("phone"));
		session.setAttribute("email", request.getParameter("email"));
		return "redirect:/initChat.do";
	}
	
	//如果不经过这个跳转会得不到cookie
	@RequestMapping("/initChat.do")
	public String initChat(Model model,HttpSession session,String nickname,String phone,String email){
		return "/jsp/clientChat.jsp";
	}
	
	@RequestMapping("/clientLogout.do")
	public String clientLogout(Model model,HttpSession session,String nickname,String phone,String email){
		String bossId = (String) session.getAttribute("bossId");
		session.invalidate();
		return "redirect:/jsp/clientConnect.jsp?bossId="+bossId;
	}
}
