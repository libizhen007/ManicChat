<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ManicChat - 一个在线即时的聊天系统</title>

<title>主页登陆</title>
<!-- 本人 -->
<script type="text/javascript" src="<c:url value='js/BiZhen.js'/>"></script>
<script type="text/javascript" src="<c:url value='js/jquery-1.7.2.js'/>"></script>
<!-- 文本框  -->
<script type="text/javascript" src="<c:url value='js/phanimate.jquery.js'/>"></script> 
<!-- bootstrap插件 -->     
<script type="text/javascript" src="<c:url value='js/bootstrap.min.js'/>"></script> 
<link rel="stylesheet" href="<c:url value='css/bootstrap.min.css'/>">
<!-- 按钮 -->
<link rel="stylesheet" href="<c:url value='css/buttons.css'/>">
 
<style type="text/css">
body{
	margin:0px;
	padding: 0px;
	background: url("/ManicChat/image/mainBackground.jpg") no-repeat center center;
	background-size:cover; 
	min-height:100%;
	
	
/* 	margin-bottom:100px;/* 背景全屏 */
	height:100%;
	background-attachment:fixed; 
}
div.login{
	margin-top: 50px;
	width:500px;
	height:300px;
	border: 3px solid #3AACFF;
	border-radius:30px;
	padding-top:10px;
	text-align: center;
	margin: 100px auto;
	line-height: 65px;
	font-size: 20px;
	font-family: 黑体;

}


		.container{padding: 1.5em 0;}
		.custom-input {
		  position: relative;
		  padding-top: 20px;
		  margin-bottom: 10px;
		  width: 250px;
		  margin: 0 auto;
		}

		.custom-input input {
		  padding-left: 15px;
		}

		.custom-input label {
		  cursor: text;
		  margin: 0;
		  padding: 0;
		  left: 15px;
		  top: 5px;
		  position: absolute;
		  font-size: 14px;
		  color: #ccc;
		  font-weight: normal;
		  transition: all .3s ease;
		}
		
		.custom-input label.active {
		  top: -20px;
		  left: 0;
		  font-size: 15px;
		  color: #000;
		}
		
		.custom-input label.active.focusIn {
		  color: #000;
		  font-family: 黑体;
		  font-style: oblique;
		}
		.form-control{
			font-size: 17px;
		}
		.title{
			color: #000;
			text-decoration: underline;
			font-style: oblique;
		}
		#personal-modal{
			min-height:100%;
			background-size:cover;
			margin-bottom: 300px;/* 背景全屏 */
			line-height: 65px;
		}
		
		.vcode{
			width: 150px;
			height:30px;
			float: right;
			margin-top: -35px; 
			cursor:pointer;
		}
		
		.userType-radio{
			margin-top: -30px;
			margin-bottom: -30px;
			text-align: center;
		}
		
		#explain{
			
			border: 1px solid #000000;
			border-radius:5px;
			font-family: Microsoft YaHei;
			float: left;
			width: 400px;
			height:120px;
			padding-left:5px;
			text-align:left;
			line-height: 20px;
		}
</style>
<script type="text/javascript">
	$(function(){
		$('.custom-input input').phAnim();
		
		$("#register").click(function(){
			$(":radio").removeAttr("checked");
		});
		$(":radio").click(function(){
			if($(this).val()==0){
				$("#personal-label").show(500);
				$("#enterprise-label").hide();
			}else{
				$("#enterprise-label").show(500);
				$("#personal-label").hide();
			}
		});
		
		$("#reset").click(function(){
			$("#enterprise-label").hide();
			$("#personal-label").hide();
			$("#reg-userid").val("");
			$("#reg-upassword").val("");
			$("#reg-nickname").val("");
			$("#reg-vcode").val("");

			$(".reg-usertype").get(0).checked=false;
			$(".reg-usertype").get(1).checked=false;
		}); 
		
		$(":submit").click(function(){
			var patt1=/[a-z]|[A-Z]/g;
			if($("#login-userid").val()!=""){
				if($("#login-userid").val().length>=5 && $("#login-userid").val().length<=10){
					if(isNaN($("#login-userid").val()) == false){
						if($("#login-upassword").val().length>=6 && $("#login-upassword").val().length<=16){
							
							$(".userErrorMsg").text("");
							return true;
						}else{
							$(".userErrorMsg").text("密码长度为6-16个字符");
							return false;
						}
					}else{
						$(".userErrorMsg").text("用户名只能由数字组成");
						return false;
					}
				}else{
					$(".userErrorMsg").text("用户名长度为5-10个字符");
					return false;
				}
			}else{
				$(".userErrorMsg").text("用户名不能为空");
				return false;
			}
			
			
			
		});
		
		$("#regSubmit").click(function(){
			
			
			var patt1=/[a-z]|[A-Z]/g;
			if($("#reg-userid").val()!=""){
				if($("#reg-userid").val().length>=5 && $("#reg-userid").val().length<=10){
					if(isNaN($("#reg-userid").val())==false){
						$("#userErrorMsg").text("");
					}else{
						$("#userErrorMsg").text("用户名只能由数字组成");
						return false;
					}
				}else{
					$("#userErrorMsg").text("用户名长度为5-10个字符");
					return false;
				}
			}else{
				$("#userErrorMsg").text("用户名不能为空");
				return false;
			}
			
			if($("#reg-upassword").val().length>=6 && $("#reg-upassword").val().length<=16){
				$("#userErrorMsg").text("");
			}else{
				$("#userErrorMsg").text("密码长度为6-16个字符");
				return false;
			}
			
			if($("#reg-nickname").val().length>=2 && $("#reg-nickname").val().length<=10){
				$("#userErrorMsg").text("");
			}else{
				$("#userErrorMsg").text("昵称长度为2-10个字符");
				return false;
			}
			
			if($("#reg-vcode").val() == "" || $("#reg-vcode").val().length !=4){
				$("#userErrorMsg").text("验证码长度为4位");
				return false;
			}
			
			if($("input:checked").val() == undefined){
				$("#userErrorMsg").text("请选择用户类型");
				return false;
			}


			$.post("<c:url value='registerUser.do'/>",
				{
				  userid:$("#reg-userid").val(),
			      upassword:$("#reg-upassword").val(),
			      nickname:$("#reg-nickname").val(),
			      usertype:$("input:checked").val(),
			      vcode:$("#reg-vcode").val(),
			    },
			    function(data){
			    	if(data.indexOf("成功")!=-1){
			    		alert(data);
			    		$("#userErrorMsg").text("");
			    		$(".close").click();
			    	}else{
			    		$("#userErrorMsg").text(data);
			    	}
			    }
			);
			 
		});
		
		$("#reg-vcode").focus(function(){
			$(".vcode").attr("src","<c:url value='/getVCode.do'/>?date=" 
					+ new Date().getTime());
		});
		
		$(".vcode").click(function(){
			$(".vcode").attr("src","<c:url value='/getVCode.do'/>?date=" 
					+ new Date().getTime());
		});
		
	});
	
