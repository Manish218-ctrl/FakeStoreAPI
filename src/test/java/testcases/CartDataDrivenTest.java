package testcases;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import pojo.Cart;
import pojo.CartProduct;
import routes.Routes;

public class CartDataDrivenTest {

    @Test(dataProvider="cartJsonDataProvider", dataProviderClass=utilities.DataProviders.class)
    public void testAddNewCart(Map<String,String> data)
    {
        String userIdString = data.get("userId");
        if (userIdString == null || userIdString.trim().isEmpty()) {
            throw new IllegalArgumentException("Test data error: 'userId' field is missing or empty.");
        }
        int userId = Integer.parseInt(userIdString.trim());
        String date = data.get("date"); 

        String productIdsString = data.get("productIds"); 
        String quantitiesString = data.get("quantities"); 

        List<CartProduct> products = new ArrayList<>();
        String[] ids = productIdsString.split(",");
        String[] quantities = quantitiesString.split(",");

        if (ids.length != quantities.length) {
            throw new IllegalArgumentException("Test data error: Product ID list and Quantity list must have the same length.");
        }

        for (int i = 0; i < ids.length; i++) {
            try {
                int productId = Integer.parseInt(ids[i].trim());
                int quantity = Integer.parseInt(quantities[i].trim());
                products.add(new CartProduct(productId, quantity));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric value in product IDs or quantities: " + e.getMessage());
            }
        }

        Cart newCart = new Cart(userId, date, products);

        int cartId = given()
                .contentType(ContentType.JSON)
                .body(newCart)
                .when()
                .post(Routes.CREATE_CART)
                .then()
                .log().body()
                .statusCode(isOneOf(200,201))
                .body("id", notNullValue()) // Verifies the API returns a generated ID
                .extract().jsonPath().getInt("id");

        System.out.println("Created Cart ID======> " + cartId);

        given()
                .pathParam("id", cartId)
                .when()
                .delete(Routes.DELETE_CART)
                .then()
                .statusCode(isOneOf(200,201));

        System.out.println("Deleted Cart ID======> " + cartId);
    }
}
