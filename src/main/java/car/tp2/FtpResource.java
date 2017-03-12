package car.tp2;

import java.io.IOException;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Exemple de ressource REST accessible a l'adresse :
 * 
 * 		http://localhost:8080/rest/tp2/helloworld
 * 
 */
@Path("/ftp")
public class FtpResource {

	public FtpResource() {
		
	}
	
	private final FtpClientFactory ftpClientFactory = new FtpClientFactory();
	
	
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public String loginForm() {
		return HtmlResponse.loginForm();
	}
	
	@POST
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	public Response login(@FormParam("username") final String username, @FormParam("password") final String password) {
		return list(username, password);
	}
	
	/*
	@POST
	@Path("/user/{userName}")
	@Produces(MediaType.TEXT_HTML)
	public Response user(@PathParam("userName") final String userName) {
		try {
			final int code = ftpClient.user(userName);
			return Response.status(code).build();
		} catch (final IOException e) {
			return Response.status(500).build();
		}
	}
	
	
	@POST
	@Path("/pass/{password}")
	@Produces(MediaType.TEXT_HTML)
	public Response password(@PathParam("password") final String password) {
		try {
			final int code = ftpClient.pass(password);
			return Response.status(code).build(); 
		} catch (final IOException e) {
			return Response.status(500).build();
		}
	}
	*/
	
	@GET
	@Path("/list")
	@Produces(MediaType.TEXT_HTML)
	public Response list(@QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			final FTPClient ftpClient = ftpClientFactory.newFtpInstance(username, password);
			if (ftpClient == null) {
				return Response.status(401).entity(HtmlResponse.unauthorized()).build();
			}
			final String path = ftpClient.printWorkingDirectory();
			
			final int code = ftpClient.list(path);
			final FTPFile[] files = ftpClient.listFiles(path);
			final String html = formatList(path, files, username, password);
			return Response.status(code).entity(html).build();
		} catch (final IOException e) {
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("/list/{path}")
	@Produces(MediaType.TEXT_HTML)
	public Response list(@PathParam("path") String path, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			final FTPClient ftpClient = ftpClientFactory.newFtpInstance(username, password);
			if (ftpClient == null) {
				return Response.status(401).entity(HtmlResponse.unauthorized()).build();
			}
			path = getClearedPath(path);
			path += "/";
			System.out.println(path);
			final int code = ftpClient.list(path);
			final FTPFile[] files = ftpClient.listFiles(path);
			final String html = formatList(path, files, username, password);
			return Response.status(code).entity(html).build();
		} catch (final IOException e) {
			return Response.status(500).build();
		}
	}
	
	private String getClearedPath(final String path) {
		return path.replace("%2F", "/");
	}
	
	private String getNormalizedPath(final String path) {
		return Paths.get(path.replace("//", "/")).normalize().toString().replace("\\", "/").replace("/", "%2F");
	}
	
	private String formatList(final String path, final FTPFile[] files, final String username, final String password) {
		String html = "<html><body><ul>";
		//html += "<li>" + HtmlResponse.getButton(" -> ..", "/rest/tp2/ftp/list/" + getNormalizedPath(path + "../") , "POST", username, password) + "</li>";
		html += "<li>-> <a href=\"/rest/tp2/ftp/list/" + getNormalizedPath(path + "./..") + "?username=" + username + "&password=" + password + "\" >..</a></li>";
		for(final FTPFile f : files) {
			if(f.isDirectory()) {
				//html += "<li>" + HtmlResponse.getButton(" -> " + f.getName(), "/rest/tp2/ftp/list/" + getNormalizedPath(path + f.getName() + "/"), "POST", username, password) + "</li>";
				html += "<li>-> <a href=\"/rest/tp2/ftp/list/" + getNormalizedPath(path + "./" + f.getName()) + "?username=" + username + "&password=" + password + "\" >" + f.getName() + "</a></li>";
			} else {
				html += "<li><a href=\"/rest/tp2/ftp/download/" + getNormalizedPath(path + f.getName()) + "?username=" + username + "&password=" + password + "\" >" + f.getName() + "</a></li>";
			}
		}
		html += "</ul></body></html>";
		return html;
	}
	
/*
	@GET
	@Path("/pwd")
	@Produces(MediaType.TEXT_HTML)
	public Response pwd() {
		try {
			// TODO pwd renvoie \"/\" : faire mieux
			final int code = ftpClient.pwd();
			final String data = ftpClient.printWorkingDirectory(); 
			return Response.status(code).entity(data).build();
		} catch (final IOException e) {
			return Response.status(500).build();
		}
	}
*/
	/*
	@PUT
	@Path("/quit")
	@Produces(MediaType.TEXT_HTML)
	public Response quit() {
		try {
			final int code = ftpClient.quit();
			ftpClient.disconnect();
			return Response.status(code).build();
		} catch (final IOException e) {
			return Response.status(500).build(); 
		}
	}
	*/
/*	
	@POST 
	@Path("/cwd/{path}")
	@Produces(MediaType.TEXT_HTML)
	public Response cwd(@PathParam("path") final String path) {
		try {
			ftpClient.cwd(path); 
			return list();
		} catch (final IOException e) {
			return Response.status(500).build();
		}
	}

	
	@POST
	@Path("/cdup")
	@Produces(MediaType.TEXT_HTML)
	public Response cdup() {
		try {
			ftpClient.cdup(); 
			return list();
		} catch (final IOException e) {
			return Response.status(500).build();
		}
	}
*/

	@GET
	@Path("/download/{filepath}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response download(@PathParam("filepath") String filepath, @Context final HttpServletResponse response, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			final FTPClient ftpClient = ftpClientFactory.newFtpInstance(username, password);
			if (ftpClient == null) {
				return Response.status(401).entity(HtmlResponse.unauthorized()).build();
			}
			filepath = getClearedPath(filepath);
			System.out.println(filepath);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			ftpClient.setBufferSize(60000);
			if(ftpClient.retrieveFile(filepath, response.getOutputStream())) {
				response.getOutputStream().close();
				return Response.ok().build();
			}
			
			return Response.ok().build();
		} catch (final IOException e) {
			return Response.ok().build();
		}
		
	}
}

