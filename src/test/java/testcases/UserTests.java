package testcases;


    import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payloads.Payload;
import pojo.User;
import routes.Routes;
    import utilities.ConfigReader;


public class UserTests extends BaseClass
{

    @Test
    public void testGetAllUsers()
    {
        given()
                .when()

                .get(Routes.GET_ALL_USERS)

                .then()

                .statusCode(200)
                .log().body()
                .contentType(ContentType.JSON)
                .body("size()",greaterThan(0));

    }

    @Test
    public void testGetUserById()
    {
        int userId=ConfigReader.getIntProperty("userId");
        given()
                .pathParam("id",userId)
                .when()
                .get(Routes.GET_USER_BY_ID)
                .then()
                .log().body()
                .statusCode(200);
    }


    @Test
    public void testGetUsersWithLimit()
    {
        int limit=ConfigReader.getIntProperty("limit");
        given()
                .pathParam("limit",limit)
                .when()
                .get(Routes.GET_USERS_WITH_LIMIT)
                .then()
                .statusCode(200)
                .log().body()
                .body("size()",equalTo(limit));

    }

    @Test
    void testGetUsersSorted()
    {
        Response response=given()
                .pathParam("order", "desc")
                .when()
                .get(Routes.GET_USERS_SORTED)
                .then()
                .statusCode(200)
                .extract().response();

        List<Integer> userIds=response.jsonPath().getList("id", Integer.class);


        assertThat(isSortedDescending(userIds), is(true));
    }

    @Test
    void testGetUsersSortedAsc()
    {
        Response response=given()
                .pathParam("order", "asc")
                .when()
                .get(Routes.GET_USERS_SORTED)
                .then()
                .statusCode(200)
                .extract().response();

        List<Integer> userIds=response.jsonPath().getList("id", Integer.class);


        assertThat(isSortedAscending(userIds), is(true));
    }




    @Test
    public void testCreateUser()
    {
        User newUser=Payload.userPayload();

        int id=given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post(Routes.CREATE_USER)
                .then()
                .log().body()
                .statusCode(201)
                .body("id", notNullValue())
                .extract().jsonPath().getInt("id");

        System.out.println("Generated UserID=====:"+ id);

    }


    @Test
    public void testUpdateUser()
    {
        int userId=ConfigReader.getIntProperty("userId");

        User updateUser=Payload.userPayload();

        given()
                .contentType(ContentType.JSON)
                .pathParam("id", userId)
                .body(updateUser)
                .when()
                .put(Routes.UPDATE_USER)
                .then()
                .log().body()
                .statusCode(200)
                .body("username",equalTo(updateUser.getUsername()));

    }


    @Test
    void testDeleteUser()
    {

        int userId= ConfigReader.getIntProperty("userId");

        given()
                .pathParam("id", userId)
                .when()
                .delete(Routes.DELETE_USER)
                .then()
                .statusCode(200);
    }




}




