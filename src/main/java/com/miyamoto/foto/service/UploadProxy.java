package com.miyamoto.foto.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.SocketTimeoutException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.AsyncContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.miyamoto.foto.service.ImageMeta;
import com.miyamoto.foto.service.Integration;
import com.miyamoto.foto.service.UploadProgressListener;

@WebServlet(urlPatterns = {"/flickr/upload"}/*, asyncSupported = true*/ )
public class UploadProxy extends HttpServlet {
    
    private static final Logger log = LoggerFactory.getLogger(UploadProxy.class);

	private static final String MIYAMOTO_SERVICE_PATH = "/flickr/upload";

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {		

		if (ServletFileUpload.isMultipartContent(request)) { toFlickr(request,response); }	
    }
    
    protected void toFlickr(HttpServletRequest request, HttpServletResponse response) throws 
    	IOException, ServletException, ConnectTimeoutException, SocketTimeoutException {
    
//    	final AsyncContext acontext = request.startAsync();
    
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        
        UploadProgressListener uploadListener = new UploadProgressListener();
		upload.setProgressListener(uploadListener);
		
		HttpSession session = request.getSession();
		session.setAttribute("progress", uploadListener);
    	
		try { 
		  List<FileItem> imgItems = upload.parseRequest(request);
		  	
		  for (FileItem img : imgItems) {
		  	if (!img.isFormField()) {
				String fileName = img.getName();
				String contentType = img.getContentType();
				boolean isInMemory = img.isInMemory();
				long sizeInBytes = img.getSize();
				byte[] data = img.get();

    			//response.setContentType("image/jpeg");
    			//response.setContentLength((int)sizeInBytes);
    			//response.getOutputStream().write(data);
				
				ImageMeta meta = new ImageMeta(fileName, data);
				Integration ppl = new Integration(meta);
				String imageId = ppl.postToFlikr();
				
				response.getWriter().println(imageId);
			}
		 }
	   } catch(Exception ex) {
		   System.out.println(ex);
	   }

    }    
    
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws 
    	IOException, ServletException, ConnectTimeoutException, SocketTimeoutException {

    	PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(true);
		if (session == null) {
			out.println("0"); // just to be safe
			return;
		}

		UploadProgressListener listener = (UploadProgressListener) session.getAttribute("uploadListener");
		if (listener == null) {
			out.println("0");
			return;
		}

		out.println(listener.getMessage());
		
    
    } 
}