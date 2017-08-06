<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>2048Game - BiZhen</title>
<style type="text/css">
td {
	text-align: center;
	min-width: 80px;
	height: 80px;
	position: relative;
	font-size:30px;
	border: 3px inset  #000;
	
}
table{
	/*如果可能，边框会合并为一个单一的边框。会忽略 border-spacing 和 empty-cells 属性。 */
	border-collapse: collapse;
	
/* 透明化 */
	-webkit-filter: opacity(60%); /* Chrome, Safari, Opera 支持*/
    filter: opacity(50%);
}
.red
{
	color:red;
}
.blue/*	未完善*/
{
	color:blue;
}
</style>
<script type="text/javascript" src="../js/jquery-1.7.2.js"></script>
<script type="text/javascript">
$(function(){
	$(window).keydown(function(event) {
		if(event.keyCode>=37 && event.keyCode <=40){
			removeColor();
		}
		//向左则以左边第一格为one
		switch (event.keyCode) {
			case 37:
				shift(getAllTids(1,4,1));
				randomAdd()
				break;
			case 38:
				shift(getAllTids(1,1,4));
				randomAdd();
				break;
			case 39:
				shift(getAllTids(4,4,-1));
				randomAdd();
				break;
			case 40:
				shift(getAllTids(13,1,-4));
				randomAdd();
				break;
			};			
		});
		
		//获得当前方向的所有tid数组 line_head:第一行(列)开始的第一格id数 如：up(["#t4","#t3","#t2","#t1"]); up(["#t8","#t7","#t6","#t5"]);；line_interval 行(列)的间距，如4 - 8；tid_interval tid间距
		function getAllTids(line_head,line_interval,tid_interval){
			var temp = line_head;
			var arrays = new Array(4);
			arrays[0] = getTids(temp,tid_interval);
			for(var i=1; i<4; i++){
				arrays[i] = getTids(temp + line_interval, tid_interval)
				temp = temp + line_interval;
			}
			return arrays;
		}
		
		//开始移动 是一个二维数组
		function shift(arrays){
			for(var i=0; i<arrays.length; i++){
				up(arrays[i])
			}
		}
		//获得一行(列)的tid；interval限度，向左移动则左边第一格为limit，interval间隔为左边和右边的差
		function getTids(limit,interval){
			var tids = new Array(4);
			var temp = limit;
			tids[0] = "#t" + limit;
			for(var i=1; i<tids.length; i++){
				tids[i] = "#t" + (temp + interval);
				temp = temp + interval;
			}
			return tids;
		}
		//更新移动
		function up(tids){
			var array = tids;
			for(var i = 0; i < array.length; i++){
				var tid = array[i];
				var val = "";
				if($(tid).text() == ""){
					if($(array[i + 1]).text()=="" && (i + 1) <=3){
						if($(array[i + 2]).text()=="" && (i + 2) <=3){
							if($(array[i + 3]).text()!="" && (i + 3) <=3){
								val = $(array[i + 3]).text();
								swop(tid,val,array[i + 3],"");
							}
						}else if($(array[i + 2]).text()!="" && (i + 2) <=3){
							val = $(array[i + 2]).text();
							swop(tid,val,array[i + 2],"");
						}
					}else if($(array[i + 1]).text()!="" && (i + 1) <=3){
						val = $(array[i + 1]).text();
						swop(tid,val,array[i + 1],"");
						if($(array[i + 2]).text()=="" && (i + 2) <=3){
							if($(array[i + 3]).text()!="" && (i + 3) <=3){
								val = $(array[i + 3]).text();
								swop(array[i + 1],val,array[i + 3],"");
							}
						}else if($(array[i + 2]).text()!="" && (i + 2) <=3){
							val = $(array[i + 2]).text();
							swop(array[i + 1],val,array[i + 2],"");
							////可以取消
							/*if($(array[i + 3]).text()!="" && (i + 3) <=3){
								val = $(array[i + 3]).text();
								swop(array[i + 2],val,array[i + 3],"");
							}*/
						}
					}
				}
			}
			//开始合并
			merge(array)
		}
		/* 交换 */
		function swop(new_tid,new_val,old_tid,old_val){
			
			$(new_tid).text(new_val);
			$(old_tid).text(old_val);
		}
		// 合并 
		function merge(array){
			var addendA;
			var addendB;
			var addendC;
			var sum;
			for(var i = 0; i < array.length; i++){
				addendA = Number($(array[i]).text())
				addendB = Number($(array[i+1]).text())
				if(addendA == addendB && addendA!=""){//!=0 也可以
					sum = addendA + addendB;
					swop(array[i],sum,array[i+1],"");
					setBlue(array[i],array[i+1]);
					addendC =  Number($(array[i-1]).text());
					if(sum == addendC){
						sum = sum + addendC;
						swop(array[i-1],sum,array[i],"");
						setBlue(array[i-1],array[i]);
					}
				}else if(addendA=="" && i+1<=3){
					addendC =  Number($(array[i-1]).text());
					if(addendB!="" && addendB == addendC && i-1 >= 0){
						sum = addendC + addendB;
						swop(array[i-1],sum,array[i+1],"");
						setBlue(array[i-1],array[i+1]);
						addendC =  Number($(array[i-2]).text());
						if(sum == addendC){
							sum = sum + addendC;
							swop(array[i-2],sum,array[i-1],"");
							setBlue(array[i-2],array[i-1]);
						}
					}else if(addendB!=""){
						if(addendC==""){
							swop(array[i-1],addendB,array[i+1],"");
						}else{
							swop(array[i],addendB,array[i+1],"");
						}
					}
				}
			}
		}
		//当移动的时候随机出现2或4
		function randomAdd(){
			var num = parseInt(Math.random()*4,10)+1;//出现2 or 4
			while(true){
				if(num == 2 || num == 4){
					break;
				}else{
					num = parseInt(Math.random()*4,10)+1;
				}
			}
			//得到数，随机出现在没有数字的位置
			var tnum = parseInt(Math.random()*16,10)+1;//1-16
			var tid = "#t" + tnum;
			while(true){
				if($(tid).text() == ""){
					$(tid).text(num)
					setRed(tid);
					break
				}else{
					tnum = parseInt(Math.random()*16,10)+1;
					tid = "#t" + tnum;
					//用于判断是不是满了
					if(isFull()){
						alert("试试上下左右移动一下.");					
						break;
					}
				}
			}
		}
		//游戏是否结束，为true表示结束
		function gameTheEnd(){
			
			
		}
		
		//如果全部都已经占满则返回true
		function isFull(){
			var tid = "";
			var yes = true;
			for(var i = 1; i<=16; i++){
				tid = "#t" + i;
				if($(tid).text()==""){
					yes = false;//表示还有空位
					break;
				}
			}
			return yes;
		}
		//设置红色属性
		function setRed(tid){
			$(tid).addClass("red")
		}
		//设置蓝色属性:N_tid;移除蓝色属性：O_tid
		function setBlue(n_tid,o_tid){
			$(o_tid).removeClass("blue");
			$(n_tid).addClass("blue");
		}
		//移除所有颜色
		function removeColor(){
			$("td").removeClass();//删除匹配元素的所有类
		}
		
	});
</script>
</head>
<body style="background-color: #CC9966;">

	<div style="padding: 200px 40%;">
		<table  >
			<tr>
				<td id="t1"></td>
				<td id="t2">2</td>
				<td id="t3"></td>
				<td id="t4"></td>
			</tr>
			<tr>
				<td id="t5">2</td>
				<td id="t6"></td>
				<td id="t7"></td>
				<td id="t8"></td>
			</tr>
			<tr>
				<td id="t9"></td>
				<td id="t10"></td>
				<td id="t11"></td>
				<td id="t12"></td>
			</tr>
			<tr>
				<td id="t13"></td>
				<td id="t14"></td>
				<td id="t15"></td>
				<td id="t16">2</td>
			</tr>
		</table>
	</div>
</body>
</html>