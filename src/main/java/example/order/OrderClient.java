package example.order;

import example.Client;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends Client {
    public final static String CREATE_ORDER = "api/orders";

    public final static String INGREDIENT_ERROR_MESSAGE = "Ingredient ids must be provided";
    public final static String GET_LIST_OF_ORDERS_WITHOUT_AUTH_ERROR_MESSAGE = "You should be authorised";

    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(CREATE_ORDER)
                .then();
    }

    public ValidatableResponse getListOfOrders(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .when()
                .get(CREATE_ORDER)
                .then();
    }

    public String getErrorWhenIngredientsIdsMustBeProvided() {
        return INGREDIENT_ERROR_MESSAGE;
    }
    public String getErrorWhenReceiveListOfOrdersWithoutAuth() {
        return GET_LIST_OF_ORDERS_WITHOUT_AUTH_ERROR_MESSAGE;
    }

}
