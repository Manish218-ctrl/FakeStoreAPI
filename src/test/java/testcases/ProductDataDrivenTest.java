package testcases;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.testng.annotations.Test;

import io.restassured.http.ContentType;
import pojo.Product;
import routes.Routes;

public class ProductDataDrivenTest {

 

    @Test(dataProvider="productJsonDataProvider", dataProviderClass=utilities.DataProviders.class)
    public void testAddNewProduct(Map<String,String> data)
    {

        String title=data.get("title");
        double price=Double.parseDouble(data.get("price"));
        String category=data.get("category");
        String description=data.get("description");
        String image=data.get("image");

        Product newProduct=new Product(title,price,description,image,category);


        int productId=given()
                .contentType(ContentType.JSON)
                .body(newProduct)

                .when()
                .post(Routes.CREATE_PRODUCT)
                .then()
                .log().body()
                .statusCode(isOneOf(200,201))
                .body("id", notNullValue())
                .body("title", equalTo(newProduct.getTitle()))
                .extract().jsonPath().getInt("id"); 

        System.out.println("Product ID======> "+ productId);

        given()
                .pathParam("id",productId)
                .when()
                .delete(Routes.DELETE_PRODUCT)
                .then()
                .statusCode(isOneOf(200,201));

        System.out.println("Deleted Product ID======> "+ productId);
    }

}
