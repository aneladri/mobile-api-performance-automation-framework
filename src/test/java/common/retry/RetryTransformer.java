package common.retry;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class RetryTransformer implements IAnnotationTransformer {

    @Override
    public void transform(
            ITestAnnotation annotation,
            Class testClass,
            Constructor testConstructor,
            Method testMethod) {

        int retryCount = Integer.parseInt(
                System.getProperty("mapaf.retry.count", "0")
        );

        if (retryCount > 0) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
        }
    }
}
