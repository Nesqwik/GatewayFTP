package car.tp2.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * 
 * @author Louis GUILBERT et Jonathan LECOINTE
 *
 * HtmlResponse : 
 * Permet de générer le html nécessaire
 */
public class HtmlResponse {

	private static String getHtmlFile(final String path) {
		final File f = new File("html/" + path);
		try {
			return new String(Files.readAllBytes(f.toPath()));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @return le fichier html en cas de login incorrect
	 */
	public static String unauthorized() {
		return getHtmlFile("unauthorized.html");
	}

	/**
	 * 
	 * @return Le formulaire de connexion
	 */
	public static String loginForm() {
		return getHtmlFile("loginForm.html");
	}
	
	/**
	 * renvoie une ligne listé par ftp
	 * @param isDir vrai si la ligne est un dossier
	 * @param path chemin du fichier
	 * @param idsParams identifiants en paramètre url
	 * @param name nom du fichier
	 * @return le format html de la ligne
	 */
	public static String newListLine(final boolean isDir, final String path, final String idsParams, final String name) {
		if(isDir) {
			if(name.equals("..")) {
				return "<tr>" + 
						"<td>Dir</td>" +
						"<td><a href=\"/rest/tp2/ftp/list/" + Utils.getNormalizedPath(path + "/" + name) + idsParams + "\" >"+name+"</a></td>" + 
						"<td></td>" + 
						"<td></td>" + 
						"</tr>";
			}
			return "<tr>" + 
					"<td>Dir</td>" +
					"<td><a href=\"/rest/tp2/ftp/list/" + Utils.getNormalizedPath(path + "/" + name) + idsParams + "\" >"+name+"</a></td>" + 
					"<td><a href=\"/rest/tp2/ftp/rmdir/" + Utils.getNormalizedPath(path + "/" + name) + idsParams + "\">delete</a></td>" + 
					"<td>"+ renameForm(name, path, idsParams) +"</td>" + 
					"</tr>";
		} else {
			return "<tr>" + 
					"<td>File</td>" +
					"<td><a href=\"/rest/tp2/ftp/download/" + Utils.getNormalizedPath(path + "/" + name) + idsParams + "\" >"+name+"</a></td>" + 
					"<td><a href=\"/rest/tp2/ftp/dele/" + Utils.getNormalizedPath(path + "/" + name) + idsParams + "\">delete</a></td>" + 
					"<td>"+ renameForm(name, path, idsParams) +"</td>" + 
					"</tr>";
		}
	}
	
	private static String renameForm(final String name, final String path, final String idsParams) {
		return "<form action=\"/rest/tp2/ftp/rename/" + path + idsParams + "\" method=\"POST\">" + 
					"<input type=\"hidden\" name=\"oldName\" value=\"" + name + "\" />" + 
					"<input type=\"text\" name=\"newName\" />" +
					"<input type=\"submit\" value=\"renommer\" />" + 
				"</form>";
	}
	
	/**
	 * génère le formulaire de création de dossier
	 * @param path chemin du dossier
	 * @param idsParams identifiants en paramètre url
	 * @return le format html du formulaire
	 */
	public static String newDirForm(final String path,  final String idsParams) {
		return "<form action=\"/rest/tp2/ftp/mkdir/" + path + idsParams + "\" method=\"POST\">" + 
				"<input type=\"text\" name=\"name\" />" +
				"<input type=\"submit\" value=\"Créer dossier\" />" + 
			"</form>";
	}
	
	/**
	 * Génère le formulaire d'upload de fichier
	 * @param path
	 * @param idsParams identifiants en paramètre url
	 * @return le format html du formulaire
	 */
	public static String uploadForm(final String path, final String idsParams) {
		return "<form action=\"/rest/tp2/ftp/upload/" + path + idsParams + "\" method=\"post\" enctype=\"multipart/form-data\">" +
					"Select a file : <input type=\"file\" name=\"file\" size=\"45\" />" + 
					"<input type=\"submit\" value=\"Envoyer fichier\" />" + 
				"</form>";
	}
}
