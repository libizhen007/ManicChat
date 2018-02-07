/**
 * 项目页面函数
 */

var socket;
function logoutClearCookie() {
	var chat_friend_list = $("#chat-friend-list").children("div");
	for (var i = 0; i < chat_friend_list.size(); i++) {
		var friend_id = $(chat_friend_list.get(i)).attr("class").split("-")[1];
		clearCookie("chat" + friend_id);
	}
	//删除所有cookie，火狐好像不适用
	clearAllCoolie();

}

function closeChangeWindow(event) {
	var hiddenId = $($(event.target).parents("div").get(2)).attr("id");
	$("#" + hiddenId).css("visibility", "hidden");
}

var friend_menu = {
	items : [
			{
				header : '好友管理'
			},
			{
				divider : true
			},
			{
				text : '修改备注',
				onclick : function(e) {
					var friend_cname = prompt("请输入新的好友备注", "");
					var friend_id = $("#right-hand-friend").text();
					var updateFriendCnameUrl = $("#updateFriendCnameUrl")
							.text();
					if (friend_cname.length < 2 || friend_cname.length > 6) {
						alert("分组名称长度为2-6");
						return false;
					}
					;
					$.get(updateFriendCnameUrl, {
						"friend_cname" : friend_cname,
						"friend_id" : friend_id
					}, function(data) {
						if (data.indexOf("成功") != -1) {
							alert("更新成功！");
							$("#" + friend_id).text(friend_cname);
							$("div[title=" + friend_id + "]")
									.text(friend_cname);
							$(".chat-" + friend_id + " a").text(friend_cname);
						} else {
							alert(data);
						}
						;
					});

				}
			},// 好友删除完之后看看还有没有还有
			{
				text : '删除好友',
				onclick : function(e) {
					var delete_friend = confirm("确定要删除吗？");
					var friend_id = $("#right-hand-friend").text();
					var delete_friend_url;
					//判断是删除客服还是删除好友
					if($("#" + friend_id).hasClass("service")){
						alert("删除的是客服，删除成功之后该客服将不能登录");
						delete_friend_url = $("#deleteServiceUrl").text();
					}else{
						delete_friend_url = $("#deleteFriendUrl").text();
					}
					if (delete_friend) {
						$.get(delete_friend_url, {
							"friend_id" : friend_id
						}, function(data) {
							if (data.indexOf("成功") != -1) {
								alert("删除成功！");
								//删除成功之后通知这个被删除的好友,删除当前列表的该好友。如果当前被删除的好友在线的话。
/*								socket.send("{deleteOffline:{friend_id:"
										+ friend_id + ",userid:"
										+ getCookie("wsUserid") + "}}");*/
								
								socket.send("{'msgType':'deleteOffline','msgContent':{'friend_id':"+ friend_id +",'userid':"+ getCookie("wsUserid") +"}}");
								deletedFriendById(friend_id);
							} else {
								alert(data);
								return false;
							};
						});
					};
				}
			}, {
				text : '转移分组',
				onclick : function(e) {
					$("#change-group").css("visibility", "visible");
					var friend_id = $("#right-hand-friend").text();
					$("#shift-name").text($("#" + friend_id).text());
					$("#shift-name").attr("shift-friend-id", friend_id);
				}
			}, {
				divider : true
			}, {
				header : 'ManicChat',
				href : '#'
			} ],
	menuId : "friend_menu"
};

function showAddServicesWindow(usertype) {
	if (usertype != 1) {
		alert("当前登录的用户不是企业类型，不能进行客服添加！");
		return false;
	}
	$("#register-service").css("visibility", "visible");
}
function showAddFriendWindow() {
	$("#change-query-add").css("visibility", "visible");
	$("input[name='friend_id']").val("");
	$("#query-friend-name").text("");
	$("input[name='friend_id']").removeAttr("readonly");
}

function registerSerivce(url) {
	var service_id = $("input[name='service_id']").val();
	var spassword = $("input[name='spassword']").val();
	var nickname = $("input[name='nickname']").val();
	// var patt1=/[a-z]|[A-Z]/g;

	if (service_id != "") {
		if (service_id.length >= 5 && service_id.length <= 10) {
			if (isNaN(service_id)) {
				alert("用户名只能由数字组成");
				return false;
			}
		} else {
			alert("用户名长度为5-10个字符");
			return false;
		}
	} else {
		alert("用户名不能为空");
		return false;
	}

	if (spassword.length < 6 || spassword.length > 16) {
		alert("密码长度为6-16个字符");
		return false;
	}

	if (nickname.length < 2 || nickname.length > 10) {
		alert("昵称长度为2-10个字符");
		return false;
	}
	var affiliation_group = $($("div[group-id]").get(0)).attr("group-id");
	$
			.get(
					url,
					{
						"userid" : service_id,
						"upassword" : spassword,
						"nickname" : nickname,
						"usertype" : 2,
						"affiliation_group" : affiliation_group,
						"friend_type" : 1
					},
					function(data) {
						if (data.indexOf("成功") != -1) {
							// 默认第一个分组
							alert("添加客服成功！新添加的客服将处于分组："
									+ $(
											"div[group-id=" + affiliation_group
													+ "] .group-name").text());
						} else {
							alert(data);
							return false;
						}

						addFriendToList(nickname, service_id, affiliation_group, false);
						$("input[name='service_id']").val("");
						$("input[name='spassword']").val("");
						$("input[name='nickname']").val("");
						$("#register-service").css("visibility", "hidden");
						$('.friend-manage').contextify(friend_menu);// 弹出右键好友菜单
					});

}

function query(url) {
	var friend_id = $("input[name='friend_id']").val();
	// 查询之后把编辑框弄残疾
	var patt1 = /[a-z]|[A-Z]/g;
	if (friend_id != "") {
		if (friend_id.length >= 5 && friend_id.length <= 10) {
			if (friend_id.match(patt1) == null) {
				$.get(url, {
					"friend_id" : friend_id
				}, function(data) {
					if (data.indexOf("error") != -1) {
						$("#query-friend-name").text(data);
						$("#query-friend-name").css("color", "red");
					} else {
						$("#query-friend-name").css("color", "#FF00CC");
						$("#query-friend-name").text(data);
						$("input[name='friend_id']").attr("readonly",
								"readonly");
					}
				});
			} else {
				alert("用户名不能含有字母");
				return false;
			}
		} else {
			alert("用户名长度为5-10个字符");
			return false;
		}
	} else {
		alert("用户名不能为空");
		return false;
	}
}

