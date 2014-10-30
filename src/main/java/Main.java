import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.util.IOUtilities;

import org.scribe.model.Token;
import org.scribe.model.Verifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

import com.miyamoto.foto.service.UploadProxy;
import com.miyamoto.foto.service.AlbumProxy;

public class Main extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

  }

  private void showHome(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

  }
  
  
  /*

	public static void auth() throws IOException, FlickrException {
	
        Flickr flickr = new Flickr("eccbfec6885f6adefb2fd063dce81a20", "b114d59e550cf4e9", new REST());
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
    }
    
    */
}
