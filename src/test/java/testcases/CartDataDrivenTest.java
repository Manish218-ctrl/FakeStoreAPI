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

    // Uses the dedicated data provider for cart JSON data
    @Test(dataProvider="cartJsonDataProvider", dataProviderClass=utilities.DataProviders.class)
    public void testAddNewCart(Map<String,String> data)
    {
        // 1. Extract Data
        // Robustly parse the user ID
        String userIdString = data.get("userId");
        if (userIdString == null || userIdString.trim().isEmpty()) {
            throw new IllegalArgumentException("Test data error: 'userId' field is missing or empty.");
        }
        int userId = Integer.parseInt(userIdString.trim());
        String date = data.get("date"); // e.g., "2024-05-15"

        String productIdsString = data.get("productIds"); // e.g., "7,8"
        String quantitiesString = data.get("quantities"); // e.g., "1,1"

        // 2. Parse Nested List (Products)
        List<CartProduct> products = new ArrayList<>();
        String[] ids = productIdsString.split(",");
        String[] quantities = quantitiesString.split(",");

        if (ids.length != quantities.length) {
            throw new IllegalArgumentException("Test data error: Product ID list and Quantity list must have the same length.");
        }

        for (int i = 0; i < ids.length; i++) {
            try {
                // Trim to remove any spaces introduced by CSV/JSON formatting
                int productId = Integer.parseInt(ids[i].trim());
                int quantity = Integer.parseInt(quantities[i].trim());
                products.add(new CartProduct(productId, quantity));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric value in product IDs or quantities: " + e.getMessage());
            }
        }

        // 3. Construct the Main Cart POJO
        Cart newCart = new Cart(userId, date, products);

        // 4. POST Request to Create Cart
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

        // 5. DELETE Request to Clean Up
        given()
                .pathParam("id", cartId)
                .when()
                .delete(Routes.DELETE_CART)
                .then()
                .statusCode(isOneOf(200,201));

        System.out.println("Deleted Cart ID======> " + cartId);
    }
}
