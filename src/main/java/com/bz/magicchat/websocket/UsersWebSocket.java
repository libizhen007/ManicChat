package com.bz.magicchat.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.bz.magicchat.domain.Users;
import com.bz.magicchat.util.CommonUtils;

public class UsersWebSocket implements WebSocketHandler{
	/**
	 * 这个是并发的；之后查一下资料
	 */
	//已经登录的用户
	private ConcurrentHashMap<String,WebSocketSession> usersWebSocketMap;
	//好友列表
	private ConcurrentHashMap<String, String[]> friendIdsMap;
	//各个公司的在线客服列表
	private ConcurrentHashMap<String, ConcurrentHashMap<String,WebSocketSession>> serviceList;
	public void setServiceList(
			ConcurrentHashMap<String, ConcurrentHashMap<String, WebSocketSession>> serviceList) {
		this.serviceList = serviceList;
	}
	public void setFriendIdsMap(ConcurrentHashMap<String, String[]> friendIdsMap) {
		//System.out.println("setFriendIdsMap");
		this.friendIdsMap = friendIdsMap;
	}
	public void setUsersWebSocketMap(
			ConcurrentHashMap<String, WebSocketSession> usersWebSocketMap) {
		//System.out.println("setUsersWebSocketMap");
		this.usersWebSocketMap = usersWebSocketMap;
	}
//	private WebSocketSession session;
	/**
	 * 打开连接成功之后执行
	 * 
	 */
    @Override  
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {  
    	String usersid = getWsUserId(session);
        usersWebSocketMap.put(usersid, session);
    }  
 
