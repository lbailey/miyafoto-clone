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

import com.miyamoto.foto.service.AuthProxy;
import com.miyamoto.foto.service.Integration;
import com.miyamoto.foto.service.ProxyConstants;

@WebServlet(
        name = "RemoveProxy",
        urlPatterns = {"/flickr/remove"}
    )
public class RemoveProxy extends HttpServlet {

	private static final String PATH = "/flickr/remove";
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		
		try { 
			if (request.getParameter(ProxyConstants.PHOTO_ID) != null && request.getParameter(ProxyConstants.PHOTOSET_ID) != null && AuthProxy.canRemove(request)) {
		  		String photoSetId = request.getParameter(ProxyConstants.PHOTOSET_ID);
          		String photoId = request.getParameter(ProxyConstants.PHOTO_ID);
		  		Integration ppl = new Integration();
		  		String resStr = ppl.removePhotoFromAlbum(photoSetId, photoId);
		  		System.out.println(resStr);
		  		response.getWriter().println(resStr);
		  	}
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
    }
    
}