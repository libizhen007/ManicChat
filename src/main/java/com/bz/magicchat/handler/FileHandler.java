package com.bz.magicchat.handler;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.bz.magicchat.util.CommonUtils;
/**
 * 上传文件工具类
 * @author admin_BiZhen
 *
 */
@Controller
public class FileHandler {
	@RequestMapping("/uploadUserImg.do")
	public void uploadUserImg(Model model,MultipartFile userImg,HttpServletResponse response,HttpSession session){
				//原始文件名称
				String pictureFile_name =  userImg.getOriginalFilename();
				//新文件名称
				String newFileName = CommonUtils.getUUID()+pictureFile_name.substring(pictureFile_name.lastIndexOf("."));
				//上传图片
				//这个不是很好，这里使用虚拟目录。到时候转到服务器可能会出现一点问题
				//String savePath = session.getServletContext().getRealPath("uploadFile");
				//File uploadPic = new java.io.File(savePath, newFileName);
				//如果是使用vps就用这个吧
				File uploadPic = new java.io.File("E:/files",newFileName);
				if(!uploadPic.exists()){
					uploadPic.mkdirs();
				}
				try {
					//向磁盘写文件
					userImg.transferTo(uploadPic);
					response.getWriter().write(newFileName);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
				
	}
	
	@RequestMapping("/uploadUserFile.do")
	public void uploadUserFile(Model model,MultipartFile userFile,HttpServletResponse response,HttpSession session){
				//原始文件名称
				String pictureFile_name =  userFile.getOriginalFilename();
				//新文件名称
				String newFileName = CommonUtils.getUUID()+pictureFile_name.substring(pictureFile_name.lastIndexOf("."));
				//上传文件
				//String savePath = session.getServletContext().getRealPath("uploadFile");
				//File uploadPic = new java.io.File(savePath, newFileName);
				File uploadPic = new java.io.File("E:/files",newFileName);
				if(!uploadPic.exists()){
					uploadPic.mkdirs();
				}
				try {
					//向磁盘写文件
					userFile.transferTo(uploadPic);
					response.getWriter().write(newFileName);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
				
	}
}
