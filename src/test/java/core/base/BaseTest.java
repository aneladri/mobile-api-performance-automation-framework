package core.base;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class BaseTest {

    @BeforeSuite
    public void beforeSuite() {

        System.out.println(
                "===== Test Execution Started ====="
        );
    }

    @AfterSuite
    public void afterSuite() {

        System.out.println(
                "===== Test Execution Completed ====="
        );
    }
}