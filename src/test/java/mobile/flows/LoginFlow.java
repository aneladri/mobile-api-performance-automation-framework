package mobile.flows;

import mobile.screens.LoginScreen;
import mobile.factory.ScreenFactory;

public class LoginFlow {

    private final LoginScreen loginScreen;

    public LoginFlow() {
        this.loginScreen = ScreenFactory.loginScreen();
    }

    public void login(String username, String password) {
        loginScreen.enterUsername(username);
        loginScreen.enterPassword(password);
        loginScreen.tapLogin();
    }
}
