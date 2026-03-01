package ru.netology.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.db.DbUtils;
import ru.netology.page.LoginPage;
import ru.netology.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest {

    @BeforeEach
    void setup() {
        DbUtils.cleanDatabase();
        DbUtils.resetUserStatus("vasya");
    }

    @Test
    void shouldLoginUsingCodeFromDb() {

        LoginPage loginPage = open("http://localhost:9999", LoginPage.class);

        VerificationPage verificationPage =
                loginPage.validLogin("vasya", "qwerty123");

        String code = DbUtils.getAuthCode("vasya");

        verificationPage.verify(code);
    }

    @Test
    void shouldBlockUserAfterThreeInvalidPasswordAttempts() {

        for (int i = 0; i < 3; i++) {
            LoginPage loginPage = open("http://localhost:9999", LoginPage.class);
            loginPage.validLogin("vasya", "wrongpassword");
        }

        String status = DbUtils.getUserStatus("vasya");

        assertEquals("blocked", status);
    }
}
