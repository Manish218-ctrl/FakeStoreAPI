package testcases;

import static io.restassured.RestAssured.given;

import java.io.InputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.module.jsv.JsonSchemaValidator;
import routes.Routes;

public class SchemaTests extends BaseClass {

    private void validateSchemaExists(String schemaPath) {
        InputStream schemaStream = getClass().getClassLoader().getResourceAsStream(schemaPath);
        Assert.assertNotNull(schemaStream, "Schema file not found in classpath: " + schemaPath);
    }

    @Test
    public void testProductSchema() {
        int productId = configReader.getIntProperty("productId");
        String schemaPath = "schemas/productSchema.json";

        // Check schema exists
        validateSchemaExists(schemaPath);

        given()
                .pathParam("id", productId)
                .when()
                .get(Routes.GET_PRODUCT_BY_ID)
                .then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
    }

    @Test
    public void testCartSchema() {
        int cartId = configReader.getIntProperty("cartId");
        String schemaPath = "schemas/cartSchema.json";

        // Check schema exists
        validateSchemaExists(schemaPath);

        given()
                .pathParam("id", cartId)
                .when()
                .get(Routes.GET_CART_BY_ID)
                .then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
    }

    @Test
    public void testUserSchema() {
        int userId = configReader.getIntProperty("userId");
        String schemaPath = "schemas/userSchema.json";

        // Check schema exists
        validateSchemaExists(schemaPath);

        given()
                .pathParam("id", userId)
                .when()
                .get(Routes.GET_USER_BY_ID)
                .then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(schemaPath));
    }
}
