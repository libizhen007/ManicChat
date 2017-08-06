<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>欢迎使用ManicChat - 魔法聊天_当前用户：username</title>
<!-- 本人 -->
<script type="text/javascript" src="<c:url value='/js/BiZhen.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/ManicChat.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/jquery-1.7.2.js'/>"></script>
<!-- 文本框  -->
<script type="text/javascript" src="<c:url value='/js/phanimate.jquery.js'/>"></script> 
<!-- bootstrap插件 -->     
<script type="text/javascript" src="<c:url value='/js/bootstrap.min.js'/>"></script> 
<link rel="stylesheet" href="<c:url value='/css/bootstrap.min.css'/>">
<!-- 按钮 -->
<link rel="stylesheet" href="<c:url value='/css/buttons.css'/>">


<!--通知  -->
<link rel='stylesheet' href='<c:url value='/css/animate.min.css'/>'>
<link rel="stylesheet" href="<c:url value='/css/notification.css'/>">
<script src="<c:url value='/js/notification.js'/>"></script>



<!--右键菜单插件  -->
<script type="text/javascript" src="<c:url value='/js/jquery.contextify.js'/>"></script>          

<!--jquery-form  -->
<script type="text/javascript" src="<c:url value='/js/jquery.form.js'/>"></script> 
<!-- 本页面的css -->
<link rel="stylesheet" href="<c:url value='/css/ManicChat.css'/>">

<style type="text/css">
body{
	margin:0px;
	padding: 0px;
	background: url("<c:url value='/image/mainBackground.jpg'/>") no-repeat center;
	background-size:cover;
	-webkit-background-size: cover;/* 兼容Webkit内核浏览器如Chrome和Safari */
	-o-background-size: cover;/* 兼容Opera */ 
	background-attachment:fixed; 
}
#chat-window{
	width: 1000px;
	height: 600px;
	float: none;
	margin: 0 auto;
}
#chat-from{
	height: 400px;
	
}
.from-right-content{
	margin-left: 378px;
}
.sendChatImg{
	margin-left: 645px;
}
</style>

<script type="text/javascript">

//循环聊天内容
function showChatContent(userCookieData){
	var obj = JSON.parse(userCookieData);
	var recordLength = obj.chat_time.length;
	var action = "";
	for (var i = 0; i < recordLength; i++) {
		action = obj.chat_action[i];
		if (action == "send") {
			$("#chat-from")
					.append(
							"<div class='right-bubble'><div class='chat-time'></div><pre class='from-right-content'></pre></div>");
			$(".from-right-content:last").text(
					decodeURIComponent(obj.chat_content[i]));
		} else if(action == "receive"){
			$("#chat-from")
					.append(
							"<div class='left-bubble'><div class='chat-time'></div><pre class='from-left-content'></pre></div>");
			$(".from-left-content:last").text(
					decodeURIComponent(obj.chat_content[i]));
		}else if(action == "sendImg"){
			$("#chat-from")
			.append(
					"<div class='right-bubble'><div class='chat-time'></div><a class='chatImga' title='双击在新窗口查看图片' target='_blank' href=''><img alt='聊天图片' src='' class='sendChatImg'/></a></div>");
           $(".right-bubble:last a").attr("href", "/file/" + decodeURIComponent(obj.chat_content[i]));
           $(".right-bubble:last img").attr("src", "/file/" + decodeURIComponent(obj.chat_content[i]));
		  
		}else if(action == "receiveImg"){
			$("#chat-from")
			.append(
					"<div class='left-bubble'><div class='chat-time'></div><a class='chatImga' title='双击在新窗口查看图片' target='_blank' href=''><img alt='聊天图片' src='' class='receiveChatImg'/></a></div>");
           $(".left-bubble:last a").attr("href", "/file/" + decodeURIComponent(obj.chat_content[i]));
           $(".left-bubble:last img").attr("src", "/file/" + decodeURIComponent(obj.chat_content[i]));
		  
		}else if(action == "sendFile"){
			var fileUrl = decodeURIComponent(obj.chat_content[i]).split("/")[0];
			var fileName = decodeURIComponent(obj.chat_content[i]).split("/")[1];
			$("#chat-from")
			.append(
					"<div class='right-bubble'><div class='chat-time'></div><p class='from-right-content sendChatFile'>[文件类型]"+fileName+"<a title='点击保存文件'  target='_blank' href=''>保存文件</a></p></div>");
           $(".right-bubble:last a").attr("href", "/file/" + fileUrl);
		  
		  
		}else if(action == "receiveFile"){
			var fileUrl = decodeURIComponent(obj.chat_content[i]).split("/")[0];
			var fileName = decodeURIComponent(obj.chat_content[i]).split("/")[1];
			$("#chat-from")
			.append(
					"<div class='left-bubble'><div class='chat-time'></div><p class='from-left-content sendChatFile'>[文件类型]"+fileName+"<a title='点击保存文件'  target='_blank' href=''>保存文件</a></p></div>");
           $(".left-bubble:last a").attr("href", "/file/" + fileUrl);
		   
		  
		}
		$(".chat-time:last").text(obj.chat_time[i]);
		$("#msgContent").focus();
	}
}

