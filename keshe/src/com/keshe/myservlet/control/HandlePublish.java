package com.keshe.myservlet.control;

import java.util.Date; 
import java.util.Calendar; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat; 
import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

import com.keshe.mybean.data.*;

public class HandlePublish extends HttpServlet {
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		Connection con = null;
		PreparedStatement sql;
		HttpSession session = request.getSession(true);
		Login loginBean = (Login)session.getAttribute("login");
		String backNews = "";
		Publish pub = new Publish();
		
		Date now = new Date(); 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String uptime = dateFormat.format(now); 
		
		request.setAttribute("publish", pub);
		//String newid = request.getParameter("newsid").trim();
		String title = request.getParameter("title").trim();
		String message = request.getParameter("message").trim();
		String newstype = request.getParameter("newstype").trim();
		String contact = request.getParameter("contact").trim();
		String number = request.getParameter("number").trim();
		String uri = "jdbc:mysql://localhost/factory?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		
		String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(number);
		
		if (m.find()==false && number.length()!=11) {
			request.setAttribute("message", "电话号码格式不正确！");
			request.getRequestDispatcher("publish.jsp").forward(request, response);
		} else {
		
		if (loginBean!=null) {
			
			boolean boo = (title.length()>0)&&(message.length()>0)&&(newstype.length()>0)&&(contact.length()>0)&&(number.length()>0);
			
			try {
				String logname = loginBean.getLogname().trim();
				con = DriverManager.getConnection(uri, "root", "7162");
				String insertCondition = "INSERT INTO news(logname,title,message,newstype,uptime,contact,number) VALUES(?,?,?,?,?,?,?)";
				sql = con.prepareStatement(insertCondition);
				
				if (boo) {
					sql.setString(1, logname);
					sql.setString(2, title);
					sql.setString(3, message);
					sql.setString(4, newstype);
					sql.setString(5, uptime);
					sql.setString(6,contact);
					sql.setString(7, number);
					int d = sql.executeUpdate();
					if (d!=0) {
						backNews = "发布成功，返回主页查看";
						pub.setBackNews(backNews);
						pub.setLogname(logname);
						pub.setNewstype(newstype);
						pub.setTitle(title);
						pub.setMessage(message);
						pub.setContact(contact);
						pub.setNumber(number);
						pub.setUptime(uptime);
						
						request.setAttribute("message", "发布成功，返回主页查看！");
						request.getRequestDispatcher("index.jsp").forward(request, response);
					}
				} else {
					System.out.println("dddd");
					backNews = "信息填写不完整，发布失败";
					pub.setBackNews(backNews);
					request.setAttribute("message", "信息填写不完整，发布失败！");
					request.getRequestDispatcher("publish.jsp").forward(request, response);
				}
				con.close();
			} catch (SQLException exp) {
				backNews = "登录后才能发布信息哦~";
				pub.setBackNews(backNews);
				request.setAttribute("message", "登录后才能发布信息！");
				request.getRequestDispatcher("login.jsp").forward(request, response);
			}
		} else {
			request.setAttribute("message", "登录后才能发布信息！");
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
