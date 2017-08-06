<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>准备连接</title>
<script type="text/javascript" src="<c:url value='/js/jquery-1.7.2.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/ManicChat.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/BiZhen.js'/>"></script>
<style type="text/css">
body{
	margin:0px;
	padding: 0px;
	background: url("<c:url value='/image/mainBackground.jpg'/>") no-repeat center;
	
	min-height:100%;
	background-size:cover;
	height:100%;
	background-attachment:fixed; 
}
	input{
		margin-bottom: 20px;
		width: 150px;
	}
	

</style>

<script type="text/javascript">
$(function(){
	function getParameter(name) { 
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
		var r = window.location.search.substr(1).match(reg); 
		if (r != null) return unescape(r[2]); return null; 
	} 
	var bossId = getParameter("bossId");

	if(bossId==null){
		alert("url不正确，请勿随意修改！");
		return false;
	}
	$("#connect").click(function(){
		var nickname = $("input[name='nickname']").val()
		var phone = $("input[name='phone']").val()
		var email = $("input[name='email']").val()
		if(nickname=="" || phone=="" || email==""){
			alert("请先填写以上选项");
			return false;
		}
		$("input[name='bossId']").val(bossId);
		/* setCookie("nickname", nickname, 0.1);
		setCookie("phone", phone, 0.1);
		setCookie("email", email, 0.1); */
		//window.open("<c:url value='/clientConnect.do'/>?bossId="+bossId,"_top");
		
		$("#myform").submit();
	});
	
});

</script>
</head>
<body>
	
	<div style="height: 800px;width: 1000px;margin: 0 auto; border: 3px solid #99CCFF;">
		<div style="text-align: center;">
			<h3>欢迎您的到来，请输入以下简要信息以便客服更好地服务！</h3>
			<div style="margin-top: 100px;">
				<form action="<c:url value='/clientConnect.do'/>" id="myform" style="color: red;">
					<input type="hidden" name="bossId" value=""/>
					您的称呼:<input type="text" name="nickname"/><b style="color: red;margin-left: 10px;">*</b><br/>
					联系电话:<input type="text" name="phone"/><b style="margin-left: 10px;">*</b><br/>
					联系邮箱:<input type="text" name="email"/><b style="margin-left: 10px;">*</b><br/>
					<input value="连接客服" type="button" id="connect" style="margin-left: 50px;"/>
				</form>
			</div>
		</div>
		<%-- <div style=""><!--1000*500  -->
			<a href="www.baidu.com" title="进入网站主页" target="_blank">
				<img alt="acImg" src="<c:url value='/image/mainBackground.jpg'/>" style="max-width: 1000px;max-height: 500px;">
			</a>
		</div> --%>
	</div>
</body>
</html>