$(function(){
	var clientId = "${clientId}";
	var bossId = "${bossId}";
	var serviceId = "";
	var userObj = {
			"clientId":clientId,
			"bossId":bossId,
			"nickname":encodeURIComponent("${nickname}"),
			"phone":"${phone}",
			"email":"${email}",
	}
	
	setCookie(clientId, JSON.stringify(userObj), 1);//这个没有什么用
	setCookie("wsUserid", clientId, 1);;
	var socket;
	if ('WebSocket' in window) {
		socket = new WebSocket('ws://www.ageuu.top/ManicChat/createWebsocket.ws');

	}
	socket.onopen = function() {
//		socket.send("clientOnline:" + JSON.stringify(userObj));
		var msgType = "clientOnline";
		socket.send("{\"msgType\":\""+ msgType +"\",\"msgContent\":"+ JSON.stringify(userObj) +"}");
	};
	socket.onmessage = function(evt) {
		var received_msg = evt.data;
//		alert(received_msg)
		var msgObj = JSON.parse(received_msg);
		var msgType = msgObj.msgType;
		var msgContent = msgObj.msgContent;
		
		switch (msgType) {
			case "online":
				$("#chat-from").append("<p style='color: #00CCFF;'>连接客服成功..........<p>")
				//保存当前客服id
				serviceId = msgContent;
				$("#chat-nickname").attr("title",serviceId);
				if(getCookie("chat" + serviceId)!=""){
					showChatContent(getCookie("chat" + serviceId));
				}
				break;
			case "noService":
				$("#chat-from").append("<p style='color: red;'>客服不在线上，请稍后刷新页面再联系..........<p>");
				socket.close();
				break;
			case "imageMsg":
				/**
				客户只允许发送图片
				*/
				showAndSaveImg(msgContent);
				break;
			case "textChat":
				var msgObj = msgContent;
				var msgContent = msgObj.chat_content;
				var dateText = msgObj.chat_time;
				var sendUser = msgObj.chat_sendUser;
				var userid = msgObj.chat_reception;
				$("#chat-from")
				.append(
						"<div class='left-bubble'><div class='chat-time'></div><pre class='from-left-content'></pre></div>")
				$(".from-left-content:last").text(decodeURIComponent(msgContent));
				$(".chat-time:last").text(dateText);
				
				var userChatMsg = getCookie("chat" + sendUser);
				var jsonStr;
				if (userChatMsg == "") {
					userChatMsg = {
						"chat_time" : [ dateText ],
						"chat_action" : [ "receive" ],
						"chat_content" : [ msgContent ],
						"chat_reception" : sendUser,
						"chat_sendUser" : userid
					};
					jsonStr = JSON.stringify(userChatMsg);
				} else {
					var obj = JSON.parse(userChatMsg);
					obj.chat_time[obj.chat_time.length] = dateText;
					obj.chat_action[obj.chat_action.length] = "receive";
					obj.chat_content[obj.chat_content.length] = msgContent;
					jsonStr = JSON.stringify(obj);

				}
				$("#chat-from").scrollTop(1000000);
				setCookie("chat" + sendUser, jsonStr, 1);
				break;
			case "noOnline":
				$("#chat-from").append("<p style='color: red;'>客服已经下线，请刷新页面或稍后咨询.......<p>");
				break;
			default:
				break;
		}
	};
	socket.onclose = function(e) {
		alert("连接断开，请刷新页面");
	}
	socket.onerror = function(e) {
		alert("连接错误，请重新打开或刷新页面");
	};
	
	$("#sendMsg").click(function(){
		var msgContent = $("#msgContent").val();
		var contentLength = msgContent.length;
		if (contentLength == 0) {
			alert("请输入内容!");
			return false;
		} else if (contentLength > 128) {
			alert("内容过长，请保持在128字以内。当前长度：" + contentLength);
			return false;
		};
		
		
		var nowDate = new Date();
		var dateText = "";
		//日期
		dateText = nowDate.getFullYear() + "/" + nowDate.getMonth() + "/" + nowDate.getDate();//nowDate.toLocaleString().split(" ")[0];//使用这个会在uc中出现中文
		//时间
		dateText = dateText + " " + nowDate.toTimeString().split(" ")[0];
		
		
		$("#chat-from")
				.append(
						"<div class='right-bubble'><div class='chat-time'></div><pre class='from-right-content'></pre></div>");
		$(".from-right-content:last").text(msgContent);
		$(".chat-time:last").text(dateText);
		$("#msgContent").val("");
		$("#msgContent").focus();
		
		// 保存信息,chat_time:发送或接收信息世界，chat_obj：send本人发送，receive来自好友
		var userChatMsg = getCookie("chat" + serviceId);
		var jsonStr = "";
		if (userChatMsg == "") {
			// 转换成字符串，不然会取不出来
			userChatMsg = {
				"chat_time" : [ dateText ],
				"chat_action" : [ "send" ],
				"chat_content" : [ encodeURIComponent(msgContent) ],
				"chat_reception" : serviceId,
				"chat_sendUser" : clientId,
		
			};
			jsonStr = JSON.stringify(userChatMsg);
		} else {
			var obj = JSON.parse(userChatMsg);
			obj.chat_time[obj.chat_time.length] = dateText;
			obj.chat_action[obj.chat_action.length] = "send";
			obj.chat_content[obj.chat_content.length] = encodeURIComponent(msgContent);
			jsonStr = JSON.stringify(obj);
		
		}
		var sendMsgContent = {
			"chat_time" : dateText,
			"chat_action" : "send",
			"chat_content" : encodeURIComponent(msgContent),
			"chat_reception" : serviceId,
			"chat_sendUser" : clientId
		};
//		sendMsgContent = JSON.stringify(sendMsgContent);
		setCookie("chat" + serviceId, jsonStr, 1);
		
//		socket.send(sendMsgContent);
		socket.send("{\"msgType\":\"textChat\",\"msgContent\":"+  JSON.stringify(sendMsgContent) +"}");
		$("#chat-from").scrollTop(1000000);
	});
	
 	$("#imgSend").change(function(){
		
		var url = $("#uploadUrl").text();
		$("#fileForm").attr("action",url);
		/* '<c:url value='/uploadUserImg.do'/>' */
		var options = {
		        success: function (data) {
		        	var nowDate = new Date();
		        	var dateText = "";
		        	//日期
		        	dateText = nowDate.getFullYear() + "/" + nowDate.getMonth() + "/" + nowDate.getDate();//nowDate.toLocaleString().split(" ")[0];//使用这个会在uc中出现中文
		        	//时间
		        	dateText = dateText + " " + nowDate.toTimeString().split(" ")[0];
		           $("#chat-from")
					.append(
							"<div class='right-bubble'><div class='chat-time'></div><a class='chatImga' title='双击在新窗口查看图片' target='_blank' href=''><img alt='聊天图片' src='' class='sendChatImg'/></a></div>");
		           $(".right-bubble:last a").attr("href", "/file/" + data);
		           $(".right-bubble:last img").attr("src", "/file/" + data);
				   $(".chat-time:last").text(dateText);
				   $("#chat-from").scrollTop(1000000);
				   
				   //socket发送[ friendId:imageUrl]
				   var friendId = $("#chat-nickname").attr("title");
				   var dataObj = {"friendId":friendId,"dateText":dateText,"fileUrl":data,"chat_sendUser":getCookie("wsUserid")};
				   
				    var userChatMsg = getCookie("chat" + friendId);
					var jsonStr = "";
					if (userChatMsg == "") {
						// 转换成字符串，不然会取不出来
						userChatMsg = {
							"chat_time" : [ dateText ],
							"chat_action" : [ "sendImg" ],
							"chat_content" : [ encodeURIComponent(data) ],
							"chat_reception" : friendId,
							"chat_sendUser" : getCookie("wsUserid"),
						};
						jsonStr = JSON.stringify(userChatMsg);
					} else {
						var obj = JSON.parse(userChatMsg);
						obj.chat_time[obj.chat_time.length] = dateText;
						obj.chat_action[obj.chat_action.length] = "sendImg";
						obj.chat_content[obj.chat_content.length] = encodeURIComponent(data);
						jsonStr = JSON.stringify(obj);
					}
					
					setCookie("chat" + friendId, jsonStr, 1);
			//		socket.send("imageMsg," + JSON.stringify(dataObj));
					socket.send("{\"msgType\":\"imageMsg\",\"msgContent\":"+ JSON.stringify(dataObj) +"}");
		        }
		    };
		$("#fileForm").ajaxSubmit(options);
	}); 

});
</script>

