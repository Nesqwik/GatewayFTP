package car.tp2;

import org.apache.commons.net.ftp.FTPClient;

public class FTPClientProvider {
	
	public FTPClientProvider() {}
	
	public static FTPClient createFTPClient() {
		return new FTPClient();
	}

}
