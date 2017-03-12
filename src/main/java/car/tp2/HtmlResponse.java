package car.tp2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HtmlResponse {

	private static String getHtmlFile(final String path) {
		final File f = new File("html/" + path);
		try {
			return new String(Files.readAllBytes(f.toPath()));
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String unauthorized() {
		return getHtmlFile("unauthorized.html");
	}

	public static String loginForm() {
		return getHtmlFile("loginForm.html");
	}
	
	public static String getButton(final String text, final String link, final String method, final String username, final String password) {
		return "<form method=\""+method+"\" action=\""+link + "?username=" + username + "&password=" + password +"\">" +
//				"<input type=\"hidden\" name=\"username\" value=\""+username+"\">" +
//				"<input type=\"hidden\" name=\"password\" value=\""+password+"\">" +
				"<button type=\"submit\">"+text+"</button></form>";
	}
}
