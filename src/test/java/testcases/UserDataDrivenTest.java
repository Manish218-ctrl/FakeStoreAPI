package testcases;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import pojo.User;
import pojo.Name;
import pojo.Address;
import pojo.Geolocation;
import routes.Routes;

public class UserDataDrivenTest {

    @Test(dataProvider="userJsonDataProvider", dataProviderClass=utilities.DataProviders.class)
    public void testAddNewUser(Map<String,String> data)
    {
        // 1. Extract Data
        String email = data.get("email");
        String username = data.get("username");
        String password = data.get("password");
        String phone = data.get("phone");

        String firstname = data.get("firstname");
        String lastname = data.get("lastname");

        String city = data.get("city");
        String street = data.get("street");
        String zipcode = data.get("zipcode");

        String lat = data.get("lat");
        String lng = data.get("lng");

        // 2. Robust Extraction and Conversion for 'number'
        String numberString = data.get("number");

        // This check prevents the java.lang.NumberFormatException: Cannot parse null string
        if (numberString == null || numberString.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Test data error: 'number' field is missing, null, or empty for user: " + username
            );
        }

        int number = Integer.parseInt(numberString);

        // 3. Construct Nested POJO Objects
        // Matches Geolocation constructor: public Geolocation(String lat, String lng)
        Geolocation geolocation = new Geolocation(lat, lng);

        // Matches Name constructor: public Name(String firstname, String lastname)
        Name userName = new Name(firstname, lastname);

        // Matches Address constructor: public Address(String city, String street, int number, String zipcode, Geolocation geolocation)
        Address userAddress = new Address(city, street, number, zipcode, geolocation);

        // 4. Construct the Main User POJO
        // Matches User constructor: public User(String email, String username, String password, Name name, Address address, String phone)
        User newUser = new User(email, username, password, userName, userAddress, phone);


        // 5. POST Request to Create User
        int userId = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post(Routes.CREATE_USER)
                .then()
                .log().body()
                .statusCode(isOneOf(200,201))
                .body("id", notNullValue()) // Verifies the API returns a generated ID
                .extract().jsonPath().getInt("id");

        System.out.println("Created User ID======> " + userId);

        // 6. DELETE Request to Clean Up
        given()
                .pathParam("id", userId)
                .when()
                .delete(Routes.DELETE_USER)
                .then()
                .statusCode(isOneOf(200,201));

        System.out.println("Deleted User ID======> " + userId);
    }
}