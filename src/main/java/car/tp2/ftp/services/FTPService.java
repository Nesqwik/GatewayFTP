package car.tp2.ftp.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import car.tp2.ftp.FtpClientFactory;
import car.tp2.utils.FtpRequestException;
import car.tp2.utils.HtmlResponse;
import car.tp2.utils.Utils;

public class FTPService {
	private final FtpClientFactory ftpClientFactory = new FtpClientFactory();
	
	private FTPClient getFtpClient(final String username, final String password) throws FtpRequestException {
		FTPClient ftpClient;
		try {
			ftpClient = ftpClientFactory.newFtpInstance(username, password);
		} catch (final ConnectException e) {
			throw new FtpRequestException(500);
		}
		if (ftpClient == null) {
			throw new FtpRequestException(401, HtmlResponse.unauthorized());
		}
		
		return ftpClient;
	}
	
	
	
	private String formatList(String path, final FTPFile[] files, final String username, final String password) {
		String html = "<html><head><meta charset=\"UTF-8\"></head><body>";
		final String idsParams = "?username=" + username + "&password=" + password;
		path = Utils.getNormalizedPath(path);
		html += HtmlResponse.newDirForm(path, idsParams);
		html += HtmlResponse.uploadForm(path, idsParams);
		html += "<table border=\"\">";
		if(!path.isEmpty() && !path.equals("/")) {			
			html += HtmlResponse.newListLine(true, path, idsParams, "..");
		}
		System.out.println("path " + path);
		for(final FTPFile f : files) {
			System.out.println(f.getName());
			if(f.isDirectory()) {
				html += HtmlResponse.newListLine(true, path, idsParams, f.getName());
			} else {
				html += HtmlResponse.newListLine(false, path, idsParams, f.getName());
			}
		}
		html += "</table></body></html>";
		return html;
	}
	
	public String list(final String path, final String username, final String password) throws FtpRequestException {
		try {
			final FTPClient ftpClient = getFtpClient(username, password);

			final FTPFile[] files = ftpClient.listFiles(path);
			final String html = formatList(path, files, username, password);
			
			ftpClient.disconnect();
			
			return html;
		} catch (final IOException e) {
			throw new FtpRequestException(500);
		}
	}
	
	public String pwd(final String username, final String password) throws FtpRequestException {
		final FTPClient ftpClient = getFtpClient(username, password);
		String pwd;
		try {
			pwd = ftpClient.printWorkingDirectory();
			ftpClient.disconnect();
			return pwd;
		} catch (IOException e) {
			throw new FtpRequestException(500);
		}
	}
	
	
	private void recursiveRmdir(final String path, final FTPClient ftpClient) throws IOException, FtpRequestException {
		final FTPFile[] files = ftpClient.listFiles(path);
		for(final FTPFile f : files) {
			if(f.isDirectory()) {
				recursiveRmdir(path + "/" + f.getName(), ftpClient);
			} else {
				if (ftpClient.dele(path + "/" + f.getName()) == 550) {
					throw new FtpRequestException(500);
				}
			}
		}
		if (ftpClient.rmd(path) == 550) {
			throw new FtpRequestException(500);
		}
	}
	
	public void rmdir(final String path, final String username, final String password) throws FtpRequestException {
		try {
			final FTPClient ftpClient = getFtpClient(username, password);
			recursiveRmdir(path, ftpClient);
			ftpClient.disconnect();
		} catch (final IOException | FtpRequestException e) {
			throw new FtpRequestException(500);
		}
	}
	
	
	public void dele(final String path, final String username, final String password) throws FtpRequestException {
		try {
			final FTPClient ftpClient = getFtpClient(username, password);
			if (ftpClient.dele(path) == 550) {
				throw new FtpRequestException(500);
			}
			ftpClient.disconnect();
		} catch (final IOException e) {
			throw new FtpRequestException(500);
		}
	}
	
	
	public void rename(final String path, final String oldName, final String newName, final String username, final String password) throws FtpRequestException {
		try {
			final FTPClient ftpClient = getFtpClient(username, password);
			final String from = path + "/" + oldName;
			final String to = path + "/" + newName;
			
			if (!ftpClient.rename(from, to)) {
				throw new FtpRequestException(500);
			}
			
			ftpClient.disconnect();
		} catch (final IOException e) {
			throw new FtpRequestException(500);
		}
	}
	
	
	public StreamingOutput download(final String filepath, final HttpServletResponse response, final String username, final String password) {
		final StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(final OutputStream arg0) {
				FTPClient ftpClient;
				try {
					ftpClient = getFtpClient(username, password);
					if (ftpClient != null) {
						ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
						ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
						ftpClient.setBufferSize(60000);
						if (ftpClient.retrieveFile(filepath, response.getOutputStream())) {
							response.getOutputStream().close();
							ftpClient.disconnect();
						}
					}
				} catch (final FtpRequestException e) {
					throw new RuntimeException(e);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
				
		return stream;
	}
	
	
	public void uploadFile(final Attachment attachment, final String path, final String username, final String password) throws FtpRequestException {
		try {
			final FTPClient ftpClient = getFtpClient(username, password);
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			
			final String filepath = path + "/" + attachment.getDataHandler().getName();
			if (!ftpClient.storeFile(filepath, attachment.getDataHandler().getInputStream())) {
				throw new FtpRequestException(500);
			}
			
			ftpClient.disconnect();
		} catch (final IOException e) {
			throw new FtpRequestException(500);
		}
	}
	
	public void mkdir(@PathParam("path") final String path, @FormParam("name") final String name, @QueryParam("username") final String username, @QueryParam("password") final String password) throws FtpRequestException {
		try {
			final FTPClient ftpClient = getFtpClient(username, password);
			final String createPath = path + "/" + name;
			
			if (ftpClient.mkd(createPath) == 550) {
				throw new FtpRequestException(500);
			}
			ftpClient.disconnect();
		} catch (final IOException e) {
			throw new FtpRequestException(500);
		}
	}
}
