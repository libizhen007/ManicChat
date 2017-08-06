/**
 * 一些常用工具函数
 */

/**
 * @returns 2017/5/4 16:03:20 格式
 */
function getLocalDateStr(){
	var nowDate = new Date();
	var dateStr = "";
	dateStr = nowDate.toLocaleString().split(" ")[0];
	dateStr = dateStr + " " + nowDate.toTimeString().split(" ")[0];
	return dateStr;
}

/**
 * 设置cookie，key使用当前客服的uid
 * @param cname cookie名称
 * @param cvalue cookie值
 * @param exdays 过期时间，1表示一天
 */
function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + "; " + expires;
}
/**
 * 获取cookie值
 * @param cname cookie名称
 * @returns 该cookie值
 */
function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ')
			c = c.substring(1);
		if (c.indexOf(name) != -1)
			return c.substring(name.length, c.length);
	}
	return "";
}
/**
 * 清空所有cookie
 */
function clearAllCoolie(){
	var keys=document.cookie.match(/[^ =;]+(?=\=)/g); 
	if (keys) { 
		for (var i = keys.length; i--;) {
			document.cookie=keys[i]+'=0;expires=' + new Date( 0).toUTCString() ;
		}
	} 
}

/**
 * 清除指定cookie值
 * @param cname cookie名称
 */ 
function clearCookie(cname) {
	setCookie(cname, "", -1);
}