function addFriend(url) {
	// 默认第一个分组
	var affiliation_group = $($("div[group-id]").get(0)).attr("group-id");
	var friend_id = $("input[name='friend_id']").val();
	// 默认就是昵称
	var friend_cname = $("#query-friend-name").text();
	$
			.get(
					url,
					{
						"affiliation_group" : affiliation_group,
						"friend_type" : 0
					},
					function(data) {
						if (data.indexOf("成功") == -1) {
							$("#query-friend-name").text(data);
							$("#query-friend-name").css("color", "red");

						} else {
							alert("添加好友成功！新添加的好友将处于分组："
									+ $(
											"div[group-id=" + affiliation_group
													+ "] .group-name").text());

							
							$("input[name='friend_id']").val("");
							$("#query-friend-name").text("");
							addFriendToList(friend_cname, friend_id, affiliation_group, true);
							
							//好友添加成功之后重新发送当前所有列表的好友到ws用于更新,并且对这个新添加的好友用于判断，如果这个新添加的好友在线上则添加
							if(getAllFriendId()!=""){
							//	alert(getAllFriendId());
								socket.send("{'msgType':'updateAllFriendId','msgContent':'"+ getAllFriendId() +"'}");
								
								socket.send("{'msgType':'newFriend','msgContent':{'friend_id':"+ friend_id +",'nickname':"+ $("#user-manage").text() +",'userid':" + getCookie("wsUserid") + "}}");
							//	socket.send("{updateAllFriendId:{friendIdsMap:[" + getAllFriendId() + "],friendId:" + friend_id + ",username:"+ $("#user-manage").text() + ",userid:" + getCookie("wsUserid") + "}}" );
							}
						}
						$("input[name='friend_id']").removeAttr("readonly");
					});
	

}

var user_manage_menu = {
	items : [ {
		header : '用户管理'
	}, {
		divider : true
	}, {
		text : '修改密码',
		onclick : function(e) {
			alert("请注意！更新密码成功之后系统会自动退出当前账号并返回主页，请保存你的资料！");
			var newPassword = prompt("请输入新的用户密码", "");
			if (newPassword == "${user.upassword}") {
				alert("新密码不能与旧密码相同");
				return false;
			}
			if (newPassword.length < 6 || newPassword.length > 16) {
				alert("新密码长度需在6-16个字符之间");
				return false;
			}
			var updatePasswordUrl = $("#updatePasswordUrl").text();
			$.get(updatePasswordUrl, {
				"newPassword" : newPassword
			}, function(data) {
				alert(data);
				document.getElementById("logout").click();
			});
		}
	}, {
		text : '修改昵称',
		onclick : function(e) {

			var newNickname = prompt("请输入新的用户昵称", "");
			if (newNickname.length < 2 || newNickname.length > 10) {
				alert("昵称长度为2-10个字符");
				return false;
			}
			var updateNicknameUrl = $("#updateNicknameUrl").text();
			$.get(updateNicknameUrl, {
				"newNickname" : newNickname
			}, function(data) {
				alert(data);
				$("#user-manage").text(newNickname);
			});
		}
	}, {
		divider : true
	}, {
		header : 'ManicChat',
		href : '#'
	} ],
	menuId : "group_menu"
};

function sendImg(file,url) {
	var friendId = $("#chat-nickname").attr("title");
	$("#fileForm").attr("action",url);
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
				var dataObj = {"friendId":friendId,"dateText":dateText,"fileUrl":data,"chat_sendUser":getCookie("wsUserid")};
				setCookie("chat" + friendId, jsonStr, 1);
				
//				socket.send("imageMsg," + JSON.stringify(dataObj));
				//socket.send("{'msgType':'imageMsg','msgContent':{'friendId':'"+friendId+"','dateText':'"+dateText+"','fileUrl':'"+data+"','chat_sendUser':'"+getCookie("wsUserid")+"'}}");
				sendMessage("imageMsg", JSON.stringify(dataObj));
	        }
	    };
	$("#fileForm").ajaxSubmit(options);
}
function sendFile(file,url) {
	var friendId = $("#chat-nickname").attr("title");
	$("#fileForm").attr("action",url);
	
	var fileName =  $(file).val().substring($(file).val().lastIndexOf("\\")+1);
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
						"<div class='right-bubble'><div class='chat-time'></div><p class='from-right-content sendChatFile'>[文件类型]"+fileName+"<a title='点击保存文件'  href=''>保存文件</a></p></div>");
	           $(".right-bubble:last a").attr("href", "/file/" + data);
			   $(".chat-time:last").text(dateText);
			   $("#chat-from").scrollTop(1000000);

			   var dataObj = {"friendId":friendId,"dateText":dateText,"fileUrl":data+"/"+fileName,"chat_sendUser":getCookie("wsUserid")};
			   
			    var userChatMsg = getCookie("chat" + friendId);
				var jsonStr = "";
				if (userChatMsg == "") {
					// 转换成字符串，不然会取不出来
					userChatMsg = {
						"chat_time" : [ dateText ],
						"chat_action" : [ "sendFile" ],
						"chat_content" : [ encodeURIComponent(data+"/"+fileName) ],
						"chat_reception" : friendId,
						"chat_sendUser" : getCookie("wsUserid"),
					};
					jsonStr = JSON.stringify(userChatMsg);
				} else {
					var obj = JSON.parse(userChatMsg);
					obj.chat_time[obj.chat_time.length] = dateText;
					obj.chat_action[obj.chat_action.length] = "sendFile";
					obj.chat_content[obj.chat_content.length] = encodeURIComponent(data +"/" + fileName);
					jsonStr = JSON.stringify(obj);
				}
				setCookie("chat" + friendId, jsonStr, 1);
			    sendMessage("fileMsg", JSON.stringify(dataObj));
	        }
	    };
	
	$("#fileForm").ajaxSubmit(options);
}

function addUserGroup() {
	alert("待开发群聊");
}

