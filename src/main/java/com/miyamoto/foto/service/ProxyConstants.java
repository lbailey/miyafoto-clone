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


public final class ProxyConstants {

	// Flickr vars
	public static final String PHOTOSET_ID = "photoSetId";
	public static final String PHOTO_ID = "photoId";
	public static final String INVALIDATE_ALBUM = "invalidateAlbum";
	public static final String ALBUM_YEAR = "albumYear";
	public static final String ALBUM_NAME = "albumName";
	public static final String ALBUM_PREFIX = "albumPrefix";
	
	
	// Authentication
	public static final String USER_ID = "userId";
	public static final String USER_PASS = "userPass";
	public static final String AUTH_LOGOUT = "logout";
	public static final String AUTHENTICATED = "authenticated";
	public static final String AUTH_PERMISSION = "permission";
	public static final String AUTH_REDIRECT = "index.jsp";
	
}