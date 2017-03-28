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
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.miyamoto.foto.service.files.Store;

public class OAuth {
    
    public enum Permission {    
    	NONE (0),
    	READ (1),
    	WRITE (2),
    	DELETE (3);

    	private final int permission;
    	public int getVal() { return permission;}
    	
    	Permission(int permission) {
        	this.permission = permission;
    	}
    }
    
    public class User {
    	private String userId, userName, userReal;
    	
    	public User(String id, String name, String real) {
    		this.userId = id;
    		this.userName = name;
    		this.userReal = real;
    	}
    	
    	public User(String userFilePath) throws IOException {
    		Map<String,String> authMap = Store.getAuthPairs(userFilePath);
    		this.userId = authMap.get("userId");
    		this.userName = authMap.get("userName");
    		this.userReal = authMap.get("userReal");
    	}
    	
    	public String getUserId() {
    		return this.userId;
    	}
    }
    
    private String authToken, 
    			   authSecret,
    			   apiKey,
    			   sharedSecret;
    			   
    private Permission permission;
    private User user;
    
    public OAuth(String token, String secret, String api, String shared, Permission perm) {
    	this.authToken = token;
    	this.authSecret = secret;
    	this.apiKey = api;
    	this.sharedSecret = shared;
    	this.permission = perm;
    }     

	public OAuth(String oAuthFilePath) throws IOException {
		Map<String,String> authMap = Store.getAuthPairs(oAuthFilePath);		
		this.authToken = 	authMap.get("authToken");
    	this.authSecret = 	authMap.get("authSecret");
    	this.apiKey = 		authMap.get("apiKey");
    	this.sharedSecret = authMap.get("sharedSecret");
    	this.permission = 	Permission.DELETE;
	}	
	
	protected String getApiKey() {
		return this.apiKey;
	}
	
	protected String getSharedSecret() {
		return this.sharedSecret;
	}
	
	protected String getToken() {
		return this.authToken;
	}
	
	protected String getTokenSecret() {
		return this.authSecret;
	}
}