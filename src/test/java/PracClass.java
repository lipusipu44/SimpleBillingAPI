import static com.jayway.restassured.RestAssured.*;

import com.jayway.restassured.response.Response;

/**
 * @author Anilkumar.P
 *
 */
public class PracClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Response res=given().
				contentType("application/json").

				when().
				get("http://www.simplebilling.co.in:8080/login/auth");

		String cook=res.getCookie("JSESSIONID");
		System.out.println(cook);


		res = given().
				//proxy("http://localhost:9090").
				cookie("JSESSIONID",cook).
				param("interactive_login", "true").
				param("j_username", "admin").
				param("j_password", "Webdata@123").
				param("j_client_id", "12").
				urlEncodingEnabled(true).
				expect().statusCode(302).
				when().
				post("http://www.simplebilling.co.in:8080/j_spring_security_check")
				.then()
				.extract().response();
		System.out.println(res.getCookie("JSESSIONID"));



		res=given().
				cookie("JSESSIONID",cook).
				contentType("application/json").

				when().
				get("http://www.simplebilling.co.in:8080/customer/list");
		System.out.println((res.asString()).toString());





	}

}
