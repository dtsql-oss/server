package org.tsdl.infrastructure.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.tsdl.infrastructure.model.DataPoint;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TsdlTestVisualizer implements InvocationInterceptor {
    // TODO remove .getName() as soon as SLF4J logging is introduced
    private static final Logger LOGGER = Logger.getLogger(TsdlTestVisualizer.class.getName());

    private static final String DISABLING_ENVIRONMENT_VARIABLE = "TSDL_SKIP_TEST_VISUALIZATION";

    private final TimeSeriesTestVisualizer testVisualizer = TimeSeriesTestVisualizer.INSTANCE();

    @Override // for @ParameterizedTest
    public void interceptTestTemplateMethod(Invocation<Void> invocation,
                                            ReflectiveInvocationContext<Method> invocationContext,
                                            ExtensionContext extensionContext) throws Throwable {
        visualizeTestData(invocation, invocationContext, extensionContext);
    }

    @Override // for @Test
    public void interceptTestMethod(Invocation<Void> invocation,
                                    ReflectiveInvocationContext<Method> invocationContext,
                                    ExtensionContext extensionContext) throws Throwable {
        visualizeTestData(invocation, invocationContext, extensionContext);
    }

    private void visualizeTestData(Invocation<Void> invocation,
                                   ReflectiveInvocationContext<Method> invocationContext,
                                   ExtensionContext extensionContext) throws Throwable {
        var envVariablePresent = Boolean.parseBoolean(System.getenv(DISABLING_ENVIRONMENT_VARIABLE));
        var annotationPresent = extensionContext.getRequiredTestClass().isAnnotationPresent(DisableTsdlTestVisualization.class);
        var shouldSkipVisualization = envVariablePresent || annotationPresent;
        if (shouldSkipVisualization) {
            // TODO .debug
            LOGGER.info("Skipping test visualization because environment variable '%s' is set to 'true' or '@%s' annotation is present."
              .formatted(DISABLING_ENVIRONMENT_VARIABLE, DisableTsdlTestVisualization.class.getName()));
            invocation.proceed();
            return;
        }

        var argumentSeries = extractTimeSeriesArguments(invocationContext.getArguments());
        var shortTestName = extensionContext.getRequiredTestMethod().getName();
        var longTestName = extensionContext.getRequiredTestClass().getName() + "." + shortTestName;

        var visualizationConfig = extensionContext.getRequiredTestMethod().getAnnotation(TsdlTestVisualization.class);
        var testInfo = new TsdlTestInfo(shortTestName, longTestName, argumentSeries);
        var executeTest = testVisualizer.visualizeBlocking(testInfo, visualizationConfig);
        if (executeTest) {
            invocation.proceed();
        } else {
            LOGGER.info("Test '%s' was skipped by user input in TsdlTestVisualizer dialog.".formatted(longTestName));
            invocation.skip();
        }
    }

    @SuppressWarnings("unchecked") // unchecked operation required due to type erasure
    private List<List<DataPoint>> extractTimeSeriesArguments(List<Object> arguments) {
        var dataPointArguments = new ArrayList<List<DataPoint>>();
        for (var argument : arguments) {
            if (argument instanceof List<?> listArgument && !listArgument.isEmpty() && listArgument.get(0) instanceof DataPoint) {
                dataPointArguments.add((List<DataPoint>) argument);
            }
        }
        return dataPointArguments;
    }
}