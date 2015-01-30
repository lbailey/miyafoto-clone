package launch;

import java.io.File;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.JreMemoryLeakPreventionListener;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.catalina.webresources.DirResourceSet;


/**
*
* Huge thanks to http://stackoverflow.com/questions/11669507/
*						embedded-tomcat-7-servlet-3-0-annotations-not-working
*
*/
public class Main {

    public static void main(String[] args) throws Exception {
        
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
    }
}