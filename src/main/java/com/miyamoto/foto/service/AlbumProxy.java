package com.miyamoto.foto.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.miyamoto.foto.service.ImageMeta;
import com.miyamoto.foto.service.Integration;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

@WebServlet(
        name = "AlbumProxy",
        urlPatterns = {"/flickr/albums"}
    )
public class AlbumProxy extends HttpServlet {

	private static final String PATH = "/flickr/albums";

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		
		try {
			if (request.getParameter("photoSetId") != null) {
				String photoSetId = request.getParameter("photoSetId");
				getAlbum(request,response,photoSetId);
			} else if  (request.getParameter("invalidateAlbum") != null) {
				String invalidateAlbum = request.getParameter("invalidateAlbum");
				getAlbumSet(request,response,invalidateAlbum);	
			} else {
				getAlbumSet(request,response,"12345");				
			}
		} catch(Exception ex) {
		
		}
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		

		try { 
		  String photoSetId = request.getParameter("photoSetId");
          String photoId = request.getParameter("photoId");
		  Integration ppl = new Integration();
		  String resStr = ppl.moveToAlbum(photoSetId, photoId);
		  System.out.println(resStr);
		  response.getWriter().println(resStr);
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
    }
    
    protected void getAlbumSet(HttpServletRequest request, HttpServletResponse response, String invalidateId) {    
		try { 
		  Integration ppl = new Integration();
		  String resStr = ppl.allPhotoSets(invalidateId);
		  response.getWriter().println(resStr);
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }

    }    
    
    protected void getAlbum(HttpServletRequest request, HttpServletResponse response, String photoSetId) {
    
		try { 
		  Integration ppl = new Integration();
		  String resStr = ppl.specificPhotoSetJson(photoSetId);
		  response.getWriter().println(resStr);
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }

    }    
}