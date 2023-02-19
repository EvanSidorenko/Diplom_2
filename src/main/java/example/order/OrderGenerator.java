package example.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderGenerator {
    public Order getOrderWithCorrectIngredients() {
        return new Order(new ArrayList<>(Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa7a", "61c0c5a71d1f82001bdaaa79")));
    }

    public Order getOrderWithIncorrectIngredients() {
        return new Order(new ArrayList<>(Arrays.asList("!@#", "$%%^", "^%#$")));
    }

    public Order getOrderWithNoIngredients() {
        return new Order(new ArrayList<>(List.of()));
    }


}
