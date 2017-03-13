package car.tp2.ftp.resources;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
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

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import com.sun.istack.NotNull;

import car.tp2.ftp.FtpClientFactory;
import car.tp2.ftp.services.FTPService;
import car.tp2.utils.FtpRequestException;
import car.tp2.utils.HtmlResponse;
import car.tp2.utils.Utils;

/**
 * Exemple de ressource REST accessible a l'adresse :
 * 
 * 		http://localhost:8080/rest/tp2/helloworld
 * 
 */
@Path("/ftp")
public class FTPResource {
	private final FtpClientFactory ftpClientFactory = new FtpClientFactory();
	private final FTPService ftpService = new FTPService();

	public FTPResource() {

	}
	
	
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
			return Response.ok(ftpService.list("/", username, password)).build();
		} catch (final FtpRequestException e) {
			return Response.status(e.getCode()).entity(e.getData()).build();
		}
	}
	
	@GET
	@Path("/list/{path: .*}")
	@Produces(MediaType.TEXT_HTML)
	public Response list(@PathParam("path") final String path, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			return Response.ok(ftpService.list(path, username, password)).build();
		} catch (final FtpRequestException e) {
			return Response.status(e.getCode()).entity(e.getData()).build();
		}
	}

	@GET
	@Path("/rmdir/{path: .*}")
	@Produces(MediaType.TEXT_HTML)
	public Response rmdir(@PathParam("path") final String path, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			ftpService.rmdir(path, username, password);
			return Response.ok(ftpService.list(Utils.getNormalizedPath(path + "/.."), username, password)).build();
		} catch (final FtpRequestException e) {
			return Response.status(e.getCode()).entity(e.getData()).build();
		}
	}
	
	@GET
	@Path("/dele/{path: .*}")
	@Produces(MediaType.TEXT_HTML)
	public Response dele(@PathParam("path") final String path, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			ftpService.dele(path, username, password);
			return Response.ok(ftpService.list(Utils.getNormalizedPath(path + "/.."), username, password)).build();
		} catch (final FtpRequestException e) {
			return Response.status(e.getCode()).entity(e.getData()).build();
		}
	}
	
	@POST
	@Path("/rename/{path: .*}")
	@Produces(MediaType.TEXT_HTML)
	public Response rename(@PathParam("path") final String path, @FormParam("oldName") final String oldName, @FormParam("newName") final String newName, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			ftpService.rename(path, oldName, newName, username, password);
			return Response.ok(ftpService.list(Utils.getNormalizedPath(path), username, password)).build();
		} catch (final FtpRequestException e) {
			return Response.status(e.getCode()).entity(e.getData()).build();
		}
	}
	
	@GET
	@Path("/download/{filepath: .*}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response download(@PathParam("filepath") final String filepath, @Context final HttpServletResponse response, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		return Response.ok(ftpService.download(filepath, response, username, password)).build();
		
	}
	
	
	@POST
	@Path("/upload/{path: .*}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	public Response uploadFile(
		@Multipart(value = "file") @NotNull final Attachment attachment,
		@PathParam("path") final String path,
		@QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			ftpService.uploadFile(attachment, path, username, password);
			return Response.ok(ftpService.list(Utils.getNormalizedPath(path), username, password)).build();
		} catch (final FtpRequestException e) {
			return Response.status(e.getCode()).entity(e.getData()).build();
		}
		
	} 
	
	@POST
	@Path("/mkdir/{path: .*}")
	@Produces(MediaType.TEXT_HTML)
	public Response mkdir(@PathParam("path") final String path, @FormParam("name") final String name, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			ftpService.mkdir(path, name, username, password);
			return Response.ok(ftpService.list(Utils.getNormalizedPath(path), username, password)).build();
		} catch (final FtpRequestException e) {
			return Response.status(e.getCode()).entity(e.getData()).build();
		}
	}
}

