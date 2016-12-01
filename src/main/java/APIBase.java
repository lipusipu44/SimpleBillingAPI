import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Cookies;
import com.jayway.restassured.response.Response;
import org.testng.Assert;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.with;

/**
 * Created by habisravi on 27/1/16.
 */
public class APIBase {
	//Has to come from config
	/*public static final String SSO_HOST ="http://capitalone.login.qa.247-inc.net";
	public static final String AGENT_PORTAL_HOST ="http://capitalone.portal.qa.assist.247-inc.net";
	public static final String CA_HOST ="http://capitalone.ca.qa.assist.247-inc.net";
	public static final String VS_HOST ="http://capitalone.vs.qa.assist.247-inc.net";*/

	//Build URLS
	/*public static final String SSO_LOGIN = SSO_HOST + "/sso/servlet/login";
	public static final String SSO_LOGOUT_URL = SSO_HOST + "/sso/servlet/logout";
	public static final String CONSOLE_URL = SSO_HOST + "/en/console";
	public static final String LOGIN_URL = AGENT_PORTAL_HOST + "/en/console";
	public static final String SSO_TOKEN_URL = SSO_HOST + "/sso/config/getLoginToken";
	public static final String CREATE_AGENT_SESSION_URL = AGENT_PORTAL_HOST + "/cb/as/rest/agentSession/create";
	public static final String VISITOR_URL = CA_HOST + "/en/ca/rest/checkAvailability?queueId=QUEUE_ID&accountId=ACCOUNT_ID";
	public static final String VISITOR_VS_URL = VS_HOST + "/cb/vs/rest/visitorSession/create";


	public static final String locale = "en_US";
	public static final String CLIENT_DOMAIN_NAME = "capitalone";*/

	Response res;
	String cook;

