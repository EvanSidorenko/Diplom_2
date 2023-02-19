package user_tests;

import example.user_client.User;
import example.user_client.UserClient;
import example.user_client.UserGenerator;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;

public class CreateUserTests {
    private String accessToken;
    private UserGenerator userGenerator = new UserGenerator();
    private UserClient userClient = new UserClient();

    @Test
    @DisplayName("Check a unique user can be created")
    public void checkUniqueUserCanBeCreated() {
        ValidatableResponse response = userClient.createUser(userGenerator.getUserWithRandomCreds());
        int actualStatusCode = response.extract().statusCode();
        accessToken = response.extract().path("accessToken");

        Assert.assertEquals(response.extract().path("success"), true);
        Assert.assertEquals(SC_OK, actualStatusCode);

    }

    @Test
    @DisplayName("Check there is 403 error and error message when trying to create a user that already exists")

    public void checkUserThatAlreadyExistsCannotBeCreated() {
        User user = userGenerator.getUniqueUserWithValidCreds();
        ValidatableResponse response = userClient.createUser(user);

        int actualStatusCode = response.extract().statusCode();
        String actualErrorMessage = response.extract().path("message");
        accessToken = response.extract().path("accessToken");

        Assert.assertEquals(SC_FORBIDDEN, actualStatusCode);
        Assert.assertEquals(actualErrorMessage, userClient.get403ErrorMessageWhenUserAlreadyExists());

    }

    @Test
    @DisplayName("Check a user cannot be created without compulsory field")
    public void checkUserCannotBeCreatedWithoutCompulsoryField() {
        User user = userGenerator.getUserWithoutPasswordField();
        ValidatableResponse response = userClient.createUser(user);

        accessToken = response.extract().path("accessToken");

        int actualStatusCode = response.extract().statusCode();
        String actualErrorMessage = response.extract().path("message");

        Assert.assertEquals(SC_FORBIDDEN, actualStatusCode);
        Assert.assertEquals(actualErrorMessage, userClient.get403ErrorMessageWhenUserIsCreatedWithoutCompulsoryField());
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
