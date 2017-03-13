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

import car.tp2.ftp.services.FTPService;
import car.tp2.utils.FtpRequestException;
import car.tp2.utils.HtmlResponse;
import car.tp2.utils.Utils;

/**
 * 
 * @author Louis GUILBERT et Jonathan LECOINTE
 *
 * FTPResource : 
 * Controlleur permettant de gérer les requêtes HTTP
 */
@Path("/ftp")
public class FTPResource {
	private final FTPService ftpService = new FTPService();
	
	/**
	 * Resource générant le formulaire de connexion
	 * @return le formulaire de connexion
	 */
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML)
	public String loginForm() {
		return HtmlResponse.loginForm();
	}
	
	/**
	 * Resource permettant la connexion au serveur ftp
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return la liste des fichiers à la racine du dossier utilisateur
	 */
	@POST
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	public Response login(@FormParam("username") final String username, @FormParam("password") final String password) {
		return list(username, password);
	}
	
	/**
	 * Resource permettant la création de la liste des fichiers du serveur ftp
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return la liste des fichiers à la racine du dossier utilisateur
	 */
	@GET
	@Path("/list")
	@Produces(MediaType.TEXT_HTML)
	public Response list(@QueryParam("username") final String username, @QueryParam("password") final String password) {
		try {
			String workingDirectory = ftpService.pwd(username, password);
			System.out.println(workingDirectory);
			return Response.ok(ftpService.list(workingDirectory, username, password)).build();
		} catch (final FtpRequestException e) {
			return Response.status(e.getCode()).entity(e.getData()).build();
		}
	}
	
	/**
	 * Resource permettant la création de la liste des fichiers du serveur ftp
	 * @param path chemin vers le dossier à lister
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return la liste des fichiers du dossier spécifié
	 */
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

	/**
	 * supprime le dossier ayant le chemin spécifié
	 * @param path chemin vers le dossier à supprimer
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return la liste du dossier parent du dossier spécifié
	 */
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
	
	/**
	 * supprime le fichier ayant le chemin spécifié
	 * @param path chemin vers le fichier à supprimer
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return la liste du dossier parent du fichier spécifié
	 */
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
	
	/**
	 * renomme le fichier ou dossier spécifié
	 * @param path chemin vers le dossier dans lequel se trouve le fichier ou dossier à renommer
	 * @param oldName ancien nom du fichier ou dossier
	 * @param newName nouveau nom du fichier ou dossier
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return la liste du dossier spécifié par path
	 */
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
	
	/**
	 * Télécharge le fichier spécifié
	 * @param filepath chemin vers le fichier à télécharger
	 * @param response réponse du context
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return le fichier à télécharger
	 */
	@GET
	@Path("/download/{filepath: .*}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response download(@PathParam("filepath") final String filepath, @Context final HttpServletResponse response, @QueryParam("username") final String username, @QueryParam("password") final String password) {
		return Response.ok(ftpService.download(filepath, response, username, password)).build();
		
	}
	
	/**
	 * Récupère le fichier envoyé
	 * @param attachment le descripteur de fichier à uploader
	 * @param path chemin vers le dossier ou va s'uploader le fichier
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return la liste du chemin passé en paramètre
	 */
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
	
	/**
	 * permet de créer un dossier
	 * @param path chemin vers le dossier dans lequel créer le nouveau dossier
	 * @param name le nom du nouveau dossier
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return la liste du chemin passé en paramètre
	 */
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

