package car.tp2;

import io.restassured.RestAssured;

import org.junit.BeforeClass;

public class FunctionnalTest {

    @BeforeClass
    public static void setup() {
    	RestAssured.port = Integer.valueOf(8080);
        RestAssured.basePath = "/rest/tp2/";
        RestAssured.baseURI = "http://localhost";
    }

}