function shiftFriend(url, rightImg, downImg) {

	// 要转移的好友所属div id
	var friend_id = $("#shift-name").attr("shift-friend-id");
	// 所属分组 id
	var affiliation_group = $("." + friend_id).attr("group-id");
	// shift-group要转移到的分组
	var shift_group = $("select option:selected").val();

	if (shift_group == affiliation_group) {
		alert("分组无改变！");
		return false;
	}
	$
			.get(
					url,
					{
						"affiliation_group" : shift_group,
						"friend_id" : friend_id
					},
					function(data) {
						if (data.indexOf("成功") != -1) {
							alert("移动成功！");
							// 判断要转移到的分组有没有好友
							if ($("#" + shift_group).children().size() == 1) {//没有好友

								$("#" + shift_group)
										.append(
												"<div class='panel-body concrete-friend-list'  hidden ></div>");
								$("#" + shift_group).find(
										".concrete-friend-list").show();

								var img = $("#" + shift_group)
										.find(".flex-img");
								// 把箭头放出来
								img.attr("src", downImg);
								$("#" + shift_group).find(".flex").addClass(
										"activate");
							} else if ($("#" + shift_group).children().size() != 1) {
								$("#" + shift_group + " .concrete-friend-list")
										.append(
												"<hr style='border-bottom:2px solid red;'/>");

							}
							;
							$("." + friend_id).clone(true).appendTo(
									"#" + shift_group
											+ " .concrete-friend-list");
							$(
									"#" + shift_group
											+ " .concrete-friend-list ."
											+ friend_id).attr("group-id",
									shift_group);

							// 判断是不是该分组第一个好友
							if ($(
									"." + friend_id + "[group-id="
											+ affiliation_group + "]").prev(
									"hr").size() == 0) {
								$(
										"." + friend_id + "[group-id="
												+ affiliation_group + "]")
										.next("hr").remove();
							} else {
								$(
										"." + friend_id + "[group-id="
												+ affiliation_group + "]")
										.prev("hr").remove();
							}
							$(
									"." + friend_id + "[group-id="
											+ affiliation_group + "]").remove();

							// 该分组的好友都转移完了
							if ($($("#" + affiliation_group).children().get(1))
									.children().size() == 0) {
								$($("#" + affiliation_group).children().get(1))
										.remove();
								// 把箭头变回来
								var group = $("#" + affiliation_group);
								var img = group.find(".flex-img");
								// 把箭头收回去
								img.attr("src", rightImg);
								group.removeClass("activate");
							}
							$("#change-group").css("visibility", "hidden");
						} else {
							alert(data);
						}
						;
					});
}

// 要保证最少有一个分组
var group_menu = {
	items : [
			{
				header : '分组管理'
			},
			{
				divider : true
			},
			{
				text : '增加分组',
				onclick : function(e) {
					var newGroupName = prompt("请输入新的分组名称", "");
					if (newGroupName.length < 2 || newGroupName.length > 6) {
						alert("分组名称长度为2-6");
						return false;
					}
					;
					var addFriendGroupUrl = getCookie("addFriendGroupUrl");
					$.get(addFriendGroupUrl, {
						"newGroupName" : newGroupName
					}, function(data) {
						if (data.length == 32) {
							alert("分组添加成功！");
							$(".group:last").clone(true).insertAfter(
									".group:last");
							$(".group-name:last").text(newGroupName);
							var imgRightUrl = $("#imgRightUrl").text();
							$(".group:last").find("img").attr("src",
									imgRightUrl);
							// 新分组无人
							$($(".group:last").children().get(1)).remove();
							// 调整分组间隔
							$(".group:last").addClass("adjust");
							// 设置新id
							$(".group:last").attr("id", data);

							$(".group:last").attr("group-id", data);
							$(".group:last *").attr("group-id", data);
							$($("select").children().get(0)).clone(true)
									.appendTo("select");
							$("select option:last").val(data);
							$("select option:last").text(newGroupName);
						} else {
							alert("添加失败！");
						}
						;
					});

				}
			},
			{
				text : '删除分组',
				onclick : function(e) {
					var r = confirm("确定要删除吗？");
					if (r == true) {
						// 要删除的分组id
						var group_id = $("#right-hand-group").text();
						// 得到好友要转移到的分组id,如果有好友。
						var first_group_id = $(
								$("#" + group_id).siblings("div").get(0)).attr(
								"id");
						// 转移到的分组名
						var newGroupName = $(
								"#" + first_group_id + " .group-name").text();
						var friend_divs = $($("#" + group_id).children().get(1))
								.children("div");
						var friend_id_size = friend_divs.size();
						var friend_id_array = new Array();
						var deleteFriendGroupUrl = getCookie("deleteFriendGroupUrl");
						if ($("#" + group_id).siblings("div").size() == 0) {
							alert("就只剩下一个分组了，不能删除！");
							return false;
						}
						// 要删除的分组有好友,先更新好友所在分组
						if (friend_id_size != 0) {
							for (var i = 0; i < friend_id_size; i++) {
								friend_id_array[i] = $(friend_divs.get(i))
										.attr("class");
							}
							var friend_ids = friend_id_array.join();
							var updateFriendGroupListUrl = getCookie("updateFriendGroupListUrl");
							$
									.get(
											updateFriendGroupListUrl,
											{
												"friendsId" : friend_ids,
												"affiliation_group" : first_group_id
											},
											function(data) {
												if (data.indexOf("success") != -1) {
													// 判断要转移到的分组是不是新建的分组且这个将要删除的分组有好友存在
													if ($("#" + first_group_id)
															.children().size() == 1
															&& $("#" + group_id)
																	.children()
																	.size() != 1) {
														$("#" + first_group_id)
																.append(
																		"<div class='panel-body concrete-friend-list'  hidden ></div>");
													} else if ($("#" + group_id)
															.children().size() != 1) {
														// 有好友
														$(
																"#"
																		+ first_group_id
																		+ " .concrete-friend-list")
																.append("<hr/>")
													}
													// 移除好友,在这之前要判断有没有好友
													if ($("#" + group_id)
															.children().size() != 1) {
														// 也就是有好友在这个分组
														$(
																$(
																		$(
																				"#"
																						+ group_id)
																				.children()
																				.get(
																						1))
																		.children())
																.appendTo(
																		"#"
																				+ first_group_id
																				+ " .concrete-friend-list");
													}

													$
															.get(
																	deleteFriendGroupUrl,
																	{
																		"group_id" : group_id
																	},
																	function(
																			data) {
																		if (data
																				.indexOf("成功") != -1) {
																			alert("删除成功！该分组下所有的好友将移到:"
																					+ newGroupName);
																			// 删除的是最顶部的分组
																			if ($(
																					"#"
																							+ group_id)
																					.hasClass(
																							"adjust") == false) {
																				var next_id = $(
																						"#"
																								+ group_id
																								+ " + "
																								+ "div")
																						.attr(
																								"group-id");
																				$(
																						"#"
																								+ next_id)
																						.removeClass(
																								"adjust");
																			}
																			// 通过元素删除该分组
																			$(
																					"#"
																							+ group_id)
																					.remove();
																		} else {
																			alert(data);
																		}
																		;
																	});
												} else {
													alert(data);
													return false;
												}
											});
						} else {
							// 如果不在else，那么就会出现分组还没更新玩就删除分组的错误
							$.get(deleteFriendGroupUrl, {
								"group_id" : group_id
							},
									function(data) {
										if (data.indexOf("成功") != -1) {
											alert("删除成功！该分组下所有的好友将移到:"
													+ newGroupName);
											// 删除的是最顶部的分组
											if ($("#" + group_id).hasClass(
													"adjust") == false) {
												var next_id = $(
														"#" + group_id + " + "
																+ "div").attr(
														"group-id");
												$("#" + next_id).removeClass(
														"adjust");
											}
											// 通过元素删除该分组
											$("#" + group_id).remove();
										} else {
											alert(data);
										}
										;
									});
						}

						// 从select中移除
						$("select option[value='" + group_id + "']").remove();
					}

				}
			},
			{
				text : '修改分组',
				onclick : function(e) {
					var group_id = $("#right-hand-group").text();
					var newGroupName = prompt("请输入新的分组名称", "");
					if (newGroupName.length < 2 || newGroupName.length > 6) {
						alert("分组名称长度为2-6");
						return false;
					}
					;
					var updateGroupUrl = getCookie("updateGroupUrl");
					$.get(updateGroupUrl, {
						"newGroupName" : newGroupName,
						"group_id" : group_id
					}, function(data) {
						if (data.indexOf("成功") != -1) {
							alert("更新成功！");
							$("#" + group_id + " .group-name").text(
									newGroupName);

							$("select option[value='" + group_id + "']").text(
									newGroupName);

						} else {
							alert(data);
						}
						;
					});

				}
			}, {
				divider : true
			}, {
				header : 'ManicChat',
				href : '#'
			} ],
	menuId : "group_menu"
}

