package car.tp2;

import java.io.ByteArrayOutputStream;
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
			int statusCode = path != null ? 200 : 500;

			final FTPFile[] files = ftpClient.listFiles(path);
			final String html = formatList(path, files, username, password);
			return Response.status(statusCode).entity(html).build();
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
			path = getClearedPath(path) + "/";
			
			final FTPFile[] files = ftpClient.listFiles(path);
			final String html = formatList(path, files, username, password);
			return Response.status(200).entity(html).build();
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
	
	private String newListLine(final boolean isDir, final String path, final String username, final String password, final String name) {
		final String idsParams = "?username=" + username + "&password=" + password;
		if(isDir) {
			if(name.equals("..")) {
				return "<tr>" + 
						"<td>Dir</td>" +
						"<td><a href=\"/rest/tp2/ftp/list/" + getNormalizedPath(path + "./" + name) + idsParams + "\" >"+name+"</a></td>" + 
						"<td></td>" + 
						"<td></td>" + 
						"</tr>";
			}
			return "<tr>" + 
					"<td>Dir</td>" +
					"<td><a href=\"/rest/tp2/ftp/list/" + getNormalizedPath(path + "./" + name) + idsParams + "\" >"+name+"</a></td>" + 
					"<td><a href=\"/rest/tp2/ftp/rmdir/" + getNormalizedPath(path + "./" + name) + idsParams + "\">delete</a></td>" + 
					"<td>"+ renameForm(name, path, idsParams) +"</td>" + 
					"</tr>";
		} else {
			return "<tr>" + 
					"<td>File</td>" +
					"<td><a href=\"/rest/tp2/ftp/download/" + getNormalizedPath(path + name) + idsParams + "\" >"+name+"</a></td>" + 
					"<td><a href=\"/rest/tp2/ftp/dele/" + getNormalizedPath(path + "./" + name) + idsParams + "\">delete</a></td>" + 
					"<td>"+ renameForm(name, path, idsParams) +"</td>" + 
					"</tr>";
		}
	}
	
	private String renameForm(final String name, final String path, final String idsParams) {
		return "<form action=\"/rest/tp2/ftp/rename/" + getNormalizedPath(path) + idsParams + "\" method=\"POST\">" + 
					"<input type=\"hidden\" name=\"oldName\" value=\"" + name + "\" />" + 
					"<input type=\"text\" name=\"newName\" />" +
					"<input type=\"submit\" value=\"renommer\" />" + 
				"</form>";
	}
	
	private String formatList(final String path, final FTPFile[] files, final String username, final String password) {
		String html = "<html><head><meta charset=\"UTF-8\"></head><body><table border=\"\">";
		//html += "<li>" + HtmlResponse.getButton(" -> ..", "/rest/tp2/ftp/list/" + getNormalizedPath(path + "../") , "POST", username, password) + "</li>";
		if(!path.replace("//", "/").equals("/")) {			
			html += newListLine(true, path, username, password, "..");
		}
		for(final FTPFile f : files) {
			if(f.isDirectory()) {
				html += newListLine(true, path, username, password, f.getName());
			} else {
				html += newListLine(false, path, username, password, f.getName());
			}
		}
		html += "</table></body></html>";
		System.out.println("html " + html);
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
	@Path("/rmdir/{path}")
	@Produces(MediaType.TEXT_HTML)
	public Response rmdir(@PathParam("path") final String path, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			final FTPClient ftpClient = ftpClientFactory.newFtpInstance(username, password);
			if (ftpClient == null) {
				return Response.status(401).entity(HtmlResponse.unauthorized()).build();
			}
			System.out.println("remove : " + getClearedPath(path));
			ftpClient.rmd(getClearedPath(path)); 
			
			return list(path + "/..", username, password);
		} catch (final IOException e) {
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("/dele/{path}")
	@Produces(MediaType.TEXT_HTML)
	public Response dele(@PathParam("path") final String path, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			final FTPClient ftpClient = ftpClientFactory.newFtpInstance(username, password);
			if (ftpClient == null) {
				return Response.status(401).entity(HtmlResponse.unauthorized()).build();
			}
			System.out.println("remove : " + getClearedPath(path));
			ftpClient.dele(getClearedPath(path)); 
			
			return list(path + "/..", username, password);
		} catch (final IOException e) {
			return Response.status(500).build();
		}
	}
	
	@POST
	@Path("/rename/{path}")
	@Produces(MediaType.TEXT_HTML)
	public Response rename(@PathParam("path") final String path, @FormParam("oldName") final String oldName, @FormParam("newName") final String newName, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			final FTPClient ftpClient = ftpClientFactory.newFtpInstance(username, password);
			if (ftpClient == null) {
				return Response.status(401).entity(HtmlResponse.unauthorized()).build();
			}
			final String from = getClearedPath(path) + "/" + oldName;
			final String to = getClearedPath(path) + "/" + newName;
			
			ftpClient.rename(from, to);
			
			return list(path, username, password);
		} catch (final IOException e) {
			return Response.status(500).build();
		}
	}
	
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

