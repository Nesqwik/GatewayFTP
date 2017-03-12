package car.tp2;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
	
public class FtpClientFactory {

	public FTPClient newFtpInstance(final String username, final String password) throws ConnectException {
		final FTPClient client = new FTPClient();
		try {
			final String host = Constants.get("host");
			final int port = Integer.parseInt(Constants.get("port"));
			client.connect(host, port);
			client.login(username, password);
		} catch (final SocketException e) {
			throw new ConnectException("Impossible de se connecter au serveur ftp");
		} catch (final IOException e) {
			throw new ConnectException("Impossible de se connecter au serveur ftp");
		}
		
		return client;
	}
}