// 聊天右键菜单
var chat_friend_menu = {
	items : [ {
		header : '聊天管理'
	}, {
		divider : true
	}, {
		text : '删除聊天',
		onclick : function(e) {
			var friend_id = $("#right-hand-chat-friend").text();
//			alert(friend_id);
			if ($(".chat-" + friend_id).prev("hr").size() == 0) {
				$(".chat-" + friend_id).next("hr").remove();
			} else {
				$(".chat-" + friend_id).prev("hr").remove();
			}
			$(".chat-" + friend_id).remove();
			$("#chat-from").children().remove();
			$("#chat-nickname").text("欢迎使用ManicChat");
			$("#chat-nickname").attr("title", "");
			$("#sendMsg").attr("disabled", "disabled");
			clearCookie(friend_id);
		
			deleteChatListById(friend_id);
		}
	}, {
		text : '邀请群聊',
		onclick : function(e) {
			alert("后面实现");
		}
	}, {
		divider : true
	}, {
		header : 'ManicChat',
		href : '#'
	} ],
	menuId : "group_menu"
};

function startChat(event) {
	var friend_id = $(event.target).attr("id");
	var nickname = $(event.target).text();
	var currentChatUser = $("#chat-nickname").attr("title");
	addFriendToChatList(nickname, friend_id, true);
	saveChatList(nickname, friend_id, true);

	if (friend_id != currentChatUser) {
		$("#chat-from").children().remove();
		// 设置新聊天
		$("#chat-nickname").text(nickname);
		$("#chat-nickname").attr("title",friend_id);
		//循环聊天记录
		showChatContentById(friend_id);
	}
	if($(event.target).hasClass("button-primary")){
		online(friend_id);
	}
	clearChatUnreadNum(friend_id);
	$("#sendMsg").removeAttr("disabled");
	$("#chat-from").scrollTop(1000000);
	$("#msgContent").focus();
}

function setChatFriend(event) {
	var btnNum = event.button;
	if (btnNum == 2) {
		$("#right-hand-chat-friend").text($(event.target).attr("chat-id"));
	}
}

function changeChat(event) {
	$("#sendMsg").removeAttr("disabled");
	
	var currentChatUser = $("#chat-nickname").attr("title");
	var friend_id = $(event.target).attr("chat-id");
	var nickname = $(event.target).text();
	if (friend_id != currentChatUser) {
		$("#chat-nickname").text(nickname);
		$("#chat-nickname").attr("title", friend_id);
		$("#chat-from").children().remove();
		clearChatUnreadNum(friend_id);
		showChatContentById(friend_id);
	}

	$("#msgContent").focus();
	$("#chat-from").scrollTop(1000000);
}

function setListFriendId(event) {
	var btnNum = event.button;
	if (btnNum == 2) {
		$("#right-hand-friend").text($(event.target).attr("id"));
	}
}

function setListGroupId(event) {
	var btnNum = event.button;
	if (btnNum == 2) {
		$("#right-hand-group").text($(event.target).attr("group-id"));
	}
}

function flexGroup(event, imgDownUrl) {
	var imgRightUrl = $("#imgRightUrl").text();
	var group = $(event.target);
	var img;
	var friendList;

	if (group.hasClass("flex")) {
		// 点到了.flex-DIV上
		img = group.find("img");
		friendList = group.siblings("div");
	} else {
		// 点到的是h3或其子元素
		group = group.parents(".flex");
		img = group.find("img");
		friendList = group.siblings("div");

	}
	if (group.hasClass("activate")) {
		// 收回去
		img.attr("src", imgRightUrl);
		group.removeClass("activate");
		$(friendList).hide();
	} else {
		img.attr("src", imgDownUrl);
		group.addClass("activate");
		$(friendList).show();
	}

}
//onchange="changeBackground(this)"
function changeBackground(file) {
	// 将img的src从路径变为数据显示
	if (file.files && file.files[0]) {
		var reader = new FileReader();
		reader.onload = function(evt) {
			// img.src = evt.target.result;
			$("body").css("background-image", "url(" + evt.target.result + ")");

		};
		reader.readAsDataURL(file.files[0]);
	}
}

