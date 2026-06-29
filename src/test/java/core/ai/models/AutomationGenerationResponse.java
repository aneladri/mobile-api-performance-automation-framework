package core.ai.models;

public class AutomationGenerationResponse {

    private final String content;
    private final String screenObject;
    private final String businessFlow;
    private final String testClass;
    private final String assertions;
    private final String testData;
    private final String todoItems;

    public AutomationGenerationResponse(String content) {
        this(
                content,
                "",
                "",
                "",
                "",
                "",
                ""
        );
    }

    public AutomationGenerationResponse(
            String content,
            String screenObject,
            String businessFlow,
            String testClass,
            String assertions,
            String testData,
            String todoItems
    ) {
        this.content = content;
        this.screenObject = screenObject;
        this.businessFlow = businessFlow;
        this.testClass = testClass;
        this.assertions = assertions;
        this.testData = testData;
        this.todoItems = todoItems;
    }

    public String getContent() {
        return content;
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

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }
}