package launch;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.LinkedHashMap;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.util.IOUtilities;

import org.scribe.model.Token;
import org.scribe.model.Verifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.JreMemoryLeakPreventionListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.catalina.webresources.DirResourceSet;

import com.miyamoto.foto.service.files.Store;

//
//
// Tomcat server startup for Miya
//
//
public class Main {

    public static void main(String[] args) throws Exception {
        
        //
        // Setup OAuth vars for connecting app to Flickr
        // 
        /*
        authSetup();
        */
        
        //
        // Comment out Tomcat Server details to setup oath
        //
        ///*
        String webappDirLocation = "src/main/webapp/";
		Tomcat tomcat = new Tomcat();
		
		String webPort = System.getenv("PORT");
        tomcat.setPort(Integer.valueOf(webPort));

		StandardContext ctx = (StandardContext) tomcat.addWebapp("/",
						new File(webappDirLocation).getAbsolutePath());

		//declare an alternate location for your "WEB-INF/classes" dir:     
		File additionWebInfClasses = new File("target/classes");
		WebResourceRoot resources = new StandardRoot(ctx);
		resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", 
						additionWebInfClasses.getAbsolutePath(), "/"));
						
		ctx.setResources(resources);

		tomcat.start();
		tomcat.getServer().await();
		//*/
    }
    
  public static void authSetup() throws IOException, FlickrException {
  
  		String flickrApiKey = "c3d608ad4fa1e7527d7e6aff3957f288"; 
  		String flickrSharedSecret = "7fc26e7de0e295fe";
	
        Flickr flickr = new Flickr(flickrApiKey, flickrSharedSecret, new REST());
        Flickr.debugStream = false;
        AuthInterface authInterface = flickr.getAuthInterface();

        Scanner scanner = new Scanner(System.in);

        Token token = authInterface.getRequestToken();
        System.out.println("token: " + token);

        String url = authInterface.getAuthorizationUrl(token, Permission.DELETE);
        System.out.println("Follow this URL to authorise yourself on Flickr");
        System.out.println(url);
        System.out.println("Paste in the token it gives you:");
        System.out.print(">>");

        String tokenKey = scanner.nextLine();
        scanner.close();

        Token requestToken = authInterface.getAccessToken(token, new Verifier(tokenKey));
        System.out.println("Authentication success");

        Auth auth = authInterface.checkToken(requestToken);

        // This token can be used until the user revokes it.
        System.out.println("Token: " + requestToken.getToken());
        System.out.println("Secret: " + requestToken.getSecret());
        System.out.println("nsid: " + auth.getUser().getId());
        System.out.println("Realname: " + auth.getUser().getRealName());
        System.out.println("Username: " + auth.getUser().getUsername());
        System.out.println("Permission: " + auth.getPermission().getType());
        
        //need to do this with project init, where AutoAuth is
		HashMap<String, String> userMap = new LinkedHashMap<String,String> () {{
		  	put("admin","admin");
		  	put("rick","schwifty");
		  	put("morty","szechuan");
		  	put("default","password");
		}};
		
		HashMap<String, String> userAuth = new LinkedHashMap<String,String> () {{
		  	put("userId",auth.getUser().getId());
		  	put("userName",auth.getUser().getUsername());
		  	put("userReal",auth.getUser().getRealName());
		}};	
		
		HashMap<String, String> oAuthVars = new LinkedHashMap<String,String> () {{
		  	put("authToken",requestToken.getToken());
		  	put("authSecret",requestToken.getSecret());
		  	put("apiKey",flickrApiKey);
		  	put("sharedSecret",flickrSharedSecret);
		  	put("permission","Permission.DELETE;");

		}};		
		
		//
		// TODO: move these to Store to handle, only send hashmaps, don't reference filenames here. 
		//
		Store.writeFile(userMap);
		Store.writeOAuth(userAuth, "store/userauth.txt");
		Store.writeOAuth(oAuthVars, "store/oauth.txt");
    }
}