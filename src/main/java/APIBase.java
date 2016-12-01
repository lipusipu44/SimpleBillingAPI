import static com.jayway.restassured.RestAssured.given;

import com.jayway.restassured.response.Response;


/**
 * @author Anilkumar.P
 *
 */
public class APIBase {


	Response res;
	String cook;

	public void getJesessionID(){
		res=given().
				contentType("application/json").

				when().
				get("http://www.simplebilling.co.in:8080/login/auth");

		cook=res.getCookie("JSESSIONID");
		System.out.println(cook);
	}

	public void loginTest(String username, String password){
		res = given().
				//proxy("http://localhost:9090").
				cookie("JSESSIONID",cook).
				param("interactive_login", "true").
				param("j_username", username).
				param("j_password", password).
				param("j_client_id", "12").
				urlEncodingEnabled(true).
				expect().statusCode(302).
				when().
				post("http://www.simplebilling.co.in:8080/j_spring_security_check")
				.then()
				.extract().response();
		System.out.println(res.getCookie("JSESSIONID"));
	}

	public void getCustomerList(){
		res=given().
				cookie("JSESSIONID",cook).
				contentType("application/json").

				when().
				get("http://www.simplebilling.co.in:8080/customer/list");
		System.out.println((res.asString()).toString());
	}

}