</head>
<body >
	<div style="text-align: center;">
		<input id="changeBackgroundFile" type="file" style="display:none;" accept="image/*" onchange="changeBackground(this)"/>
		<img alt="更换背景" src="<c:url value='/image/ball.png'/>" id="changggg"  style="height: 100px;width: 100px;cursor: pointer;" title="更换背景图片" onclick="$('#changeBackgroundFile').click()" >
		<div style="float: right;">
			<a id="logout" onclick="logoutClearCookie()" href="<c:url value='/clientLogout.do'/>" class="button button-glow button-border button-rounded button-primary button-small">退出系统</a>
		</div>
	</div>
<div id="bizhen" style="text-align: center;">
<p id="uploadUrl" hidden><c:url value='/uploadUserImg.do'/></p>

	<div id="chat-window">
		<div class="panel panel-info" style="text-align: center;" >
			<div class="panel-heading" id="chat-nickname" title="">
			欢迎使用ManicChat
		    </div>
		   	<div id="chat-from">
				
			</div>
		
			<div id="chat-utils" style="text-align:left;">
			<form action="" enctype="multipart/form-data" method="post" id="fileForm" target="frameFile">
				<input id="imgSend" type="file" style="display:none;" accept="image/*" name="userImg"/>
				<img  class="imgSend"  alt="发送图片" title="发送图片" src="image/image_util.png"  style="height: 50px;cursor:pointer;" onclick="$('#imgSend').click();">
				
			</form>	
			</div>
		<div id="chat-my">
			<div class="input-group">
					<!-- <input type="" class="form-control" style="height: 95px"> -->
					<textarea class="form-control" style="height: 80px" id="msgContent"></textarea>
					<span class="input-group-btn">
						<button class="button button-action" type="button" style="height: 80px;" id="sendMsg">
							发送
						</button>
						
					</span>
			</div><!-- /input-group -->
		</div>
		
	</div>

	</div>

</div>
</body>
</html>