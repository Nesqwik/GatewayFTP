package car.tp2;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FTPClientProvider.class)
@PowerMockIgnore("javax.net.ssl.*")
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
			statusCode(230).
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
		given().
			queryParam("username", user).
			queryParam("password", pass).
		expect().
			statusCode(200).
			body(containsString("testfile")).
			body(containsString("testfolder")).
		when().
			get("list");
		
	}
	
	@Test
	public void testList2() {
		given().
			queryParam("username", user).
			queryParam("password", pass).
		expect().
			statusCode(200).
			body(containsString("testfile2")).
		when().
			get("list/testfolder");
	}
}