	/*String getRedirectUrl(Response response) {
		return response.getHeader("Location");
	}

	Cookies agentLoginCookie;
	Cookies jsVersionCookie;
	Cookies asId;
	Cookies visitorCookie;

	String AGENT_ID;
	String SKEY;
	String REDIRECT_URL;
	String TRACKING_ID;

	String INTERACTION_ID;
	String VISITOR_SESSION_ID;

	public void disposeInteraction() {
		String PATH = AGENT_PORTAL_HOST+"/cb/as/rest/agentParticipant/"+AGENT_ID+"/interaction/"+INTERACTION_ID+"/dispose";
		Response res = with()
				.cookies(agentLoginCookie).cookies(jsVersionCookie).cookies(asId)
				.param("_skey", SKEY)
				.param("session", AGENT_ID)
				.param("dc", "{\"Q01 What was the reason for contact (Free Text)\":\"\",\"Q02 Was the customer's issue resolved on chat\":\"A01 Yes\",\"Q03 Disposition Status\":\"A01 Closed\"}")
				.expect().statusCode(200)
				.get(PATH);

	}

	public void terminateInteraction() {
		String PATH = AGENT_PORTAL_HOST+"/cb/as/rest/agentParticipant/"+AGENT_ID+"/interaction/"+INTERACTION_ID+"/terminate";
		Response res = with()
				.cookies(agentLoginCookie).cookies(jsVersionCookie).cookies(asId)
				.param("_skey", SKEY)
				.param("session", AGENT_ID)
				.param("diagSeq", "193")
				.expect().statusCode(200)
				.get(PATH);
	}

	public static void main(String[] args) {
		new APIBase().terminateInteraction();
		new APIBase().disposeInteraction();
	}

	public void login(String username, String password, String status) {

		String result[] = getCTAndEPassword(password);

		String ePassword = result[0];
		String ct = result[1];

		Response res = given().
				param("username", username).
				param("pass", "*************").
				param("status", status).
				param("r", CONSOLE_URL).
				param("s", "breeze").
				param("password", ePassword).
				param("ct", ct).
				param("locale", locale).urlEncodingEnabled(true).
				when().
				post(SSO_LOGIN)
				.then()
				.extract().response();

		REDIRECT_URL = getRedirectUrl(res);


		SKEY = getSKey(REDIRECT_URL);

		agentLoginCookie = res.getDetailedCookies();

		Response r1 = given()
				.param("_skey", SKEY)
				.param("locale", locale)
				.param("status", status)
				.cookies(agentLoginCookie)
				.expect()
				.statusCode(200)
				.when()
				.get(LOGIN_URL);

		jsVersionCookie = r1.getDetailedCookies();

		String res1 = r1.getBody().asString();

		AGENT_ID = getAgentId(res1);
	}

	void createAgentSession(String status) {
		String REFERER = LOGIN_URL + "?_skey=" + SKEY;
		asId = given()
				.cookies(agentLoginCookie)
				.cookies(jsVersionCookie)
				.param("_skey", SKEY)
				.param("status", status)
				.param("force", "false")
				.param("agentId", AGENT_ID)
				.header("Referer", REFERER)
				.expect()
				.statusCode(200)
				.when()
				.get(CREATE_AGENT_SESSION_URL)
				.getDetailedCookies();
	}

	public String[] getCTAndEPassword(String password) {
		Response res = given()
				.param("password", password)
				.when()
				.get(SSO_TOKEN_URL)
				.then()
				.contentType(ContentType.JSON)
				.statusCode(200)
				.extract()
				.response();

		String[] result = new String[2];
		result[0] = getEPassword(res);
		result[1] = getCT(res);
		return result;
	}


	public void logout() {
		with().cookies(agentLoginCookie).param("r", LOGIN_URL).param("_skey", SKEY)
		.get(SSO_LOGOUT_URL).then().statusCode(200);
	}

	public void visitorCheckAvailability(String accountId, String queueId) {
		String COMPILED_VISITOR_URL =VISITOR_URL.replace("QUEUE_ID", queueId).replace("ACCOUNT_ID", accountId);

		Response res = with()
				.param("accountId", accountId)
				.param("queueId", queueId)
				.param("jsonp", "nemo_json_1")
				.get(COMPILED_VISITOR_URL)
				.then()
				.extract()
				.response();

		String responseString = res.getBody().asString();

		String caStatus = getCAStatus(responseString);
		Assert.assertEquals(caStatus, "true", "CA status is false");

		TRACKING_ID = getTrackingId(responseString);

		String message = getMessage(responseString);
		Assert.assertEquals(message, "CA request was successful");

	}


	public void createVisitorSession(String accountId, String queueId) {
		String visitorInfo = "{\"vi\":\"vi-857\",\"trackingId\":\"TRACKING_ID\"," +
				"\"browser\":\"Firefox\",\"browserVersion\":\"44.0\"," +
				"\"tz\":\"GMT +5:30\",\"os\":\"UNIX\",\"queue\":\"QUEUE_ID\"," +
				"\"accountId\":\"ACCOUNT_ID\"}";

		String compiledVisitorInfo = visitorInfo
				.replace("TRACKING_ID", TRACKING_ID)
				.replace("QUEUE_ID", accountId)
				.replace("ACCOUNT_ID", queueId);

		Response res = with()
				.param("CLIENT_DOMAIN_NAME", CLIENT_DOMAIN_NAME)
				.param("jsonp", "nemo_json_2")
				.param("visitorInfo", compiledVisitorInfo)
				.get(VISITOR_VS_URL)
				.then()
				.extract()
				.response();

		visitorCookie = res.getDetailedCookies();
		String responseString = res.getBody().asString();
		INTERACTION_ID = getInteractionId(responseString);
		VISITOR_SESSION_ID = getVisitorSessionId(responseString);

		String actualMessage = getMessage(responseString);

		String expectedMessage = "interaction requested by sessionId " +
				VISITOR_SESSION_ID + " was successfull. interaction " +
				INTERACTION_ID + " created";

		Assert.assertEquals(actualMessage, expectedMessage, "Failed to create interaction");

	}*/
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
