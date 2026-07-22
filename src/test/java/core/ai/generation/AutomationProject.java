package core.ai.generation;

public class AutomationProject {

    private final String screenObject;
    private final String businessFlow;
    private final String testClass;
    private final String assertions;
    private final String testData;
    private final String todoItems;

    public AutomationProject(
            String screenObject,
            String businessFlow,
            String testClass,
            String assertions,
            String testData,
            String todoItems
    ) {
        this.screenObject = screenObject;
        this.businessFlow = businessFlow;
        this.testClass = testClass;
        this.assertions = assertions;
        this.testData = testData;
        this.todoItems = todoItems;
    }

    public String getScreenObject() {
        return screenObject;
    }

    public String getBusinessFlow() {
        return businessFlow;
    }

    public String getTestClass() {
        return testClass;
    }

    public String getAssertions() {
        return assertions;
    }

    public String getTestData() {
        return testData;
    }

    public String getTodoItems() {
        return todoItems;
    }
}
