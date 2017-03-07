package car.tp2;

import org.junit.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;


public class FtpResourceTest  {
	
	@Test
	public void testUserSuccess() {
		//TODO : mock 
		String username = "anonymous";
		expect().
			body("code", equalTo(331)).
			body("data", equalTo(null)).
		when().
			post("http://localhost:8080/rest/tp2/ftp/user/" + username);
	}
	
	@Test
	public void testUserFailure() {
		//TODO : mock
		String username = "notACorrectUsername";
		expect().
			body("code", equalTo(530)).
			body("data", equalTo(null)).
		when().
			post("http://localhost:8080/rest/tp2/ftp/user/" + username);
	}

}
