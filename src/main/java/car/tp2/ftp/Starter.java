package car.tp2.ftp;

import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.mockftpserver.fake.FakeFtpServer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import car.tp2.FakeFTP;
import car.tp2.utils.Constants;

/**
 * La classe principale de l'application.
 * Demarre un serveur sur le port 8080.
 * Utilise le prefixe /rest/ pour les URLs.
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
public class Starter {
	
	public static void main( final String[] args ) throws Exception {
		

		
		if (args.length == 1 && args[0].equals("test")) {
			final FakeFtpServer fake = FakeFTP.getInstance();
			Constants.put("host", "localhost");
			Constants.put("port", fake.getServerControlPort() + "");
			Constants.put("serverPort", "8080");
			Constants.put("ftpMode", "actif");
			System.out.println(Constants.get("port"));
		} else {
			
			Constants.put("serverPort", (System.getProperty("server.port") == null ? "8080" : System.getProperty("server.port")));
			Constants.put("ftpHost", (System.getProperty("ftp.host") == null ? "localhost" : System.getProperty("ftp.host")));
			Constants.put("ftpPort", (System.getProperty("ftp.port") == null ? "21" : System.getProperty("ftp.port")));
			Constants.put("ftpMode", (System.getProperty("ftp.mode") == null ? "passif" : System.getProperty("ftp.mode")));
		}
		        
		final Server server = new Server(Integer.parseInt(Constants.get("serverPort")));
 		final ServletHolder servletHolder = new ServletHolder( new CXFServlet() );
 		final ServletContextHandler context = new ServletContextHandler(); 		
 		context.setContextPath( "/" );
 		context.addServlet( servletHolder, "/rest/*" ); 	
 		context.addEventListener( new ContextLoaderListener() );
 		
 		context.setInitParameter( "contextClass", AnnotationConfigWebApplicationContext.class.getName() );
 		context.setInitParameter( "contextConfigLocation", Config.class.getName() );
 		
        server.setHandler( context );
        server.start();
        server.join();	
	}
}
