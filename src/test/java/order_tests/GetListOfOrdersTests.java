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
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetListOfOrdersTests {
    private String accessToken;
    private OrderClient orderClient = new OrderClient();
    private OrderGenerator orderGenerator = new OrderGenerator();
    private UserGenerator userGenerator = new UserGenerator();
    private UserClient userClient = new UserClient();
    private  ValidatableResponse createUserResponse;

    @Test
    @DisplayName("Check there is list of orders when user is authorized")
    public void checkUsersListOfOrdersWithAuth() {
        User user = userGenerator.getUserWithRandomCreds();
        createUserResponse= userClient.createUser(user);
        ValidatableResponse loginResponse = userClient.loginUser(UserCreds.from(user));
        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse getOrderListResponse = orderClient.getListOfOrders(accessToken);
        int actualGetOrderResponseStatusCode = getOrderListResponse.extract().statusCode();

        Assert.assertEquals(SC_OK, actualGetOrderResponseStatusCode);
        String actualOrderBody = String.valueOf(getOrderListResponse.assertThat().body("orders", notNullValue()));

    }

    @Test
    @DisplayName("Check there is no list of orders when user is not authorized")
    public void checkThereIsNoListOfOrdersWhenUserIsNotAuthorized() {

        accessToken = "wrongToken";

        ValidatableResponse getOrderListResponse = orderClient.getListOfOrders(accessToken);
        int actualGetOrderResponseStatusCode = getOrderListResponse.extract().statusCode();
        String actualBodyMessage = getOrderListResponse.extract().path("message");

        Assert.assertEquals(SC_UNAUTHORIZED, actualGetOrderResponseStatusCode);
        Assert.assertEquals(orderClient.getErrorWhenReceiveListOfOrdersWithoutAuth(), actualBodyMessage);

    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
