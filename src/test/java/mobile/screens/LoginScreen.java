package mobile.screens;

import core.ai.healing.HealingBaseScreen;
import org.openqa.selenium.By;

public class LoginScreen extends HealingBaseScreen {

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login");

    public void enterUsername(String username) {
        type(usernameField, username);
    }

    public void enterPassword(String password) {
        type(passwordField, password);
    }

    public void tapLogin() {
        tap(loginButton);
    }
}
