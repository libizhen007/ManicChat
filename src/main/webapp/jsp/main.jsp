<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>欢迎使用ManicChat - 魔法聊天_当前用户：${user.nickname }</title>
<!-- 本人 -->
<script type="text/javascript" src="js/BiZhen.js"></script>
<script type="text/javascript" src="js/ManicChat.js"></script>
<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<!-- 文本框  -->
<script type="text/javascript" src="js/phanimate.jquery.js"></script> 
<!-- bootstrap插件 -->     
<script type="text/javascript" src="js/bootstrap.min.js"></script> 
<link rel="stylesheet" href="css/bootstrap.min.css">
<!-- 按钮 -->
<link rel="stylesheet" href="css/buttons.css">


<!--通知  -->
<link rel='stylesheet' href='css/animate.min.css'>
<link rel="stylesheet" href="css/notification.css">
<script src="js/jquery-1.7.2.js"></script>
<script src="js/notification.js"></script>



<!--右键菜单插件  -->
<script type="text/javascript" src="js/jquery.contextify.js"></script>          

<!--jquery-form  -->
<script type="text/javascript" src="js/jquery.form.js"></script> 
<!-- 本页面的css -->
<link rel="stylesheet" href="css/ManicChat.css">

<style type="text/css">

</style>

<script type="text/javascript">

$(function(){
	
	
//	setCookie("wsUserid", "${user.userid}", 1);
//	setCookie("bossId", "${user.affiliation_user}", 1);
	//打开ws
	openConnect("${user.userid}","${user.usertype}","${user.affiliation_user}");
	//恢复列表
	recoverChatList();
	
	setCookie("addFriendGroupUrl", "<c:url value='/addFriendGroup.do'/>", 1);
	setCookie("updateFriendGroupListUrl", "<c:url value='/updateFriendGroupList.do'/>", 1);
	setCookie("deleteFriendGroupUrl", "<c:url value='/deleteFriendGroup.do'/>", 1);
	setCookie("updateGroupUrl", "<c:url value='/updateGroup.do'/>", 1);

	$('.flex').contextify(group_menu);//弹出右键分组菜单  

	$('.friend-manage').contextify(friend_menu);//弹出右键好友菜单
	
	$("#user-manage").contextify(user_manage_menu);//弹出用户管理菜单

	$("#queryClient").click(function(){
		if($("#chat-nickname").attr("title")==""){
			alert("请先选择一个客户然后在查看");
			return false;
		}
		setCookie("currentClient", $("#chat-nickname").attr("title"), 1);
		window.open("<c:url value='/jsp/clientInfo.jsp'/>","_blank","toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=400, height=400")
	});
	
	//关闭通知
	$(".dismiss").live("click",function(){
		$(".notification ").remove();
	});
	
	$(".fileSend").click(function(){
		var friendId = $("#chat-nickname").attr("title");
		if(friendId == ""){
			alert("请先选择一个好友!");
			return false;
		}
		$('#fileSend').click();
	});
	$(".imgSend").click(function(){
		var friendId = $("#chat-nickname").attr("title");
		if(friendId == ""){
			alert("请先选择一个好友!");
			return false;
		}
		$('#imgSend').click();
	});
	
	
});

</script>
<c:set var="contextPath" value="${savePath}" scope="session"/>  
    <script>  
        <%--JS gloable varilible--%>  
        var contextPath = "${contextPath}";
        setCookie("contextPath", "${contextPath}", 1)
    </script>

</head>
<body >
	<!--把右键菜单里面的url放到这里.这里应该使用cookie和编码  -->
	<p hidden id="deleteFriendUrl"><c:url value='/deleteFriend.do'/></p>
	<p hidden id="deleteServiceUrl"><c:url value='/deleteService.do'/></p>
	<p hidden id="updateFriendCnameUrl"><c:url value='/updateFriendCname.do'/></p>
	<p hidden id="imgRightUrl"><c:url value='/image/right.png'/></p>
	<p hidden id="updatePasswordUrl"><c:url value='/updatePassword.do'/></p>
	<p hidden id="updateNicknameUrl"><c:url value='/updateNickname.do'/></p>
	<p hidden id="userHeadUrl"><c:url value='/image/head.gif'/></p>
	<p hidden id="updateFriendList"><c:url value='/updateFriendList.do'/></p>
	
	<div style="text-align: center;">
		<input id="changeBackgroundFile" type="file" style="display:none;" accept="image/*" onchange="changeBackground(this)"/>
		<img alt="更换背景" src="image/ball.png" id="changggg"  style="height: 100px;width: 100px;cursor: pointer;" title="更换背景图片" onclick="$('#changeBackgroundFile').click()" >
		<div style="float: right;">
			<a id="logout" onclick="logoutClearCookie()" href="<c:url value='/logout.do'/>" class="button button-glow button-border button-rounded button-primary button-small">退出系统</a>
		</div>
	</div>
