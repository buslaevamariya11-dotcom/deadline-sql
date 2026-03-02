package ru.netology.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.db.DbUtils;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.VerificationPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginTest {

    @AfterAll
    static void cleanDb() {
        DbUtils.cleanDatabase();
    }

    @Test
    void shouldLoginUsingCodeFromDb() {

        DataHelper.AuthInfo authInfo = DataHelper.getValidAuthInfo();

        LoginPage loginPage = open("http://localhost:9999", LoginPage.class);

        VerificationPage verificationPage =
                loginPage.validLogin(authInfo.getLogin(), authInfo.getPassword());

        String code = DbUtils.getAuthCode(authInfo.getLogin());

        DashboardPage dashboardPage = verificationPage.verify(code);

        assertTrue(dashboardPage.isPageOpened());
    }

    @Test
    void shouldBlockUserAfterThreeInvalidPasswordAttempts() {

        DataHelper.AuthInfo invalidAuth = DataHelper.getInvalidAuthInfo();

        for (int i = 0; i < 3; i++) {
            LoginPage loginPage = open("http://localhost:9999", LoginPage.class);
            loginPage.validLogin(invalidAuth.getLogin(), invalidAuth.getPassword());
        }

        String status = DbUtils.getUserStatus(invalidAuth.getLogin());

        assertEquals("blocked", status);
    }
}
