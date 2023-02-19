package user_tests;

import example.user_client.User;
import example.user_client.UserClient;
import example.user_client.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class ChangedUsersDataTests {
    private String accessToken;
    private UserGenerator userGenerator = new UserGenerator();
    private UserClient userClient = new UserClient();

    @Test
    @DisplayName("Check users data can be changed with authorization")
    public void checkUsersDataCanBeChangedWithAuth() {
        User user = userGenerator.getUserWithRandomCreds();
        ValidatableResponse response = userClient.createUser(user);

        accessToken = response.extract().path("accessToken");

        ValidatableResponse responseWithChangedData = userClient.changeUsersCreds(userGenerator.getUserWithRandomCreds(), accessToken);

        int actualStatusCode = responseWithChangedData.extract().statusCode();
        String actualBodyMessage = responseWithChangedData.extract().path("success");

        Assert.assertEquals(SC_OK, actualStatusCode);
        Assert.assertEquals(actualBodyMessage, true);


    }

    @Test
    @DisplayName("Check users data cannot be changed without authorization")
    public void checkUsersDataCannotBeChangedWithoutAuth() {
        User user = userGenerator.getUserWithRandomCreds();
        ValidatableResponse response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");

        ValidatableResponse responseWithChangedData = userClient.changeUsersCreds(userGenerator.getUserWithRandomCreds(), "");
        int actualStatusResponse = responseWithChangedData.extract().statusCode();
        String actualErrorMessage = responseWithChangedData.extract().path("message");

        Assert.assertEquals(SC_UNAUTHORIZED, actualStatusResponse);
        Assert.assertEquals(actualErrorMessage, UserClient.USER_SHOULD_BE_AUTHORIZED_MESSAGE);


    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
