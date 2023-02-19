package example.user_client;

import example.Client;
import io.restassured.response.ValidatableResponse;
import org.codehaus.groovy.transform.tailrec.VariableAccessReplacer;

import static io.restassured.RestAssured.given;

public class UserClient extends Client {
    public static final String REGISTER_URL = "api/auth/register";
    public static final String LOGIN_URL = "api/auth/login";

    public static final String UPDATE_PROFILE_URL = "api/auth/user";

    public static final String USER_ALREADY_EXISTS_MESSAGE = "User already exists";
    public static final String USER_SHOULD_HAVE_REQUIRED_FIELDS_MESSAGE = "Email, password and name are required fields";
    public static final String USER_EMAIL_OR_PASSWORD_WRONG_MESSAGE = "email or password are incorrect";

    public static final String USER_SHOULD_BE_AUTHORIZED_MESSAGE = "You should be authorised";


    public ValidatableResponse createUser(User user) {
        return given().log().all()
                .spec(getSpec())
                .body(user)
                .when()
                .post(REGISTER_URL)
                .then();

    }

    public ValidatableResponse loginUser(UserCreds userCreds) {
        return given()
                .spec(getSpec())
                .body(userCreds)
                .when()
                .post(LOGIN_URL)
                .then();
    }

    public ValidatableResponse changeUsersCreds(User user, String accessToken) {
        return given().log().all()
                .header("Authorization", accessToken)
                .spec(getSpec())
                .body(user)
                .when()
                .patch(UPDATE_PROFILE_URL)
                .then();

    }

    public ValidatableResponse deleteUser(String accessToken) {
        return given().log().all()
                .header("Authorization", accessToken)
                .spec(getSpec())
                .when()
                .delete(UPDATE_PROFILE_URL)
                .then();


    }

    public String get403ErrorMessageWhenUserAlreadyExists() {
        return USER_ALREADY_EXISTS_MESSAGE;
    }

    public String get403ErrorMessageWhenUserIsCreatedWithoutCompulsoryField() {
        return USER_SHOULD_HAVE_REQUIRED_FIELDS_MESSAGE;
    }

    public String get401ErrorMessageWhenUserTriesToLoginWithWrongLoginAndPassword() {
        return USER_EMAIL_OR_PASSWORD_WRONG_MESSAGE;
    }
    public String get401ErrorMessageWhenUserIsNotAuthorized() {
        return USER_SHOULD_BE_AUTHORIZED_MESSAGE;
    }

}
