package car.tp2.ftp;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import car.tp2.utils.Constants;
	
public class FtpClientFactory {

	public FTPClient newFtpInstance(final String username, final String password) throws ConnectException {
		final FTPClient client = new FTPClient();
		try {
			final String host = Constants.get("ftpHost");
			final int port = Integer.parseInt(Constants.get("ftpPort"));
			
			client.connect(host, port);
			final int reply = client.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)) {
		        client.disconnect();
		        return null;
			}
			
			if(Constants.get("ftpMode").equals("actif")) {
				client.enterLocalActiveMode();
			} else {
				client.enterLocalPassiveMode();
			}
			
			if (client.login(username, password)) {
				return client;
			} else {
				return null;
			}
		} catch (final SocketException e) {
			throw new ConnectException("Impossible de se connecter au serveur ftp");
		} catch (final IOException e) {
			throw new ConnectException("Impossible de se connecter au serveur ftp");
		}
	}
}
