
package testcases;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payloads.Payload;
import pojo.Cart;
import routes.Routes;
import utilities.ConfigReader;
import static utilities.ListUtils.isSortedAscending;
import static utilities.ListUtils.isSortedDescending;

import io.qameta.allure.Description;
import io.qameta.allure.Step;

public class CartTests extends BaseClass {

    @Test
    @Description("Verify that retrieving all carts returns a non-empty list")
    public void testGetAllCarts() {
        getAllCartsStep();
    }
 
    @Step("Sending GET request to retrieve all carts")
    private void getAllCartsStep() {
        given()
                .when()
                .get(Routes.GET_ALL_CARTS)
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    @Description("Verify retrieving a cart by ID returns the correct cart")
    public void testGetCartById() {
        int cartId = ConfigReader.getIntProperty("cartId");
        getCartByIdStep(cartId);
    }

    @Step("Sending GET request to retrieve cart with ID {cartId}")
    private void getCartByIdStep(int cartId) {
        given()
                .pathParam("id", cartId)
                .when()
                .get(Routes.GET_CART_BY_ID)
                .then()
                .statusCode(200)
                .log().body()
                .body("id", equalTo(cartId));
    }

    @Test
    @Description("Verify retrieving carts within a date range returns valid dates")
    public void testGetCartsByDateRange() {
        String startDate = ConfigReader.getProperty("startdate");
        String endDate = ConfigReader.getProperty("enddate");
        getCartsByDateRangeStep(startDate, endDate);
    }

    @Step("Retrieving carts from {startDate} to {endDate}")
    private void getCartsByDateRangeStep(String startDate, String endDate) {
        Response response = given()
                .pathParam("startdate", startDate)
                .pathParam("enddate", endDate)
                .when()
                .get(Routes.GET_CARTS_BY_DATE_RANGE)
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .extract().response();

        List<String> cartDates = response.jsonPath().getList("date");
        assertThat(validateCartDatesWithinRange(cartDates, startDate, endDate), is(true));
    }

    @Test
    @Description("Verify retrieving carts for a specific user returns only that user's carts")
    public void testGetUserCart() {
        int userId = ConfigReader.getIntProperty("userId");
        getUserCartStep(userId);
    }

    @Step("Retrieving carts for user ID {userId}")
    private void getUserCartStep(int userId) {
        given()
                .pathParam("userId", userId)
                .when()
                .get(Routes.GET_USER_CART)
                .then()
                .statusCode(200)
                .body("userId", everyItem(equalTo(userId)));
    }

    @Test
    @Description("Verify retrieving carts with a limit returns no more than the limit")
    public void testGetCartsWithLimit() {
        int limit = ConfigReader.getIntProperty("limit");
        getCartsWithLimitStep(limit);
    }

    @Step("Retrieving up to {limit} carts")
    private void getCartsWithLimitStep(int limit) {
        given()
                .pathParam("limit", limit)
                .when()
                .get(Routes.GET_CARTS_WITH_LIMIT)
                .then()
                .statusCode(200)
                .body("size()", lessThanOrEqualTo(limit));
    }

    @Test
    @Description("Verify retrieving carts sorted in descending order by ID")
    public void testGetCartsSorted() {
        getCartsSortedStep("desc");
    }

    @Step("Retrieving carts sorted in {order} order")
    private void getCartsSortedStep(String order) {
        Response response = given()
                .pathParam("order", order)
                .when()
                .get(Routes.GET_CARTS_SORTED)
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .extract().response();

        List<Integer> cartIds = response.jsonPath().getList("id", Integer.class);
        if(order.equals("desc")) {
            assertThat(isSortedDescending(cartIds), is(true));
        } else {
            assertThat(isSortedAscending(cartIds), is(true));
        }
    }

    @Test
    @Description("Verify retrieving carts sorted in ascending order by ID")
    public void testGetCartsSortedAsc() {
        getCartsSortedStep("asc");
    }

    @Test
    @Description("Verify creating a new cart works correctly")
    public void testCreateCart() {
        int userId = ConfigReader.getIntProperty("userId");
        Cart newCart = Payload.cartPayload(userId);
        createCartStep(newCart);
    }

    @Step("Creating a new cart for user ID {cart.userId}")
    private void createCartStep(Cart cart) {
        given()
                .contentType(ContentType.JSON)
                .body(cart)
                .when()
                .post(Routes.CREATE_CART)
                .then()
                .statusCode(201)
                .log().body()
                .body("id", notNullValue())
                .body("userId", notNullValue())
                .body("products.size()", greaterThan(0));
    }

    @Test
    @Description("Verify updating a cart works correctly")
    public void testUpdateCart() {
        int userId = ConfigReader.getIntProperty("userId");
        int cartId = ConfigReader.getIntProperty("cartId");
        Cart updateCart = Payload.cartPayload(userId);
        updateCartStep(cartId, updateCart);
    }

    @Step("Updating cart ID {cartId}")
    private void updateCartStep(int cartId, Cart cart) {
        given()
                .pathParam("id", cartId)
                .contentType(ContentType.JSON)
                .body(cart)
                .when()
                .put(Routes.UPDATE_CART)
                .then()
                .statusCode(200)
                .body("id", equalTo(cartId))
                .body("userId", notNullValue())
                .body("products.size()", equalTo(1));
    }

    @Test
    @Description("Verify deleting a cart works correctly")
    public void testDeleteCart() {
        int cartId = ConfigReader.getIntProperty("cartId");
        deleteCartStep(cartId);
    }

    @Step("Deleting cart ID {cartId}")
    private void deleteCartStep(int cartId) {
        given()
                .pathParam("id", cartId)
                .when()
                .delete(Routes.DELETE_CART)
                .then()
                .statusCode(200);
    }

    public boolean validateCartDatesWithinRange(List<String> cartDates, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        for (String dateStr : cartDates) {
            String cleanDate = dateStr.length() >= 10 ? dateStr.substring(0, 10) : dateStr;
            LocalDate cartDate = LocalDate.parse(cleanDate, formatter);

            if (cartDate.isBefore(start) || cartDate.isAfter(end)) {
                return false;
            }
        }

        return true;
    }
}

