package mobile.factory;

import mobile.screens.LoginScreen;

public final class ScreenFactory {

    private ScreenFactory() {
    }

    public static LoginScreen loginScreen() {
        return new LoginScreen();
    }
}
