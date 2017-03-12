package car.tp2;

import io.restassured.RestAssured;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FTPClientProvider.class)
@PowerMockIgnore("javax.net.ssl.*")
public class FtpResourceTest {
	
	@BeforeClass
    public static void setup() {
    	RestAssured.port = Integer.valueOf(8080);
        RestAssured.basePath = "/rest/tp2/";
        RestAssured.baseURI = "http://localhost";
    }
	
	@Test
	public void testUserSuccess() throws IOException {
		String username = "username";
		int successCode = 331;
		
		expect().
			statusCode(successCode).
		when().
			post("ftp/user/" + username);
	}
	
	@Test
	public void testLoginSuccess() {
		String user = "user";
		String pass = "password";
		
		expect().
			statusCode(331).
		when().
			post("ftp/user/" + user);
		
		expect().
			statusCode(230).
		when().
			post("ftp/pass/" + pass);
	}
	
	@Test
	public void testLoginFailureUser() {
		String user = "wrongLogin";
		String pass = "password";
		
		expect().
			statusCode(331).
		when().
			post("ftp/user/" + user);
		
		expect().
			statusCode(530).
		when().
			post("ftp/pass/" + pass);
	}
	
	@Test
	public void testLoginFailurePass() {
		String user = "user";
		String pass = "wrongPassword";
		
		expect().
			statusCode(331).
		when().
			post("ftp/user/" + user);
		
		expect().
			statusCode(530).
		when().
			post("ftp/pass/" + pass);
	}

}
