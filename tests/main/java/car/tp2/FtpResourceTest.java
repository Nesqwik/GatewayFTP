package car.tp2;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

public class FtpResourceTest {
	
	private String user = "user";
	private String pass = "password";
	
	@BeforeClass
    public static void setup() {
    	RestAssured.port = Integer.valueOf(8080);
        RestAssured.basePath = "/rest/tp2/ftp/";
        RestAssured.baseURI = "http://localhost";
        
        RestAssured.defaultParser = Parser.JSON;
    }
	
	@Test
	public void testGetLoginForm() throws IOException {
		expect().
			statusCode(200).
			body(containsString("username")).
			body(containsString("password")).
		when().
			get("/");
	}
	
	
	@Test
	public void testLoginSuccess() {
		given().
			queryParam("username", user).
			queryParam("password", pass).
		expect().
			statusCode(200).
		when().
			post("login");
	}
	
	@Test
	public void testLoginFailureUser() {
		user = "wrongLogin";
		
		given().
			queryParam("username", user).
			queryParam("password", pass).
		expect().
			statusCode(401).
		when().
			post("login");
	}
	
	@Test
	public void testLoginFailurePass() {
		pass = "wrongPassword";
		
		given().
			queryParam("username", user).
			queryParam("password", pass).
		expect().
			statusCode(401).
		when().
			post("login");
	}
	
	@Test
	public void testList() {
		
		System.out.println(FakeFTP.getInstance().getFileSystem());
		
		given().
			queryParam("username", user).
			queryParam("password", pass).
		expect().
			statusCode(200).
			body(containsString("testfile100")).
			body(containsString("testfolder")).
		when().
			get("list");
		
	}
	
	@Test
	public void testList2() {
		given().
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", "testfolder").
		expect().
			statusCode(200).
			body(containsString("testfile2")).
		when().
			get("list/{path}");
	}
	
	@Test
	public void testMkdirFailure() {
		// The folder testfolder already exists, mkdir will fail
		String newDir = "testfolder";
		
		given().
			formParam("name", newDir).
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", FakeFTP.rootDirectory).
		expect().
			statusCode(500).
		when().
			post("mkdir/{path}");
	}
	
	@Test
	public void testMkdirSuccess() {
		String newDir = "testfolder2";
		
		given().
			formParam("name", newDir).
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", FakeFTP.rootDirectory).
		expect().
			statusCode(200).
			body(containsString(newDir)).
		when().
			post("mkdir/{path}");
	}
	
	@Test
	public void testRmdirSuccess() {
		String dirToDelete = "testfolderRmdir";
		String path = FakeFTP.rootDirectory + "/" + dirToDelete;

		//We create a dir. We know it's safe, because we tested mkdir atomically in another test
		given().
			formParam("name", dirToDelete).
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", FakeFTP.rootDirectory).
		when().
			post("mkdir/{path}");
		
		given().
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", path).
		expect().
			statusCode(200).
			body(not(containsString(dirToDelete))).
		when().
			get("rmdir/{path}");
	}
	
	@Test
	public void testRmdirFailure() {
		String dirToDelete = "testdir2";
		String path = FakeFTP.rootDirectory + "/" + dirToDelete;
		
		// We delete a folder that doesn't exist
		given().
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", path).
		expect().
			statusCode(500).
		when().
			get("rmdir/{path}");
	}
	
	@Test
	public void testDeleSuccess() {
		String newFile = "testfiledele";
		String path = FakeFTP.rootDirectory + "/" + newFile;
		
		given().
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", path).
		expect().
			statusCode(200).
			body(not(containsString(newFile))).
		when().
			get("dele/{path}");
	}
	
	@Test
	public void testDeleFailure() {
		String newFile = "testfiledelefailure";
		String path = FakeFTP.rootDirectory + "/" + newFile;
		
		// The file doesn't exist, the dele should fail

		given().
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", path).
		expect().
			statusCode(500).
		when().
			get("dele/{path}");
	}
	
	@Test
	public void testRenameSuccess() {
		String oldName = "testfile100";
		String newName = "testfile50";
		String path = FakeFTP.rootDirectory;
		
		given().
			formParam("oldName", oldName).
			formParam("newName", newName).
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", path).
		expect().
			statusCode(200).
			body(containsString(newName)).
			body(not(containsString(oldName))).
		when().
			post("rename/{path}");
		
		given().
			formParam("oldName", newName).
			formParam("newName", oldName).
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", path).
		expect().
			statusCode(200).
			body(containsString(oldName)).
			body(not(containsString(newName))).
		when().
			post("rename/{path}");
	}
	
	@Test
	public void testRenameFailure() {
		String oldName = "testfile140";
		String newName = "testfile50";
		String path = FakeFTP.rootDirectory;
		
		// The old file doesn't exist, the rename should fail

		given().
			formParam("oldName", oldName).
			formParam("newName", newName).
			queryParam("username", user).
			queryParam("password", pass).
			pathParam("path", path).
		expect().
			statusCode(500).
		when().
			post("rename/{path}");
	}
}
