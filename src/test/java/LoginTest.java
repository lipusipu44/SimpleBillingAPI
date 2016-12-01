import org.testng.annotations.Test;



/**
 * @author Anilkumar.P
 *
 */
public class LoginTest extends APIBase {


	//to be placed in the prop file, for the time being placed in test class

	String username = "admin";
	String password = "Webdata@123";
	String clientId = "12";

	@Test
	public LoginTest() {

		getJesessionID();
		loginTest(username, password);
		getCustomerList();
	}





}