// 创建websocket
// 1.双击好友的时候开始聊天 2.通过useryt判断当前登录的是普通用户还是客服，如果是客服则添加到该公司的map中
function openConnect(userid,usertype,affiliation_user) {
	var wsUserid = getCookie("wsUserid");
	if (userid == wsUserid && userid != "") {
//		 alert("正确");
		if ('WebSocket' in window) {
			socket = new WebSocket(
					'ws://haihihi.cn/createWebsocket.ws');

		}
		socket.onopen = function() {
			// 连接打开成功之后发送当前账号的所有好友id，用于判断这些好友是否在线
			if(getAllFriendId()!=""){
				//alert(getAllFriendId())
//				socket.send("notifyOnline:" + getAllFriendId());
				socket.send("{'msgType':'notifyOnline','msgContent':'"+ getAllFriendId() +"'}");
				
			}
			
			if(usertype==2 && affiliation_user!=0){
				var objMsg = {"serviceId":userid,"bossId":affiliation_user};
				sendMessage("serviceOnline", JSON.stringify(objMsg));
				
			}
		};
		socket.onmessage = function(evt) {
			var received_msg = evt.data;
			//received_msg = received_msg.replace(/'/g, "\"");
			
			//alert(received_msg)
			
			var msgObj = JSON.parse(received_msg);
//			alert(msgObj)
			var msgType = msgObj.msgType;
			var msgContent = msgObj.msgContent;
//			alert(msgType + "    " + msgContent);
			switch (msgType) {
				case "isOnline":
					isOnline(msgContent);
					break;
				case "online":
					online(msgContent);
					break;
				case "offline":
					offline(msgContent);
					break;
				case "deleteFriend":
					deletedFriendById(msgContent);
					break;
				case "newFriend":
//				alert(msgContent.userid)
					newFriendAddOnline(msgContent);
					break;
				case "imageMsg":
					showAndSaveImg(msgContent);
					break;
				case "fileMsg":
					showAndSaveFile(msgContent);
					break;
				case "clientOnline":
					var clientId = msgContent.clientId;
					var nickname = decodeURIComponent(msgContent.nickname);
					addFriendToChatList(nickname, clientId, false);
					saveChatList(nickname, clientId, false);
					setCookie("client"+clientId, JSON.stringify(msgContent), 1);//保存到cookie以便在新窗口查看用户信息
					break;
				case "textChat":
				//	alert("userid::::::" + userid);
					showReceiveMsg(msgContent, userid);
					break;
				case "noOnline":
					$("#chat-from").append("<p style='color: red;'>"+ msgContent +"<p>");
					break;
				default:
				//	showReceiveMsg(received_msg, userid);
					break;
			}
/*			var friendid = "";
			if (received_msg.indexOf("isOnline") != -1) {//好友已经在线
				isOnline(received_msg);
			} else if (received_msg.indexOf("online") != -1) {//好友上线
				friendid = received_msg.split(":")[1];
				online(friendid);
			} else if (received_msg.indexOf("offline") != -1) {//好友下线
				friendid = received_msg.split(":")[1];
//				alert("好友下线:" + friendid);
				offline(friendid);
			} else if (received_msg.indexOf("deleteFriend") != -1) {//被删除
				var friend_id = received_msg.split(":")[1];
//				alert("从当前列表删除该好友:" +friend_id)
				deletedFriendById(friend_id);
			}else if(received_msg.indexOf("newFriend") != -1){//有人添加我
				newFriendAddOnline(received_msg);
			}else if(received_msg.indexOf("newAddFriendOnline") != -1){//新添加的好友在线上
				online(received_msg.split(":")[1]);
			}else if(received_msg.indexOf("imageMsg") != -1){//收到图片
				var dataStr = JSON.stringify(received_msg);
				var dataObj = JSON.parse(dataStr);//JSON.parse(received_msg.substring(9));
				alert(dataObj.msgType)
			//	showAndSaveImg(dataObj);
			}else if(received_msg.indexOf("fileMsg") != -1){//收到文件
				var dataObj = JSON.parse(received_msg.substring(8));
				showAndSaveFile(dataObj);
			}else if(received_msg.indexOf("clientOnline") != -1){//客户上线
				var dataObj = JSON.parse(received_msg.substring(13));
				var clientId = dataObj.clientId;
				var nickname = decodeURIComponent(dataObj.nickname);
				addFriendToChatList(nickname, clientId, false);
				saveChatList(nickname, clientId, false);
				
				//保存到cookie以便在新窗口查看用户信息
				setCookie("client"+clientId, JSON.stringify(dataObj), 1);
//				alert(123)
			}else {
				//正常通信
				showReceiveMsg(received_msg, userid);
			}*/

		};
		socket.onclose = function(e) {
			alert("连接断开，请刷新页面");
		};
		socket.onerror = function(e) {
			alert("连接错误，请重新打开或刷新页面");
		};
	} else {
		alert("请不要随便修改cookie！");
	}

}

function deletedFriendById(friend_id){

	if ($("." + friend_id).prev("hr").size() == 0) {
		$("." + friend_id).next("hr").remove();
	} else {
		$("." + friend_id).prev("hr").remove();
	}
	// 通过判断同级元素可以得到当前分组是不是已经没有人了
	if ($("." + friend_id).siblings("div").size() == 0) {
		var group = $("." + friend_id).parent(
				".concrete-friend-list").siblings("div");
		var img = group.find(".flex-img");
		var imgRightUrl = $("#imgRightUrl").text();
		// 把箭头收回去
		img.attr("src", imgRightUrl);
		group.removeClass("activate");
		// 移除
		$("." + friend_id).parent(".concrete-friend-list").remove();
		$("." + friend_id).remove();
	} else {
		$("." + friend_id).remove();
	}
	//更新session中的好友列表以及socket中的列表
	$.get($("#updateFriendList").text());
//	socket.send("{updateAllFriendId:{friendIdsMap:[" + getAllFriendId() + "],friendId:" + 0 + ",username:"+ 0 + ",userid:" + 0 + "}}" );
	socket.send("{'msgType':'updateAllFriendId','msgContent':'"+ getAllFriendId() +"'}");
}

