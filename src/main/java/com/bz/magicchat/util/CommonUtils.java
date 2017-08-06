package com.bz.magicchat.util;


import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Random;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.Document;

public class CommonUtils {
	
	public static Integer getRandomNumber(int interval){
		Random random = new Random();
		return random.nextInt(interval);
	}
	
	
	/**
	 * 
	 * @param sqlActionName 一个在xml文件中配置的sql语句元素名。
	 * @return 返回该sql语句
	 */
	public static String getSqlStatement(String sqlActionName){
		String sqlStatement = null;
		try {
			// 创建解析器工厂
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory .newInstance();
			// 创建解析器
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			InputStream in = CommonUtils.class.getResourceAsStream("/StatementSql.xml");//获取流
			// 解析xml
			Document document = builder.parse(in); // 这里的这个Document是w3c的包下的

			sqlStatement = document.getElementsByTagName(sqlActionName).item(0).getTextContent();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqlStatement.trim();
	}
	
	/**
	 * 返回一个UUID
	 * @return
	 */
	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		String string = uuid.toString();
		string = string.replace("-", "");
		string = string.toUpperCase();
		return string;
	}
	/**
	 * 
	 * @param 要加密的文本
	 * @return 加密成功的md5值
	 */
	public final static String encrypForMD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	
}
