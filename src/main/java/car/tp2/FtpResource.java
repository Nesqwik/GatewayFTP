package car.tp2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

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
		if (!ftpClient.isConnected()) {
			try {
				ftpClient.connect("ftp.univ-lille1.fr");
			} catch (SocketException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private FTPClient ftpClient = new FTPClient();
	
	
	@POST
	@Path("/user/{userName}")
	@Produces("application/json")
	public Response user(@PathParam("userName") String userName) {
		try {
			int code = ftpClient.user(userName);
			return Response.status(code).build();
		} catch (IOException e) {
			return Response.status(500).build();
		}
	}
	
	
	@POST
	@Path("/pass/{password}")
	@Produces("application/json")
	public Response password(@PathParam("password") String password) {
		try {
			int code = ftpClient.pass(password);
			return Response.status(code).build(); 
		} catch (IOException e) {
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("/list")
	@Produces("application/json")
	public Response list() {
		try {
			int code = ftpClient.list();
			FTPFile[] files = ftpClient.listDirectories();
			return Response.status(code).entity(files).build();
		} catch (IOException e) {
			return Response.status(500).build();
		}
	}
	
	
	@GET
	@Path("/pwd")
	@Produces("application/json")
	public Response pwd() {
		try {
			// TODO pwd renvoie \"/\" : faire mieux
			int code = ftpClient.pwd();
			String data = ftpClient.printWorkingDirectory(); 
			return Response.status(code).entity(data).build();
		} catch (IOException e) {
			return Response.status(500).build();
		}
	}
	
	
	@PUT
	@Path("/quit")
	@Produces("application/json")
	public Response quit() {
		try {
			int code = ftpClient.quit();
			ftpClient.disconnect();
			return Response.status(code).build();
		} catch (IOException e) {
			return Response.status(500).build(); 
		}
	}
	
	@PUT
	@Path("/cwd/{path}")
	@Produces("application/json")
	public Response cwd(@PathParam("path") String path) {
		try {
			int code = ftpClient.cwd(path); 
			return Response.status(code).build();
		} catch (IOException e) {
			return Response.status(500).build();
		}
	}
	
	
	@PUT
	@Path("/cdup")
	@Produces("application/json")
	public Response cdup() {
		try {
			int code = ftpClient.cdup(); 
			return Response.status(code).build();
		} catch (IOException e) {
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("/download/{filepath}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public StreamingOutput download(@PathParam("filepath") final String filepath) {
            
        return new StreamingOutput() {
			public void write(OutputStream output) throws WebApplicationException {
				

				try {
					
					final BufferedOutputStream outputStream = new BufferedOutputStream(output);
					final BufferedInputStream inputStream = new BufferedInputStream(ftpClient.retrieveFileStream(filepath));
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
					final byte[] bytesArray = new byte[4096];
					
					int bytesRead = -1;
					while ((bytesRead = inputStream.read(bytesArray)) != -1) {
						outputStream.write(bytesArray, 0, bytesRead);
					}
					
					output.close();
					inputStream.close();
					ftpClient.completePendingCommand();
				} catch (IOException e) {
					try {
						output.close();
						ftpClient.completePendingCommand();
					} catch (IOException e1) {
						throw new WebApplicationException(e1);
					}
					
					throw new WebApplicationException(e);
				}				
			}
		};
		
	}
}