<div id="bizhen">
	<div id="change">
		<div id="change-query-add" >
			<div class="panel panel-info" >
			<div class="panel-heading" >
				查找好友
				<span class="change-close" title="关闭" onclick="closeChangeWindow(event)">x</span>
			</div>
			   <div class="panel-body query" >
			    	Username:<input type="text" name="friend_id" style="width: 100px;"/><br/>
			    	<p style="margin-top:15px;margin-bottom: -10px;" id="query-friend-name"></p><br/>
			    	<button onclick="query('<c:url value='/findFriendById.do'/>')">查询</button>
			    	<button onclick="addFriend('<c:url value='/addFriend.do'/>')" >添加</button>
				</div>
				
			</div>
		</div>	
		
		<div id="change-group">
			<div class="panel panel-info" >
			<div class="panel-heading">
				把&ensp;<span id="shift-name"></span>&ensp;转移到
				<span class="change-close" title="关闭" onclick="closeChangeWindow(event)">x</span>
			</div>
			   <div class="panel-body shift" >
			    	<select name="shift-group">
			    		<c:forEach items="${groups }" var="group" varStatus="gs">
							<option value="${group.group_id }">${group.group_name }</option>
						</c:forEach>
					</select><br/>
					
				</div>
				<div class="modal-footer" style="text-align: center;">
				<button onclick="shiftFriend('<c:url value='/updateFriendGroup.do'/>','<c:url value='/image/right.png'/>','<c:url value='/image/down.png'/>')">确认</button>
				</div>
			</div>
		</div>	
		
		<div id="register-service">
			<div class="panel panel-info" >
			<div class="panel-heading" >
				注册客服账号
				<span class="change-close" title="关闭" onclick="closeChangeWindow(event)">x</span>
			</div>
			   <div class="panel-body" >
			    	账 号:<input type="text" name="service_id"/><br/>
			    	密 码:<input type="password" name="spassword"/><br/>
			    	昵 称:<input type="text" name="nickname"/><br/>
				</div>
				<div class="modal-footer" style="text-align: center;"> 
				<button id="register" onclick="registerSerivce('<c:url value='/registerSerivce.do'/>')">提交注册</button>
				</div>
			</div>
		</div>	
	</div>
	
	<!-- 用于保存修改和删除的分组id -->
	<p hidden id="right-hand-group"></p>
	<!--//当属性右键好友时分配给right-hand-friend一个id值，用于修改和删除   -->
	<p hidden id="right-hand-friend"></p>
	
	<!--w:200 h:500  -->
	<div id="my-list" class="panel panel-info">
		<div class="panel-heading" id="user-manage" class="current-user" title="修改密码" style="max-height: 40px;">
			${user.nickname }
		</div>
			
		<div  id="friend-list" style="height: 400px;width:196px;float: left;text-align: center;height: 420px;">
			<c:forEach items="${groups }" var="group" varStatus="gs">
					
					<c:choose>
				 		<c:when test="${gs.first==true }">
				 			 <div class="panel panel-info group" group-id="${group.group_id }" id="${group.group_id }">
				 		</c:when>
				 		<c:otherwise>
				 			 <div class="panel panel-info adjust group" group-id="${group.group_id }" id="${group.group_id }">
				 		</c:otherwise>
				 	</c:choose>
				
		    		<div class="panel-heading flex" group-id="${group.group_id }" onmousedown="setListGroupId(event)" onclick="flexGroup(event,'<c:url value='/image/down.png'/>')">
		    		<!--有消息来就闪烁  -->
				        <h3 class="panel-title " title="右键管理分组" group-id="${group.group_id }" >
					        <span class="group-name" group-id="${group.group_id }" >${group.group_name }</span>
					        <img alt="展开" src="image/right.png" class="flex-img" >
				        </h3>
				    </div>
				    
				    <!-- 设置一临时变量，用于判断是否生成 .concrete-friend-list div -->
				    <c:set var="first" value="1" scope="page"/> 
				    <!-- 设置一临时变量，用于判断是否生成 hr,如果为零则生成hr -->
				     <c:set var="generateFroHr" value="1" scope="page"/> 
				     
				    <c:forEach items="${friends }" var="friend" varStatus="fs">
				    		
				    		<c:if test="${group.group_id == friend.affiliation_group }">
							    <c:if test="${first==1 }">
							    	<div class="panel-body concrete-friend-list"  hidden>
								    <c:set var="first" value="0" scope="page"/>
								    
								</c:if>		
								<c:if test="${generateFroHr==0 }">
									<hr style="border-bottom:2px solid red;"/>
								</c:if>
								
								<!--好友  -->
								<div  class="${friend.friend_id }" group-id=${group.group_id }>
									<c:if test="${friend.friend_type ==0 && friend.friend_id != user.affiliation_user}">
										<a class="button button-pill button-tiny friend-manage" onmousedown="setListFriendId(event)" ondblclick="startChat(event)" id="${friend.friend_id }" data-toggle="tooltip" data-placement="left" title="id:  ${friend.friend_id }" style="border-bottom: solid 2px #0099FF;/* margin-top: 10px;margin-bottom: 10px; */">
							    				${friend.friend_cname }
							    		</a>
							    		
						    		</c:if>
						    		<!--客服  -->
						    		<c:if test="${friend.friend_type ==1 }">
										<a  class="button button-pill button-tiny friend-manage service" onmousedown="setListFriendId(event)" ondblclick="startChat(event)" id="${friend.friend_id }" data-toggle="tooltip" data-placement="left" title="id:  ${friend.friend_id }" style="border-bottom: solid 2px #9900CC;/* margin-top: 10px;margin-bottom: 10px; */">
							    				${friend.friend_cname }
							    		</a>
							    		
						    		</c:if>
						    		<!--上级  -->
						    		<c:if test="${friend.friend_id == user.affiliation_user}">
										<a  class="button button-pill button-tiny friend-manage boss" onmousedown="setListFriendId(event)" ondblclick="startChat(event)" id="${friend.friend_id }" data-toggle="tooltip" data-placement="left" title="id:  ${friend.friend_id }" style="border-bottom: solid 2px #FF0099;/* margin-top: 10px;margin-bottom: 10px; */">
							    				${friend.friend_cname }
							    		</a>
							    		<!-- <span class="badge" style="float: right;">1</span> -->
						    		</c:if>
						    		
						    		<c:set var="generateFroHr" value="0" scope="page"/> 
					    		</div> 
					    		
					    		
				    		</c:if>
				    		
				    		
				    </c:forEach>
				    <c:if test="${first==0 }">
					  </div>
					</c:if>
				</div>
			</c:forEach>
			
		</div>	

		<div id="user-util" style="text-align: center;width: 196px;height: 35px;padding-top: 5px;float: inherit;">
			<img alt="添加好友" src="image/user-find.png" style="height: 25px;width: 25px;" title="添加好友" id="addFriends" onclick="showAddFriendWindow()">
			&emsp;
			<img alt="添加客服" src="image/service.png" style="height: 25px;width: 25px;" title="添加客服" class="addServices" onclick="showAddServicesWindow(${user.usertype})">
			&emsp;
			<img alt="获取客服连接" src="image/settings.png" style="height: 25px;width: 25px;" title="获取客服连接" class="getServicesLink" onclick="getServiceLink(${user.usertype},${user.userid})">
		</div> 
	</div>
	
	<div id="chat-window">
		<div class="panel panel-info" style="text-align: center;" >
			<div class="panel-heading" id="chat-nickname" title="">
			欢迎使用ManicChat
		    </div>
		   	<div id="chat-from">
				
			</div>
		
			<div id="chat-utils" style="text-align:left;">
			 <!--
			 //$("#fileForm").submit();//使用这个将得不到返回结果
        注意到这个form的target的了么？这个target属性的值frameFile,是form之后的iframe的name值,
        这样的写法是让当前的form表单在提交表单内容的时候转交给iframe中进行页面中表单处理，
        并且不会产生当前页面跳转!
        **不过这样一样我就没办法得到返回值，得不到文件名；
        ***无奈，只得使用 jQuery-form.js
     -->
			<form action="" enctype="multipart/form-data" method="post" id="fileForm" target="frameFile">
				<input id="imgSend" type="file" style="display:none;" accept="image/*" onchange="sendImg(this,'<c:url value='/uploadUserImg.do'/>')" name="userImg"/>
				<img  class="imgSend"  alt="发送图片" title="发送图片" src="image/image_util.png"  style="height: 50px;cursor:pointer;">
				<input  id="fileSend" type="file" style="display:none;" accept="*/*" onchange="sendFile(this,'<c:url value='/uploadUserFile.do'/>')" name="userFile"/>
				<img class="fileSend"  alt="发送文件" title="发送文件" src="image/file-util.png"  style="height: 50px;cursor:pointer;">
				&ensp;
			
				<img  alt="添加用户到群组" title="添加当前好友到群组" src="image/add-user-group.png" onclick="addUserGroup()" style="height: 30px;width:30px;cursor:pointer;">    
				&ensp;&ensp;&ensp;
				
				<img  alt="当前客户信息" title="查看当前客户信息" src="image/clientNote.png"  style="height: 30px;width:30px;cursor:pointer;" id="queryClient">      
			</form>
			<!--先放这里，使用了jQuery-form之后用不到  -->
			<iframe id='frameFile1' name='frameFile' style='display: none;'></iframe>
			</div>
		<div id="chat-my">
			<div class="input-group">
					<!-- <input type="" class="form-control" style="height: 95px"> -->
					<textarea class="form-control" style="height: 80px" id="msgContent"></textarea>
					<span class="input-group-btn">
						<button class="button button-action" type="button" style="height: 80px;" id="sendMsg" disabled="disabled" onclick="sendMsg(${user.userid})">
							发送
						</button>

					</span>
			</div><!-- /input-group -->
		</div>
		
		</div>

	</div>

	<div class="panel panel-info" id="chat-list">
    		<div class="panel-heading">
		        <h3 class="panel-title">聊天列表</h3>
		    </div>
		    <p hidden id="right-hand-chat-friend"></p>
		    <div class="panel-body" id="chat-friend-list">
		    </div>    
		    
	</div>
	
	
	
</div>
</body>
</html>