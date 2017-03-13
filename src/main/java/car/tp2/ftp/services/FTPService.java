package car.tp2.ftp.services;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import car.tp2.ftp.FtpClientFactory;
import car.tp2.utils.FtpRequestException;
import car.tp2.utils.HtmlResponse;
import car.tp2.utils.Utils;

/**
 * 
 * @author Louis GUILBERT et Jonathan LECOINTE
 *
 * FTPService : 
 * Permet de fournir les services nécessaire à la passerelle pour gérer le ftp
 */
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
		for(final FTPFile f : files) {
			if(f.isDirectory()) {
				html += HtmlResponse.newListLine(true, path, idsParams, f.getName());
			} else {
				html += HtmlResponse.newListLine(false, path, idsParams, f.getName());
			}
		}
		html += "</table></body></html>";
		return html;
	}
	
	/**
	 * Crée la liste des fichiers au format html
	 * @param path chemin vers le dossier de fichier à récupérer
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return la liste formatté en html
	 * @throws FtpRequestException si une erreur arrive lors de la création de la liste. Contient le code d'erreur
	 */
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
	
	
	private void recursiveRmdir(final String path, final FTPClient ftpClient) throws IOException {
		final FTPFile[] files = ftpClient.listFiles(path);
		for(final FTPFile f : files) {
			if(f.isDirectory()) {
				recursiveRmdir(path + "/" + f.getName(), ftpClient);
			} else {
				ftpClient.dele(path + "/" + f.getName());
			}
		}
		ftpClient.rmd(path);
	}
	
	/**
	 * 
	 * @param path chemin vers le dossier à supprimer (récursivement)
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @throws FtpRequestException si une erreur arrive lors de l'action. Contient le code d'erreur
	 */
	public void rmdir(final String path, final String username, final String password) throws FtpRequestException {
		try {
			final FTPClient ftpClient = getFtpClient(username, password);
			recursiveRmdir(path, ftpClient);
			ftpClient.disconnect();
		} catch (final IOException e) {
			throw new FtpRequestException(500);
		}
	}
	
	
	/**
	 * @param path chemin vers le fichier à supprimer
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @throws FtpRequestException si une erreur arrive lors de l'action. Contient le code d'erreur
	 */
	public void dele(final String path, final String username, final String password) throws FtpRequestException {
		try {
			final FTPClient ftpClient = getFtpClient(username, password);
			ftpClient.dele(path); 
			ftpClient.disconnect();
		} catch (final IOException e) {
			throw new FtpRequestException(500);
		}
	}
	
	/**
	 * Renomme le fichier ou dossier
	 * @param path chemin vers le dossier contenant le fichier ou dossier à renommer
	 * @param oldName ancien nom
	 * @param newName nouveau nom
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @throws FtpRequestException si une erreur arrive lors de l'action. Contient le code d'erreur
	 */
	public void rename(final String path, final String oldName, final String newName, final String username, final String password) throws FtpRequestException {
		try {
			final FTPClient ftpClient = getFtpClient(username, password);
			final String from = path + "/" + oldName;
			final String to = path + "/" + newName;
			
			ftpClient.rename(from, to);
			
			
			ftpClient.disconnect();
		} catch (final IOException e) {
			throw new FtpRequestException(500);
		}
	}
	
	/**
	 * Télécharge le fichier spécifié
	 * @param filepath chemin vers le fichier
	 * @param response réponse du contexte
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @return le stream dans lequel est écris le fichier
	 */
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
	
	/**
	 * Upload le fichier passé en paramètre
	 * @param attachment descripteur du fichier
	 * @param path chemin vers le dossier contenant le fichier à uploader
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @throws FtpRequestException si une erreur arrive lors de l'action. Contient le code d'erreur
	 */
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
	
	/**
	 * permet de créer un dossier
	 * @param path chemin vers le dossier dans lequel créer le nouveau dossier
	 * @param name le nom du nouveau dossier
	 * @param username nom d'utilisateur ftp
	 * @param password mot de passe ftp
	 * @throws FtpRequestException si une erreur arrive lors de l'action. Contient le code d'erreur
	 */
	public void mkdir(final String path, final String name, final String username, final String password) throws FtpRequestException {
		try {
			final FTPClient ftpClient = getFtpClient(username, password);
			final String createPath = path + "/" + name;
			
			ftpClient.mkd(createPath);
			ftpClient.disconnect();
		} catch (final IOException e) {
			throw new FtpRequestException(500);
		}
	}
}
