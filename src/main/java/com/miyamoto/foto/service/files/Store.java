package com.miyamoto.foto.service.files;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets; 
import java.nio.file.Files; 
import java.util.stream.Stream;
import java.nio.file.StandardOpenOption;


import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.Properties; 
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import java.util.stream.Collectors; 

public class Store {

	private static final String USER_PATH = "store/users.txt";
	private static final String USER_PASS_PATH = "store/vault.txt";
	private static final String OAUTH_FILE_PATH = "store/oauth.txt";
	private static final String USER_AUTH_FILE_PATH = "store/userauth.txt";
	
	public enum Login {    			//permission levels
    	ADMIN 			("admin",   4),
    	UPLOADER	 	("maiko",   2),
    	DELETER 		("geisha",  3),
    	DEFAULT 		("default", 1),
    	NONE			("none",	0);

		private final String userType;
		private final int permissionInt;
    	
    	public String getUserType() { return userType;}
    	public int getPermissionInt() { return permissionInt; }
    	
    	Login(String userType, int permLevel) {
        	this.userType = userType;
        	this.permissionInt = permLevel;
    	}
    	
    	public boolean canUpload() {
    		return this.permissionInt > 1;
    	}
    	
    	public boolean canDelete() {
    		return this.permissionInt > 2;
    	}
    	
    	public static Login getUserType(String userId) {
    		for (Login log: Login.values()) {
    			if (log.getUserType().equalsIgnoreCase(userId)) return log;
    		}
    		
    		return Login.NONE;
    	}
    	
    }
    
    public static String getOAuthFilePath() {
    	return OAUTH_FILE_PATH;
    }
    
    public static String getUserAuthFilePath() {
    	return USER_AUTH_FILE_PATH;
    }
    
    public static Map<String,String> getAuthPairs(String filePath) throws IOException {
    	File authStore = new File(filePath);  	
    	List<String> authLines = Files.readAllLines(authStore.toPath(), StandardCharsets.UTF_8);   
    	Map<String, String> authMap = authLines.stream()
    		.map(s -> s.split(":"))
    		.collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));	
		return authMap;
    }
	
	//functions to check files for password in relation to enum logins.
	//for now the correct password is always "password"
	public static int authorizeUser(String loginUser, String loginPass) throws IOException {
	
		//verify that it matches the file stuff 
		//for now check that password matches password
		if (isPasswordMatch(loginUser, loginPass))
			return Login.getUserType(loginUser).getPermissionInt();
		
		return Login.NONE.getPermissionInt();
	}
	
	private static boolean isPasswordMatch(String loginUser, String loginPass) throws IOException {
		return verifyLoginCreds(loginUser, loginPass);
	}
	
	private static boolean verifyLoginCreds(String loginUser, String loginPass) throws IOException  { 
    	File passStore = new File(USER_PASS_PATH);
		File userStore = new File(USER_PATH);
    	
    	List<String> userLines = Files.readAllLines(userStore.toPath(), StandardCharsets.UTF_8);   	
    	List<String> passLines = Files.readAllLines(passStore.toPath(), StandardCharsets.UTF_8);
    	
    	String u = userLines.stream().filter(user -> loginUser.equals(new String(Base64.getDecoder().decode(user)))).findFirst().orElse("");
    	
    	if (!u.isEmpty()) {
    		String pass = passLines.get(userLines.indexOf(u));
    		return loginPass.equals(new String(Base64.getDecoder().decode(pass)));
    	} else return false;
    }
    
    private static void writeFile(HashMap<String,String> userLogins) throws IOException  {  
    
		for(Map.Entry<String, String> entry : userLogins.entrySet()) {
   			final String newLine = System.lineSeparator();
   			
   			ByteBuffer userBuff = null, passBuff = null;
   			userBuff = ByteBuffer.wrap((entry.getKey()).getBytes(StandardCharsets.UTF_8));
   			passBuff = ByteBuffer.wrap((entry.getValue()).getBytes(StandardCharsets.UTF_8));
		
    		String userEncrypted = Base64.getEncoder().encodeToString(userBuff.array());
        	String passEncrypted = Base64.getEncoder().encodeToString(passBuff.array());
			Files.write(Paths.get(USER_PATH), userEncrypted.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
			Files.write(Paths.get(USER_PATH), newLine.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
			Files.write(Paths.get(USER_PASS_PATH), passEncrypted.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
			Files.write(Paths.get(USER_PASS_PATH), newLine.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    	}
    }
	
	
}
