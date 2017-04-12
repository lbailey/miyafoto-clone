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

import com.miyamoto.foto.service.Integration;
import com.miyamoto.foto.service.ProxyConstants;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "AlbumProxy",
        urlPatterns = {"/flickr/albums"}
    )
public class AlbumProxy extends HttpServlet {

	private static final String PATH = "/flickr/albums";

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		
		try {
			if (request.getParameter(ProxyConstants.PHOTOSET_ID) != null) {
				String photoSetId = request.getParameter(ProxyConstants.PHOTOSET_ID);
				getAlbum(request,response,photoSetId);
			} else if  (request.getParameter(ProxyConstants.INVALIDATE_ALBUM) != null) {
				String invalidateAlbum = request.getParameter(ProxyConstants.INVALIDATE_ALBUM);
				getAlbumSet(request,response,invalidateAlbum);	
			} else if (request.getParameter(ProxyConstants.ALBUM_YEAR) != null) {		
				String photoYear = request.getParameter(ProxyConstants.ALBUM_YEAR);
				getAlbumList(request, response, photoYear);		
			} else {
				getAlbumList(request,response,"2016");
			}
		} catch(Exception ex) {
		
		}
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		
		try { 
			if (request.getParameter(ProxyConstants.PHOTOSET_ID) != null && request.getParameter(ProxyConstants.PHOTO_ID) != null) {
		  		String photoSetId = request.getParameter(ProxyConstants.PHOTOSET_ID);
          		String photoId = request.getParameter(ProxyConstants.PHOTO_ID);
		  		Integration ppl = new Integration();
		  		String resStr = ppl.moveToAlbum(photoSetId, photoId);
		  		System.out.println(resStr);
		  		response.getWriter().println(resStr);	
		  	} else if (request.getParameter(ProxyConstants.ALBUM_NAME) != null && request.getParameter(ProxyConstants.ALBUM_PREFIX) != null) {
		  		String albumName = request.getParameter(ProxyConstants.ALBUM_NAME);
		  		String albumPrefix = request.getParameter(ProxyConstants.ALBUM_PREFIX); // "year" and "category" e.g. "2014-formal"
		  		Integration ppl = new Integration();
		  		String albumPhotoSetId = ppl.createNewAlbum(albumName, albumPrefix);
		  		response.getWriter().println(albumPhotoSetId);
		  	}
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }
    }
    
    protected void getAlbumList(HttpServletRequest request, HttpServletResponse response, String year) {    
		try { 
		  Integration ppl = new Integration();
		  String resStr = ppl.albumListToJson(year);
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
		  //String resStr = ppl.specificPhotoSetJson(photoSetId);
		  String resStr = ppl.specificCachePhotoSetJson(photoSetId);
		  response.getWriter().println(resStr);
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }

    }    
}