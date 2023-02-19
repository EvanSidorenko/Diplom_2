package order_tests;

import example.order.Order;
import example.order.OrderClient;
import example.order.OrderGenerator;
import example.user_client.User;
import example.user_client.UserClient;
import example.user_client.UserCreds;
import example.user_client.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class CreateOrderTests {
    private String accessToken;
    private OrderClient orderClient = new OrderClient();
    private OrderGenerator orderGenerator = new OrderGenerator();
    private UserGenerator userGenerator = new UserGenerator();
    private UserClient userClient = new UserClient();

    private User user;


    @Before
    public void createUser() {
         user = userGenerator.getUserWithRandomCreds();
    }
    @Test
    @DisplayName("Check order creation with auth and ingredients")
    public void checkOrderCreationWithAuth() {
        ValidatableResponse createUserResponse = userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));

        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithCorrectIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assert.assertEquals(SC_OK,actualStatusCode);
        Assert.assertEquals(createOrderResponse.extract().path("success"), true);

    }

    @Test
    @DisplayName("Check order can be created without auth and with ingredients")
    public void checkOrderCannotBeCreatedWithoutAuth() {
        ValidatableResponse createUserResponse = userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));

        accessToken = "wrongToken";

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithCorrectIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assert.assertEquals(SC_OK, actualStatusCode);
        Assert.assertEquals(createOrderResponse.extract().path("success"), true);
    }

    @Test
    @DisplayName("Check order cannot be created created with authorization and without ingredients")
    public void checkOrderCannotBeCreatedWithoutIngredients() {
        ValidatableResponse createUserResponse = userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));

        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithNoIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assert.assertEquals(SC_BAD_REQUEST, actualStatusCode);
        Assert.assertEquals(orderClient.get400ErrorWhenCreateOrderWithoutIngredients(), createOrderResponse.extract().path("message"));

    }
    @Test
    @DisplayName("Check order cannot be created created without authorization and without ingredients")
    public void checkOrderCannotBeCreatedWithoutAuthAndIngredients() {
        ValidatableResponse createUserResponse = userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));

        accessToken = "wrongToken";

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithNoIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assert.assertEquals(SC_BAD_REQUEST, actualStatusCode);
        Assert.assertEquals(orderClient.get400ErrorWhenCreateOrderWithoutIngredients(), createOrderResponse.extract().path("message"));

    }

    @Test
    @DisplayName("Check order cannot be created with wrong ingredients hash")
    public void checkOrderCannotBeCreatedWithWrongIngredientHash() {
        ValidatableResponse createUserResponse = userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));

        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithNoIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assert.assertEquals(SC_BAD_REQUEST, actualStatusCode);
        Assert.assertEquals(orderClient.get400ErrorWhenCreateOrderWithoutIngredients(), createOrderResponse.extract().path("message"));

    }


    @After
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
