package testcases;


import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import pojo.Product;
import routes.Routes;
import utilities.ConfigReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import payloads.Payload;

import org.testng.ITestContext;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static utilities.ListUtils.isSortedAscending;
import static utilities.ListUtils.isSortedDescending;

import java.util.List;
import  utilities.ListUtils;

public class ProductTests extends BaseClass { 




    @Test
    public void testGetAllProducts()
    {
        given()

                .when()
                .get(Routes.GET_ALL_PRODUCTS)
                .then()
                .statusCode(200)
                .body("size()",greaterThan(0));

    }

    @Test
    public void testGetSingleProductById()
    {
        int productId=ConfigReader.getIntProperty("productId");

        given()
                .pathParam("id", productId)

                .when()
                .get(Routes.GET_PRODUCT_BY_ID)
                .then()
                .statusCode(200)
                .log().body();
    }


    @Test
    public void testGetLimitedProducts()
    {
        given()
                .pathParam("limit",3)

                .when()
                .get(Routes.GET_PRODUCTS_WITH_LIMIT)
                .then()
                .statusCode(200)
                .log().body()
                .body("size()",equalTo(3));
    }

    @Test
    public void testGetSortedProducts()
    {
        Response response=given()
                .pathParam("order", "desc")
                .when()
                .get(Routes.GET_PRODUCTS_SORTED)
                .then()
                .statusCode(200)
                .extract().response();

        List<Integer> productIds=response.jsonPath().getList("id", Integer.class);
        assertThat(isSortedDescending(productIds), is(true));
    }

    @Test
    public void testGetSortedProductsAsc()
    {
        Response response=given()
                .pathParam("order", "asc")
                .when()
                .get(Routes.GET_PRODUCTS_SORTED)
                .then()
                .statusCode(200)
                .extract().response();

        List<Integer> productIds=response.jsonPath().getList("id", Integer.class);
        assertThat(isSortedAscending(productIds), is(true));
    }

    @Test
    public void testGetAllCategories()
    {
        given()

                .when()
                .get(Routes.GET_ALL_CATEGORIES)
                .then()
                .statusCode(200)
                .body("size()",greaterThan(0));

    }



    @Test
    public void testGetProductsByCategory()
    {
        given()
                .pathParam("category", "electronics")

                .when()
                .get(Routes.GET_PRODUCTS_BY_CATEGORY)
                .then()
                .statusCode(200)
                .body("size()",greaterThan(0))
                .body("category", everyItem(notNullValue()))
                .body("category", everyItem(equalTo("electronics")))
                .log().body();

    }


    @Test
    public void testAddNewProduct()
    {
        Product newProduct= Payload.productPayload();


        int productId=given()
                .contentType(ContentType.JSON)
                .body(newProduct)

                .when()
                .post(Routes.CREATE_PRODUCT)
                .then()
                .log().body()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(newProduct.getTitle()))
                .extract().jsonPath().getInt("id"); //Extracting Id from response body

        System.out.println(productId);

    }

    @Test
    public void testUpdateProduct()
    {
        int productId=ConfigReader.getIntProperty("productId");

        Product updatedPayload=Payload.productPayload();

        given()
                .contentType(ContentType.JSON)
                .body(updatedPayload)
                .pathParam("id", productId)

                .when()
                .put(Routes.UPDATE_PRODUCT)
                .then()
                .log().body()
                .statusCode(200)
                .body("title", equalTo(updatedPayload.getTitle()));

    }

    @Test
    public void testDeleteProduct()
    {
        int productId= ConfigReader.getIntProperty("productId");

        given()
                .pathParam("id",productId)
                .when()
                .delete(Routes.DELETE_PRODUCT)
                .then()
                .statusCode(200);
    }
}
