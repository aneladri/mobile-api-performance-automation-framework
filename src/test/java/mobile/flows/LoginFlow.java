package mobile.flows;

import mobile.screens.LoginScreen;

public class LoginFlow {

    private final LoginScreen loginScreen;

    public LoginFlow() {
        this.loginScreen = new LoginScreen();
    }

    public void login(String username, String password) {
        loginScreen.enterUsername(username);
        loginScreen.enterPassword(password);
        loginScreen.tapLogin();
    }
}
