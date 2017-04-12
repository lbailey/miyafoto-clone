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
import com.miyamoto.foto.service.ProxyConstants;

@WebServlet(
        name = "AuthProxy",
        urlPatterns = {"/flickr/authorize"}
    )
public class AuthProxy extends HttpServlet {

	private static final String PATH = "/flickr/authorize";
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		
		try { 
			if (request.getParameter(ProxyConstants.USER_ID) != null && request.getParameter(ProxyConstants.USER_PASS) != null) {
		  		String loginUser = request.getParameter(ProxyConstants.USER_ID);
          		String loginPass = request.getParameter(ProxyConstants.USER_PASS);
		  		int permissionLevel = Store.authorizeUser(loginUser, loginPass);
		  		String resStr =  Integer.toString(permissionLevel);
		  		System.out.println(resStr);	
		  		if (permissionLevel > 0) {
		  			setSessionVars(request, loginUser, permissionLevel);
		  		}
		  		
		  	} else if (request.getParameter(ProxyConstants.AUTH_LOGOUT) != null) {
		  		HttpSession session = request.getSession(false); 	
		  		session.setAttribute(ProxyConstants.AUTHENTICATED, false);
		  		session.setAttribute(ProxyConstants.AUTH_PERMISSION, Store.Login.NONE.getPermissionInt());
		  	}
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
    }

    private static void setSessionVars(HttpServletRequest request, String userId, int permissionInt) throws IOException, ServletException {		
		try { 
			HttpSession session = request.getSession(false); 	
        	session.setAttribute(ProxyConstants.AUTHENTICATED, true);
        	session.setAttribute(ProxyConstants.AUTH_PERMISSION, permissionInt); 		
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
    }
    
    
    public static void authorizeSession(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		
		try { 
			HttpSession session = request.getSession(false);
			if(!isAuthorized(request)) response.sendRedirect(ProxyConstants.AUTH_REDIRECT); 
			
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
    }
    
    public static boolean isAuthorized(HttpServletRequest request) throws IOException, ServletException {		
		try { 
			HttpSession session = request.getSession(false);
        	
        	if(session.getAttribute(ProxyConstants.AUTHENTICATED) == null) return false;
 			else if(session.getAttribute(ProxyConstants.AUTHENTICATED) != null) {
 			 	return session.getAttribute(ProxyConstants.AUTHENTICATED).equals(false) ? false : true;
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
        		Integer permissionLevel = (Integer)session.getAttribute(ProxyConstants.AUTH_PERMISSION);
        		return permissionLevel > 2; 	//clean
        	}
   				 
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
	   
	   return false;
    }
    
    
    
}