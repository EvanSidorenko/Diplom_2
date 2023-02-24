package order_tests;

import helpers.order.OrderClient;
import helpers.order.OrderGenerator;
import helpers.user.User;
import helpers.user.UserClient;
import helpers.user.UserCreds;
import helpers.user.UserGenerator;
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
    ValidatableResponse createUserResponse;

    @Before
    public void createUser() {
         user = userGenerator.getUserWithRandomCreds();
         createUserResponse = userClient.createUser(user);
    }
    @Test
    @DisplayName("Check order creation with auth and ingredients")
    public void checkOrderCreationWithAuth() {
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
        accessToken = "wrongToken";

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithCorrectIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assert.assertEquals(SC_OK, actualStatusCode);
        Assert.assertEquals(createOrderResponse.extract().path("success"), true);
    }

    @Test
    @DisplayName("Check order cannot be created created with authorization and without ingredients")
    public void checkOrderCannotBeCreatedWithoutIngredients() {
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));

        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithNoIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();
        String actualBodyMessage = createOrderResponse.extract().path("message");


        Assert.assertEquals(SC_BAD_REQUEST, actualStatusCode);
        Assert.assertEquals(orderClient.getErrorWhenIngredientsIdsMustBeProvided(), actualBodyMessage);

    }
    @Test
    @DisplayName("Check order cannot be created created without authorization and without ingredients")
    public void checkOrderCannotBeCreatedWithoutAuthAndIngredients() {
        accessToken = "wrongToken";

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithNoIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assert.assertEquals(SC_BAD_REQUEST, actualStatusCode);
        Assert.assertEquals(orderClient.getErrorWhenIngredientsIdsMustBeProvided(), createOrderResponse.extract().path("message"));

    }

    @Test
    @DisplayName("Check order cannot be created with wrong ingredients hash")
    public void checkOrderCannotBeCreatedWithWrongIngredientHash() {
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));

        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderGenerator.getOrderWithIncorrectIngredients(), accessToken);
        int actualStatusCode = createOrderResponse.extract().statusCode();

        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, actualStatusCode);

    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
