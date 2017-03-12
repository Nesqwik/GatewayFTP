package car.tp2;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

public class FakeFTP {
	
	private static FakeFtpServer fakeFtpServer = null;
	
	private static void init() {
		fakeFtpServer = new FakeFtpServer();
		String user = "user";
		String password = "password";
		
		String rootDirectory = "/tmp/" + user; 
		fakeFtpServer.addUserAccount(new UserAccount(user, password, rootDirectory));

		UnixFakeFileSystem fileSystem = new UnixFakeFileSystem();
		fileSystem.add(new DirectoryEntry(rootDirectory + "/test"));
		fileSystem.add(new FileEntry(rootDirectory + "/testfile"));
		
		fakeFtpServer.setFileSystem(fileSystem);

        fakeFtpServer.setServerControlPort(0);
		fakeFtpServer.start();
	}
	
	public static FakeFtpServer getInstance() {
		if (fakeFtpServer == null) {
			init();
		}
		return fakeFtpServer;
	}
	
}
