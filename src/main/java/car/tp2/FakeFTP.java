package car.tp2;

import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

public class FakeFTP {
	
	private static FakeFtpServer fakeFtpServer = null;
	
	private static String user = "user";
	private static String password = "password";
	public static String rootDirectory = "/tmp/" + user;
	
	private static void init() {
		fakeFtpServer = new FakeFtpServer();
		fakeFtpServer.addUserAccount(new UserAccount(user, password, rootDirectory));

		UnixFakeFileSystem fileSystem = new UnixFakeFileSystem();
		
		fileSystem.add(new DirectoryEntry(rootDirectory + "/testfolder"));
		fileSystem.add(new FileEntry(rootDirectory + "/testfile"));
		fileSystem.add(new FileEntry(rootDirectory + "/testfile100"));
		fileSystem.add(new FileEntry(rootDirectory + "/testfolder" + "/testfile2"));
		
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
