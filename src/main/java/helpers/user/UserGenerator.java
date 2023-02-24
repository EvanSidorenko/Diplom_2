package helpers.user;

import com.github.javafaker.Faker;

public class UserGenerator {
    Faker faker = new Faker();

    public User getUserWithRandomCreds() {
        return new User(faker.internet().emailAddress(), faker.internet().password(), faker.name().username());
    }

    public User getUniqueUserWithValidCreds() {
        return new User("test@email.ru", "trytohackme", "TestName");

    }

    public User getUserWithoutPasswordField() {
        return new User(faker.internet().emailAddress(), null, faker.name().username());
    }
}
