package user_tests;

import example.user_client.User;
import example.user_client.UserClient;
import example.user_client.UserCreds;
import example.user_client.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class LoginUserTests {
    private String accessToken;
    private UserGenerator userGenerator = new UserGenerator();
    private UserClient userClient = new UserClient();

    @Test
    @DisplayName("Check a user with valid data can login")
    public void checkUserCanLoginWithValidData() {
        User user = userGenerator.getUserWithRandomCreds();
        userClient.createUser(user);
        ValidatableResponse response = userClient.loginUser(UserCreds.from(user));

        int actualStatusCode = response.extract().statusCode();

        accessToken = response.extract().path("accessToken");

        String actualErrorMessage = response.extract().path("message");

        Assert.assertEquals(SC_OK, actualStatusCode);
        Assert.assertEquals(actualErrorMessage, true);

    }

    @Test
    @DisplayName("Check a user with wrong login and password cannot login")
    public void checkUserWithWrongLoginAndPasswordCannotLogin() {
        User user = userGenerator.getUserWithRandomCreds();
        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");

        user.setName("WrongName");
        user.setPassword("WrongPass");

        ValidatableResponse responseToLogin = userClient.loginUser(UserCreds.from(user));

        int actualStatusCode = responseToLogin.extract().statusCode();
        String actualErrorMessage = responseToLogin.extract().path("message");

        Assert.assertEquals(SC_UNAUTHORIZED, actualStatusCode);
        Assert.assertEquals(userClient.get401ErrorMessageWhenUserTriesToLoginWithWrongLoginAndPassword(), actualErrorMessage);

    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