/**
 * 计算显示并保存未读信息数
 * @param sendUser 发送用户
 */
function countUnreadNum(sendUser){
	var chatUnread = getCookie("unread" + sendUser);
	if (chatUnread == "") {
		chatUnread = 1;

		$("#" + sendUser).parent("." + sendUser).append(
				"<span class='badge' style='float: right;'></span>");
		$("a[chat-id='" + sendUser + "']").parent("div").append(
				"<span class='badge' style='float: right;'></span>");
	} else {
		chatUnread = Number(chatUnread) + 1;
		if ($("#" + sendUser).parent("." + sendUser).children().size() == 1) {
			$("#" + sendUser).parent("." + sendUser).append(
					"<span class='badge' style='float: right;'></span>");
			$("a[chat-id='" + sendUser + "']").parent("div").append(
					"<span class='badge' style='float: right;'></span>");
		}
	}
	$("#" + sendUser).siblings(".badge").text(chatUnread);
	$("a[chat-id='" + sendUser + "']").siblings(".badge").text(chatUnread);
	setCookie("unread" + sendUser, chatUnread, 1);
}


/**
 * 
 * @param msgData
 *            收到的信息数据
 * @param userid
 *            当前登录的userid
 */
function showReceiveMsg(msgData, userid) {
	var msgDataObj = msgData;
	var msgContent = msgDataObj.chat_content;
	var dateText = msgDataObj.chat_time;
	// 该条信息来自：chat_sendUser
	var sendUser = msgDataObj.chat_sendUser;
	var receiveUserNickanem = getNicknameById(sendUser);
	// 正在跟这个聊天
	var currentChatUser = $("#chat-nickname").attr("title");

	if (sendUser == currentChatUser) {
		$("#chat-from")
				.append(
						"<div class='left-bubble'><div class='chat-time'></div><pre class='from-left-content'></pre></div>");
		$(".from-left-content:last").text(decodeURIComponent(msgContent));
		$(".chat-time:last").text(dateText);
	} else {
		// 开始计数
		countUnreadNum(sendUser);
	}

	var userChatMsg = getCookie("chat" + sendUser);

	if (userChatMsg == "") {
		userChatMsg = {
			"chat_time" : [ dateText ],
			"chat_action" : [ "receive" ],
			"chat_content" : [ msgContent ],
			"chat_reception" : sendUser,
			"chat_sendUser" : userid
		};
		var jsonStr = JSON.stringify(userChatMsg);
		// setCookie(sendUser,jsonStr , 1);
	} else {
		var obj = JSON.parse(userChatMsg);
		obj.chat_time[obj.chat_time.length] = dateText;
		obj.chat_action[obj.chat_action.length] = "receive";
		obj.chat_content[obj.chat_content.length] = msgContent;
		jsonStr = JSON.stringify(obj);

	}
	$("#chat-from").scrollTop(1000000);
	setCookie("chat" + sendUser, jsonStr, 1);
	showNotifyMsgWindow(receiveUserNickanem, decodeURIComponent(msgContent));

}

function sendMsg(sendUserid) {

	var msgContent = $("#msgContent").val();
	var contentLength = msgContent.length;
	var currentChatUser = $("#chat-nickname").attr("title");
	// var currentChatUserNickname = getNicknameById(sendUserid);
	if (contentLength == 0) {
		alert("请输入内容!");
		return false;
	} else if (contentLength > 128) {
		alert("内容过长，请保持在128字以内。当前长度：" + contentLength);
		return false;
	};
	//判断当前好友是否已经下线
	/*if($("#" + currentChatUser).hasClass("button-primary")==false){
		alert("好友不在线,不能发送消息！");
		return false;
	}*/
	
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
	var userChatMsg = getCookie("chat" + currentChatUser);
	var jsonStr = "";
	if (userChatMsg == "") {
		// 转换成字符串，不然会取不出来
		userChatMsg = {
			"chat_time" : [ dateText ],
			"chat_action" : [ "send" ],
			"chat_content" : [ encodeURIComponent(msgContent) ],
			"chat_reception" : currentChatUser,
			"chat_sendUser" : sendUserid,

		};
		jsonStr = JSON.stringify(userChatMsg);
		// setCookie(currentChatUser, jsonStr, 1);

	} else {
		var obj = JSON.parse(userChatMsg);
		obj.chat_time[obj.chat_time.length] = dateText;
		obj.chat_action[obj.chat_action.length] = "send";
		obj.chat_content[obj.chat_content.length] = encodeURIComponent(msgContent);
		// obj.chat_reception[obj.chat_reception.length] = currentChatUser;
		jsonStr = JSON.stringify(obj);

	}
	var sendMsgContent = {
		"chat_time" : dateText,
		"chat_action" : "send",
		"chat_content" : encodeURIComponent(msgContent),
		"chat_reception" : currentChatUser,
		"chat_sendUser" : sendUserid
	};
//	sendMsgContent = JSON.stringify(sendMsgContent);
	setCookie("chat" + currentChatUser, jsonStr, 1);
	//socket.send(sendMsgContent);
	sendMessage("textChat", JSON.stringify(sendMsgContent));
	$("#chat-from").scrollTop(1000000);
}
/**
 * 通过接收到的信息得到该id的昵称或备注
 */
function getUserNicknameById() {

}

function showNotifyMsgWindow(sendUser, content) {
	var position = 3;
	var userHeadUrl = $("#userHeadUrl").text();
	/* 暂时通知激活 */
	Notification.create(
	// 消息通知框的标题
	sendUser,
	// 消息通知框的内容
	content,
	// 图片
	userHeadUrl,
	// 效果
	'rollIn',
	// 定位
	position);
}

function getNicknameById(userid) {
	var aList = $("#friend-list").find("a");
	var friendListSize = $("#friend-list").find("a").size();
	var nickanme = "";
	for (var i = 0; i < friendListSize; i++) {
		var aId = $(aList.get(i)).attr("id");
		nickanme = $(aList.get(i)).text();
		if (aId == userid) {
			return nickanme;
		}
	}
}

