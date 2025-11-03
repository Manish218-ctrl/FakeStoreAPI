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

        String numberString = data.get("number");

        if (numberString == null || numberString.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Test data error: 'number' field is missing, null, or empty for user: " + username
            );
        }

        int number = Integer.parseInt(numberString);

        Geolocation geolocation = new Geolocation(lat, lng);

        Name userName = new Name(firstname, lastname);

        Address userAddress = new Address(city, street, number, zipcode, geolocation);

        User newUser = new User(email, username, password, userName, userAddress, phone);


        int userId = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post(Routes.CREATE_USER)
                .then()
                .log().body()
                .statusCode(isOneOf(200,201))
                .body("id", notNullValue())
                .extract().jsonPath().getInt("id");

        System.out.println("Created User ID======> " + userId);

        given()
                .pathParam("id", userId)
                .when()
                .delete(Routes.DELETE_USER)
                .then()
                .statusCode(isOneOf(200,201));

        System.out.println("Deleted User ID======> " + userId);
    }
}
