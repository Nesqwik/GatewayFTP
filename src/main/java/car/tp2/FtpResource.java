package car.tp2;

import java.io.IOException;
import java.net.SocketException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Exemple de ressource REST accessible a l'adresse :
 * 
 * 		http://localhost:8080/rest/tp2/helloworld
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
@Path("/ftp")
public class FtpResource {

	public FtpResource() {
		if (!client.isConnected()) {
			try {
				client.connect("ftp.univ-lille1.fr");
			} catch (SocketException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private FTPClient client = new FTPClient();
	
	
	@POST
	@Path("/user/{userName}")
	@Produces("application/json")
	public HttpResponse<Void> user(@PathParam("userName") String userName) {
		try {
			int code = client.user(userName);
			return new HttpResponse<Void>(code); 
		} catch (IOException e) {
			return new HttpResponse<Void>(500);
		}
	}
	
	
	@POST
	@Path("/pass/{password}")
	@Produces("application/json")
	public HttpResponse<Void> password(@PathParam("password") String password) {
		try {
			int code = client.pass(password);
			return new HttpResponse<Void>(code); 
		} catch (IOException e) {
			return new HttpResponse<Void>(500);
		}
	}
	
	@GET
	@Path("/list")
	@Produces("application/json")
	public HttpResponse<FTPFile[]> list() {
		try {
			int code = client.list();
			FTPFile[] files = client.listDirectories();
			return new HttpResponse<FTPFile[]>(code, files); 
		} catch (IOException e) {
			return new HttpResponse<FTPFile[]>(500);
		}
	}
	
	
	@GET
	@Path("/pwd")
	@Produces("application/json")
	public HttpResponse<String> pwd() {
		try {
			// TODO pwd renvoie \"/\" : faire mieux
			int code = client.pwd();
			String data = client.printWorkingDirectory();
			return new HttpResponse<String>(code, data); 
		} catch (IOException e) {
			return new HttpResponse<String>(500);
		}
	}
	
	
	@PUT
	@Path("/quit")
	@Produces("application/json")
	public HttpResponse<String> quit() {
		try {
			int code = client.quit();
			client.disconnect();
			return new HttpResponse<String>(code); 
		} catch (IOException e) {
			return new HttpResponse<String>(500);
		}
	}
	
	@PUT
	@Path("/cwd/{path}")
	@Produces("application/json")
	public HttpResponse<String> cwd(@PathParam("path") String path) {
		try {
			int code = client.cwd(path); 
			return new HttpResponse<String>(code); 
		} catch (IOException e) {
			return new HttpResponse<String>(500);
		}
	}
	
	
	@PUT
	@Path("/cdup")
	@Produces("application/json")
	public HttpResponse<String> cdup() {
		try {
			int code = client.cdup(); 
			return new HttpResponse<String>(code);
		} catch (IOException e) {
			return new HttpResponse<String>(500);
		}
	}
}