    /**
     * 收到消息之后执行
     * @throws IOException 
     */
    @Override  
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> wsm) throws IOException {  
    	String msgData = wsm.getPayload()+"";
    	JSONObject jsonData = JSONObject.fromObject(msgData);
    	String msgType = jsonData.getString("msgType");
    	String msgContent = jsonData.getString("msgContent");
    	String[] friendIds;
    	String friend_id;
  //  	String userid = getCookieValue(session.getHandshakeHeaders().getFirst("cookie"),"wsUserid");
    	String userid = getWsUserId(session);
    	ConcurrentHashMap<String,WebSocketSession> services=null;
    	switch (msgType) {
	    	/**
			 * 装载好友列表map，而且当添加好友或者客服的时候要需要通过发送消息重新装载。功能：通知上线和下线...
			 */
			case "notifyOnline":
				friendIds = msgContent.split(",");
				List<String> onlineFriends = getOnlineUserid(userid,friendIds);
			//	friendIdsMap.put(getCookieValue(session.getHandshakeHeaders().getFirst("cookie"),"wsUserid"), friendIds);
				friendIdsMap.put(userid, friendIds);
	    		sendMessage(userid, userid, "isOnline", onlineFriends);
				break;
			/**
			 * 删除好友并反向通知删除
			 */
			case "deleteOffline":
				JSONObject offlineMsgJSON = JSONObject.fromObject(msgContent);
	    		friend_id = offlineMsgJSON.getString("friend_id");//被删除的id
	    		userid = offlineMsgJSON.getString("userid");//登录的id
	    		deleteOffline(userid, friend_id);
				break;
			/**
			 * 更新当前连接的所有好友列表
			 */
			case "updateAllFriendId":
				friendIds = msgContent.split(",");
	    		friendIdsMap.put(userid, friendIds);
				break;
			/**
			 * 添加好友
			 */
			case "newFriend":
				JSONObject updateMsgJSON = JSONObject.fromObject(msgContent);
				friend_id = updateMsgJSON.getString("friend_id");//被添加的好友id。如果这个好友在线则通知并添加到列表
	    		String nickname = updateMsgJSON.getString("nickname");
	    		String usersid = updateMsgJSON.getString("userid");//来自这个用户
	    		if(usersWebSocketMap.containsKey(friend_id)){	    			
	    			sendMessage(usersid, friend_id, "newFriend", "{\"userid\":"+ usersid +",\"nickname\":\""+ nickname +"\"}");
	    			sendMessage(usersid, usersid, "online", friend_id);
	    		}
				break;
			/**
			 * 图片消息
			 */
			case "imageMsg":
	    		JSONObject dataJSON = JSONObject.fromObject(msgContent);
	    		friend_id = dataJSON.getString("friendId");
	    		sendMessage(userid, friend_id, "imageMsg", msgContent);
	    		break;
	    	/**
	    	 * 文件消息
	    	 */
			case "fileMsg":
	    		dataJSON = JSONObject.fromObject(msgContent);
	    		friend_id = dataJSON.getString("friendId");
	    		sendMessage(userid, friend_id, "fileMsg", msgContent);
	    		break;
	    	/**
			 * 客服上线添加
			 */
			case "serviceOnline":
				dataJSON = JSONObject.fromObject(msgContent);
				String serviceId = dataJSON.getString("serviceId");
	    		String bossId = dataJSON.getString("bossId");
	    		services = serviceList.get(bossId);
	    		if(services==null){
	    			services = new ConcurrentHashMap<String,WebSocketSession>();
	    		}
	    		services.put(serviceId, session);
	    		serviceList.put(bossId, services);
	    		break;
	    	/**
			 * 客户上线： 判断当前访问的公司是否有客服在线，如果在线则随机选取一个客服为其服务。
			 */
			case "clientOnline":
	    		dataJSON = JSONObject.fromObject(msgContent);
	    		bossId = dataJSON.getString("bossId");
	    		if(serviceList.containsKey(bossId) && serviceList.get(bossId).size()>0){
	    			services = serviceList.get(bossId);
	    			String serviceKey = (String) services.keySet().toArray()[CommonUtils.getRandomNumber(services.size())];
	//    			WebSocketSession serviceSession = services.get(serviceKey);
	    			
	    			sendMessage(userid, serviceKey,"clientOnline", msgContent);
	    			//向客户通知有客服在线
	    			sendMessage(userid,userid, "online", serviceKey);
	    		}else{
	    			sendMessage(userid, userid, "noService", "0");
	    		}
	    		break;
	    	/**
	    	 * 文本消息
	    	 */
			case "textChat":
				sendMessageToId(msgContent,userid);
				break;
			default:
				break;
		}

       
    }  
  
    @Override
    public void handleTransportError(WebSocketSession session, Throwable thrwbl) throws Exception {  
        if(session.isOpen()){  
        	session.close();  
//            String usersid = getCookieValue(session.getHandshakeHeaders().getFirst("cookie"),"wsUserid");
        	String usersid = getWsUserId(session);
            usersWebSocketMap.remove(usersid);
        }  
        
    }  
  
   
    @Override  
    public void afterConnectionClosed(WebSocketSession session, CloseStatus cs) throws Exception {
  //  	String usersid = getCookieValue(session.getHandshakeHeaders().getFirst("cookie"),"wsUserid");
    	String usersid = getWsUserId(session);
    	if(session.isOpen()){ 
    		session.close();  
        }
    	/**
    	 * 如果当前登录的不是客户端则通知所有在线的好友我已经下线,判断friendIdsMap是否存在
    	 * && friendIdsMap.containsKey(usersid)这个条件可以省略，省略的情况下表示该客服没有好友，不过不显示。客服应该不能删除上司
    	 */
    	if(Pattern.compile("[a-zA-Z]").matcher(usersid).find() == false && friendIdsMap.containsKey(usersid)){
    		offlineNotifyFriends(usersid,friendIdsMap.get(usersid));//客服
    		
 //   		String bossId = getCookieValue(session.getHandshakeHeaders().getFirst("cookie"),"bossId");
    		Users user = (Users)session.getAttributes().get("user");
    		String bossId = user.getAffiliation_user()+"";
    		ConcurrentHashMap<String, WebSocketSession> services = serviceList.get(bossId);
    		if(services != null && services.containsKey(usersid)){
    			services.remove(usersid);//通过公司遍历客服下线
    			//System.out.println("客服下线");
    			if(services.size()<=0) serviceList.remove(bossId);
    		}
    		friendIdsMap.remove(usersid);
    	}
    	/**
    	 * 移除客户session
    	 */
		usersWebSocketMap.remove(usersid);

    }
  
    @Override   
    public boolean supportsPartialMessages() {  
        return false;  
    }
    
    /**
     * 通过cookie得到当前要进行连接的userid
     */
    public String getCookieValue(String cookies,String cookieNmae){
    	
    	String[] concreteCookies = cookies.split(";");
    	String cname = "";
		String cvalue = "";
    	for(String cookie : concreteCookies){
    		if(cookie.indexOf("=")==-1) continue;
    		
    		cname = cookie.split("=")[0];
    		cvalue = cookie.split("=")[1];
    		
    		if(cname.trim().equals(cookieNmae)){
    			break;
    		}else{
    			cvalue = "";
    		}
    	}
    	return cvalue;
    }
    
    /**
     * 
     * @param msgData 一个websocket收到的信息，信息格式为json。通过解析得到发送
     */
    public void sendMessageToId(String msgContent,String sendUserid){

    	String chat_reception = JSONObject.fromObject(msgContent.toString()).getString("chat_reception");
    	String returnMessage = msgContent.replaceFirst("send", "receive");
    	
    	//WebSocketSession session = usersWebSocketMap.get(chat_reception);
    	try {
 //   		if(session !=null) sendMessage(session, "textChat", returnMessage);
    		sendMessage(sendUserid, chat_reception, "textChat", returnMessage);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
	
    /**
     * 获得在线上的好友id
     * @param excUsersid 排除的id
     * @return 当前账号的在线好友id
     * @throws IOException 
     */
    public List<String> getOnlineUserid(String excUsersid,String[] friendIds) throws IOException{
    	List<String> userids = new ArrayList<String>();
   
    	for(int i=0; i<friendIds.length; i++){
    		if(friendIds[i]!=excUsersid && usersWebSocketMap.containsKey(friendIds[i])){
    			userids.add(friendIds[i]);
    			sendMessage(excUsersid, friendIds[i], "online", excUsersid);
    			
    		}
    	}

    	return userids;
    }

    public void offlineNotifyFriends(String usersid,String[] friendIds) throws IOException{
    	for(int i= 0; i < friendIds.length; i++){

    		sendMessage(usersid, friendIds[i], "offline", usersid);
    	}
    }
    /**
     * 反向删除好友
     * @param offlineId 要删除的id
     * @param forUserid 从哪个登录的用户删除
     * @throws IOException
     */
    public void deleteOffline(String offlineId,String forUserid) throws IOException{
    	sendMessage(offlineId, forUserid, "deleteFriend", offlineId);
    }
    

    public void sendMessage(String sendUserid,String receiveUserid,String msgType,Object msgConte) throws IOException{
    	WebSocketSession sendSession = usersWebSocketMap.get(sendUserid);
    	WebSocketSession receiveSession = usersWebSocketMap.get(receiveUserid);
    	if(receiveSession==null){//好友没有在线
    		if(msgType.equals("offline")==false) sendSession.sendMessage(new TextMessage("{\"msgType\":\"noOnline\",\"msgContent\":\"当前用户不在线......\"}"));
    		
    	}else{
    		receiveSession.sendMessage(new TextMessage("{\"msgType\":\""+  msgType +"\",\"msgContent\":"+ msgConte +"}"));
    	}
    	
    }

    public String getWsUserId(WebSocketSession session){
    	Users user = (Users)session.getAttributes().get("user");
    	if(user != null){
    		return user.getUserid()+"";
    	}else{
    		return (String) session.getAttributes().get("clientId");
    	}
    	
    }
}