function online(userid) {
	
	$("#" + userid).attr("class",
			"button button-pill button-primary button-tiny friend-manage");
	$("a[chat-id="+ userid +"]").attr("class","button button-pill button-primary button-tiny friend-manage");
}

function offline(userid) {
	$("#" + userid).attr("class",
			"button button-pill button-tiny friend-manage");
	$("a[chat-id="+ userid +"]").attr("class","button button-pill button-tiny friend-manage");
}

function isOnline(userids) {
	// alert(userids)
	//var ids = userids.split(":")[1].replace("\[", "").replace("\]", "").split(",");
	for (var i = 0; i < userids.length; i++) {
		//var userid = ids[i].replace(" ", "");
	//	alert(userids.length);
		online(userids[i]);
	}
}

function getAllFriendId() {
	// 好友所处分组
	var friendList = $(".concrete-friend-list");
	var friendIdS = new Array();
	var index = 0;
	for (var i = 0; i < friendList.size(); i++) {
		var aList = $(friendList.get(i)).find("a");
		for (var j = 0; j < aList.size(); j++) {
			friendIdS[index] = $(aList.get(j)).attr("id");
			index++;
		}

	}
	return friendIdS;
}
/**
 * 
 * @param nickname 昵称
 * @param fid 好友id或客户id
 * @param friendType true表示普通好友，false表示客户
 * @returns 返回一个布尔值，为true则已经在列表，
 */
function addFriendToChatList(nickname,fid,friendType){
	
	var chat_friend_list = $("#chat-friend-list").children("div");
	var isAddToList = true;
	// 判断是不是已经在列表了
	for (var i = 0; i < chat_friend_list.size(); i++) {
		if ($(chat_friend_list.get(i)).hasClass("chat-" + fid)) {
			isAddToList = false;
			break;
		}
	}
	if (isAddToList) {
		
		// 通过判断同级元素可以得到是不是要添加hr
		if (chat_friend_list.size() != 0) {
			$("#chat-friend-list").append(
					"<hr style='border-bottom:2px solid #9999FF;'/>");
		}
		if(friendType){
			$("#chat-friend-list").append("<div class='chat-"+ fid +"'><a class='button button-pill button-tiny chat-right-manage' onmousedown='setChatFriend(event)' data-toggle='tooltip' data-placement='left' title='id:  "+fid+"'  data-contextify-id='3' onclick='changeChat(event)' chat-id='"+ fid +"'>"+ nickname+"</a></div>");
		}else{
			$("#chat-friend-list").append("<div class='chat-"+ fid +"'><a class='button button-highlight button-pill button-tiny chat-right-manage' onmousedown='setChatFriend(event)' data-toggle='tooltip' data-placement='left' title='id:  "+fid+"' data-contextify-id='3' onclick='changeChat(event)' chat-id='"+ fid +"'>"+ nickname+"</a></div>");
		}
		$('.chat-right-manage').contextify(chat_friend_menu);// 弹出右键聊天好友菜单
	}
	return isAddToList;
}
/**
 * 
 * @param nickname 昵称
 * @param friend_id 好友id
 * @param affiliation_group 所处分组
 * @param friendType true是好友  false 是客服
 */
function addFriendToList(friend_cname,friend_id,affiliation_group,friendType){

	// 当前分组有好友
	if ($("#" + affiliation_group).children().size() != 1) {
		$(
				"#" + affiliation_group
						+ " .concrete-friend-list")
				.append(
						"<hr style='border-bottom:2px solid red;'/>");
	}else{
		$("#" + affiliation_group)
		.append(
				"<div class='panel-body concrete-friend-list'  hidden ></div>");
	}
	
	$(
		"#" + affiliation_group
				+ " .concrete-friend-list")
		.append(
				"<div class='" + friend_id
						+ "' group-id='"
						+ affiliation_group
						+ "'></div>");
	$(
		"#" + affiliation_group
				+ " .concrete-friend-list ."
				+ friend_id)
		.append(
				"<a class='button button-pill button-tiny friend-manage' onmousedown='setListFriendId(event)' ondblclick='startChat(event)'  id='"
						+ friend_id
						+ "' title='id: "
						+ friend_id
						+ "' style='border-bottom: solid 2px #0099FF;'>"
						+ friend_cname + "</a>");
	
	if(friendType==false){
		$("#" + friend_id).css("border-bottom","solid 2px #9900CC");
		$("#" + friend_id).attr("class","button button-pill button-tiny friend-manage service");
	}
		
	
	$('.friend-manage').contextify(friend_menu);// 弹出右键好友菜单
}
//保存聊天列表
function saveChatList(nickname,fid,friendType){
	var userChatList = getCookie(getCookie("wsUserid") + "chatList");
	var chatListObj;
	
	if(userChatList != ""){
		if(userChatList.indexOf(fid) !=-1) return false;
		chatListObj = JSON.parse(userChatList);
		chatListObj.nicknames[chatListObj.nicknames.length] = encodeURIComponent(nickname);
		chatListObj.fids[chatListObj.fids.length] = fid;
		chatListObj.friendTypes[chatListObj.friendTypes.length] = friendType;
	}else{
		chatListObj = {"nicknames":[encodeURIComponent(nickname)],"fids":[fid],"friendTypes":[friendType]};
	}
	setCookie(getCookie("wsUserid") + "chatList", JSON.stringify(chatListObj), 1);
	
}
//恢复聊天列表
function recoverChatList(){
	var userChatList = getCookie(getCookie("wsUserid") + "chatList");
	var chatListObj;
	if(userChatList != ""){
		chatListObj = JSON.parse(userChatList);
		var listSize = chatListObj.nicknames.length;
		for(var i = 0; i < listSize; i++){
			addFriendToChatList(decodeURIComponent(chatListObj.nicknames[i]), chatListObj.fids[i], chatListObj.friendTypes[i]);
		}
	}
}
//从聊天列表中删除
function deleteChatListById(fid){
	var userChatList = getCookie(getCookie("wsUserid") + "chatList");
	var chatListObj;
	
	if(userChatList != ""){
		chatListObj = JSON.parse(userChatList);
		var listSize = chatListObj.nicknames.length;
		var newChatListObj = {"nicknames":[],"fids":[],"friendTypes":[]};
		for(var i = 0; i < listSize; i++){
			
			if(chatListObj.fids[i] != fid){
		//		alert(chatListObj.fids[i])
				if(newChatListObj.nicknames.length == 0){
					newChatListObj = {"nicknames":[chatListObj.nicknames[i]],"fids":[chatListObj.fids[i]],"friendTypes":[chatListObj.friendTypes[i]]};
				}else{
					newChatListObj.nicknames[newChatListObj.nicknames.length] = chatListObj.nicknames[i];
					newChatListObj.fids[newChatListObj.fids.length] = chatListObj.fids[i];
					newChatListObj.friendTypes[newChatListObj.friendTypes.length] = chatListObj.friendTypes[i];
				}
			}
		}
		alert(JSON.stringify(newChatListObj));
		setCookie(getCookie("wsUserid") + "chatList", JSON.stringify(newChatListObj), 1);
	}
}

