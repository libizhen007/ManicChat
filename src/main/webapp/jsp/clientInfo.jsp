<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>客户null的信息</title>
<script type="text/javascript" src="<c:url value='/js/BiZhen.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/jquery-1.7.2.js'/>"></script>
<script type="text/javascript"
	src="<c:url value='/js/bootstrap.min.js'/>"></script>
<link rel="stylesheet" href="<c:url value='/css/bootstrap.min.css'/>">

<style type="text/css">
p{
	border: 2px solid #00CCFF;
	padding: 5px;
}
</style>
<script type="text/javascript">
$(function(){
	var clientId = getCookie("currentClient")
	var clientObj = JSON.parse(getCookie("client"+clientId));
	$("#clientNickname").text(decodeURIComponent(clientObj.nickname));
	$("#clientPhone").text(clientObj.phone);
	$("#clientEmail").text(clientObj.email);
	$("title").text("客户"+decodeURIComponent(clientObj.nickname)+"的信息");
	$("#submit").click(function(){
		alert("功能错误");
		return false;
	});
});
</script>
</head>
<body>
	<div style=" max-width: 400px; max-height: 400px; ">
		<form action="">
			客户昵称：<p id="clientNickname">null</p><br/>
			客户电话：<p id="clientPhone">null</p><br>
			客户邮件：<p id="clientEmail">null</p>
			客服备注：<br>
			<textarea rows="5" cols="40" style="outline:none;resize:none;"></textarea><br>
			<input type="submit" value="保存">
		</form>
	</div>
</body>
</html>