</script>
</head>
<body>
	<div class="login">
		<label class="title">用户登录</label><br/>
		<div style="color: red;height: 50px;margin-top: -50px;" >
			<label class="userErrorMsg">${userErrorMsg }</label>
		</div>
		
		<form action="<c:url value='/login.do'/>" name="login-form" method="post">

			<div class="custom-input">
			  <label for="test">UserId</label>
			  <input type="text" class="form-control" name="userid" id="login-userid" value="${echoUserid }">
			</div>
			<div class="custom-input">
			  <label for="test">Password</label>
			  <input type="password" class="form-control" name="upassword" id="login-upassword">
			</div>
			&ensp;
			<input type="submit" value="登录" id="login" class="button button-glow button-border button-rounded button-primary button-small"/>
			&ensp;&ensp;
			<input type="button" value="注册" id="register" class="button button-glow button-border button-rounded button-primary button-small" data-toggle="modal" data-target="#personal-modal"/>
			&ensp;<br/> 
		</form>
		<!-- <a href="#" id="reg-enterprise" data-toggle="modal" data-target="#enterprise">企业(网站主)用户请点击这里注册</a> -->
	</div>

	<!-- 个人用户注册模态框（Modal） -->
	<div class="modal fade" id="personal-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="false">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" 
							aria-hidden="false">×
					</button>
					<h4 class="modal-title" id="myModalLabel">
						用户注册
					</h4>
				</div>

					<%-- <form id="reg-form" method="post" action="<c:url value='/registerUser.do'/>"> --%>
					<div class="modal-body">
						<div style="text-align: center;margin-top: -20px;height: 25px;margin-bottom: 20px;color: red;">
							<label id="userErrorMsg"></label>
						</div>
						<div class="custom-input">
						  <label for="personal-form">Username</label>
						  <input type="text" class="form-control" name="userid" id="reg-userid">
						</div>
						<div class="custom-input">
						  <label for="personal-form">Password</label>
						  <input type="password" class="form-control" name="upassword" id="reg-upassword">
						</div>
						<div class="custom-input">
						  <label for="personal-form">Nick Name</label>
						  <input type="text" class="form-control" name="nickname" id="reg-nickname">
						</div>
						<div class="custom-input">
						  <label for="personal-form">Verification Code</label>
						  <input type="text" class="form-control" name="vcode" id="reg-vcode">
						</div>
						<img alt="Verification Code" <%-- src="<c:url value='/getVCode.do'/>" --%> class="vcode"><!--src="#"  出现这个会导致访问Controller  -->
					</div>
					<div class="userType-radio">
						<label><input type="radio" name="usertype"  value="0" class="reg-usertype" >个人用户</label>&emsp;&emsp;&emsp;
						<label><input type="radio" name="usertype"  value="1" class="reg-usertype">企业(网站主)</label>
					</div>
					<div class="modal-footer">
						<div id="explain">
							<label hidden="hidden" id="enterprise-label">
								&emsp;企业用户(网站主)可以在登录成功之后为企业(网站主)添加当前企业(网站)的客服，添加的客服属于当前企业；
							<br/>&emsp;
								并且在登录成功之后可以获取一个属于本企业(网站)的专属链接，可以把该链接放置在你的网站的任何位置，通过该链接客户可以不需要注册就可以咨询当前在线的客服。
							</label>
							<label id="personal-label" hidden="hidden">
								&emsp;个人普通用户可以添加好友、聊天和群聊等等...
							</label>
						</div>
						
						<button id="regSubmit" class="btn btn-success">提交注册</button>
						<button  class="btn btn-warning" id="reset">重置</button>
					</div>
				<!-- </form> -->

			</div><!-- /.modal-content -->
		</div><!-- /.modal-dialog -->
	</div><!-- /.modal -->

</body>
</html>