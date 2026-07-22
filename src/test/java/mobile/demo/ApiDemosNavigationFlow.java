package mobile.demo;

/**
 * Business Flow: navigates from the ApiDemos main list into the "Views"
 * category, composing ApiDemosMainScreen and ViewsCategoryScreen. This is
 * the layer a real test calls - it doesn't know about locators, only about
 * the user journey.
 */
public class ApiDemosNavigationFlow {

    private final ApiDemosMainScreen mainScreen = new ApiDemosMainScreen();
    private final ViewsCategoryScreen viewsCategoryScreen = new ViewsCategoryScreen();

    public ViewsCategoryScreen openViewsCategory() {
        mainScreen.tapViewsCategory();
        return viewsCategoryScreen;
    }
}