function showChatContentById(fid){

	
	var cookieChatContent = getCookie("chat" + fid);
	//遍历内容
	if (cookieChatContent != "") {
		var obj = JSON.parse(cookieChatContent);
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
	
}

function clearChatUnreadNum(fid){
	var chatUnread = getCookie("unread" + fid);
	if (chatUnread != null) {
		$("#" + fid).siblings(".badge").remove();
		$("a[chat-id='" + fid + "']").siblings(".badge").remove();
		clearCookie("unread" + fid);
	}
}

function showAndSaveImg(dataObj){
	var fileUrl = dataObj.fileUrl;
	var dateText = dataObj.dateText;
	var friendId = dataObj.chat_sendUser;
	// 正在跟这个聊天
	var currentChatUser = $("#chat-nickname").attr("title");
	if(friendId == currentChatUser){
       $("#chat-from")
		.append(
				"<div class='left-bubble'><div class='chat-time'></div><a class='chatImga' title='双击在新窗口查看图片' target='_blank' href=''><img alt='聊天图片' src='' class='receiveChatImg'/></a></div>");
       $(".left-bubble:last a").attr("href", "/file/" + fileUrl);
       $(".left-bubble:last img").attr("src", "/file/" + fileUrl);
	   $(".chat-time:last").text(dateText);
	}else{
		countUnreadNum(friendId);
	}
    var userChatMsg = getCookie("chat" + friendId);
	var jsonStr = "";
	if (userChatMsg == "") {
		// 转换成字符串，不然会取不出来
		userChatMsg = {
			"chat_time" : [ dateText ],
			"chat_action" : [ "receiveImg" ],
			"chat_content" : [ encodeURIComponent(fileUrl) ],
			"chat_reception" : friendId,
			"chat_sendUser" : getCookie("wsUserid"),
		};
		jsonStr = JSON.stringify(userChatMsg);
	} else {
		var obj = JSON.parse(userChatMsg);
		obj.chat_time[obj.chat_time.length] = dateText;
		obj.chat_action[obj.chat_action.length] = "receiveImg";
		obj.chat_content[obj.chat_content.length] = encodeURIComponent(fileUrl);
		jsonStr = JSON.stringify(obj);
	}
	setCookie("chat" + friendId, jsonStr, 1);
	
	showNotifyMsgWindow(getNicknameById(friendId), "[有新图片]");
}

function newFriendAddOnline(msgContentObj){
	var affiliation_group = $($("div[group-id]").get(0)).attr("group-id");
	var userid = msgContentObj.userid;
	var nickname = msgContentObj.nickname;
	addFriendToList(nickname, userid, affiliation_group, true);
	//反向新添加的好友肯定在线
	online(friend_id);
	//更新session中的好友列表
	$.get($("#updateFriendList").text());
	//更新ws中的好友列表[这里不需要更新，在执行添加好友的]
	socket.send("{'msgType':'updateAllFriendId','msgContent':'"+ getAllFriendId() +"'}");
}

function showAndSaveFile(dataObj){
	var fileUrl = decodeURIComponent(dataObj.fileUrl).split("/")[0];
	var fileName = decodeURIComponent(dataObj.fileUrl).split("/")[1];
	var dateText = dataObj.dateText;
	var friendId = dataObj.chat_sendUser;
	
//	alert(fileName + "    " + fileUrl)
	// 正在跟这个聊天
	var currentChatUser = $("#chat-nickname").attr("title");
	
	if(friendId == currentChatUser){
		$("#chat-from")
		.append(
				"<div class='left-bubble'><div class='chat-time'></div><p class='from-left-content sendChatFile'>[文件类型]"+fileName+"<a title='点击保存文件'  href=''>保存文件</a></p></div>");
       $(".left-bubble:last a").attr("href", "/file/" + fileUrl);
	   $(".chat-time:last").text(dateText);
	}else{
		countUnreadNum(friendId);
	}
    var userChatMsg = getCookie("chat" + friendId);
	var jsonStr = "";
	if (userChatMsg == "") {
		// 转换成字符串，不然会取不出来
		userChatMsg = {
			"chat_time" : [ dateText ],
			"chat_action" : [ "receiveFile" ],
			"chat_content" : [ encodeURIComponent(fileUrl + "/" + fileName) ],
			"chat_reception" : friendId,
			"chat_sendUser" : getCookie("wsUserid"),
		};
		jsonStr = JSON.stringify(userChatMsg);
	} else {
		var obj = JSON.parse(userChatMsg);
		obj.chat_time[obj.chat_time.length] = dateText;
		obj.chat_action[obj.chat_action.length] = "receiveFile";
		obj.chat_content[obj.chat_content.length] = encodeURIComponent(fileUrl + "/" + fileName);
		jsonStr = JSON.stringify(obj);
	}
	
	setCookie("chat" + friendId, jsonStr, 1);
	showNotifyMsgWindow(getNicknameById(friendId), "[有新文件]");
}

function sendMessage(msgType,msgContent){
	socket.send("{\"msgType\":\""+ msgType +"\",\"msgContent\":"+ msgContent +"}");
}


function getServiceLink(usertype,userid){
	if (usertype != 1) {
		alert("当前登录的用户不是企业类型，不能进行客服添加！");
		return false;
	}else{
		prompt("将以下链接复制到你的网站任意位置即可与当前在线的客服进行聊天", "haihihi.cn/jsp/clientConnect.jsp?bossId=" + userid);
	}
}