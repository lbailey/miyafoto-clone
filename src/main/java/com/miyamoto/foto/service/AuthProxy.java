package com.miyamoto.foto.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.miyamoto.foto.service.files.Store;

@WebServlet(
        name = "AuthProxy",
        urlPatterns = {"/flickr/authorize"}
    )
public class AuthProxy extends HttpServlet {

	private static final String PATH = "/flickr/authorize";
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		
		try { 
			if (request.getParameter("userId") != null && request.getParameter("userPass") != null) {
		  		
		  		//need to do this with project init, where AutoAuth is
		  		HashMap<String, String> userMap = new LinkedHashMap<String,String> () {{
		  			put("admin","admin");
		  			put("geisha","osc51");
		  			put("maiko","osc51");
		  			put("default","password");
				}};
				
		  		//Store.writeFile(userMap);
		  		
		  		String loginUser = request.getParameter("userId");
          		String loginPass = request.getParameter("userPass");
		  		int permissionLevel = Store.authorizeUser(loginUser, loginPass);
		  		String resStr =  Integer.toString(permissionLevel);
		  		System.out.println(resStr);
		  		//response.getWriter().println(resStr);	
		  		if (permissionLevel > 0) {
		  			setSessionVars(request, loginUser, permissionLevel);
		  		}
		  		
		  	} else if (request.getParameter("logout") != null) {
		  		HttpSession session = request.getSession(false); 	
		  		session.setAttribute("authenticated", false);
		  		session.setAttribute("permission", Store.Login.NONE.getPermissionInt());
		  	}
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
    }

    private static void setSessionVars(HttpServletRequest request, String userId, int permissionInt) throws IOException, ServletException {		
		try { 
			HttpSession session = request.getSession(false); 	
        	session.setAttribute("authenticated", true);
        	session.setAttribute("permission", permissionInt); 		
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
    }
    
    
    public static void authorizeSession(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		
		try { 
			HttpSession session = request.getSession(false);
			if(!isAuthorized(request)) response.sendRedirect("index.jsp"); 
			
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
    }
    
    public static boolean isAuthorized(HttpServletRequest request) throws IOException, ServletException {		
		try { 
			HttpSession session = request.getSession(false);
        	
        	if(session.getAttribute("authenticated") == null) return false;
 			else if(session.getAttribute("authenticated") != null) {
 			 	return session.getAttribute("authenticated").equals(false) ? false : true;
			}
   				 
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
	   
	   return false;
    }
    
    public static boolean canRemove(HttpServletRequest request) throws IOException, ServletException {		
		try { 
			HttpSession session = request.getSession(false);        	
        	if(isAuthorized(request)) {
        		Integer permissionLevel = (Integer)session.getAttribute("permission");
        		return permissionLevel > 2; 	//clean
        	}
   				 
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
	   
	   return false;
    }
    
    
    
}