package org.tsdl.testutil.visualization.impl;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.testutil.visualization.api.DisableTsdlTestVisualization;
import org.tsdl.testutil.visualization.api.TimeSeriesTestVisualizer;
import org.tsdl.testutil.visualization.api.TsdlTestInfo;
import org.tsdl.testutil.visualization.api.TsdlTestVisualization;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TsdlTestVisualizer implements InvocationInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TsdlTestVisualizer.class);

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
        var shortTestName = extensionContext.getRequiredTestMethod().getName();
        var longTestName = extensionContext.getRequiredTestClass().getName() + "." + shortTestName;

        var argumentSeries = extractTimeSeriesArguments(invocationContext.getArguments());
        if (argumentSeries.isEmpty()) {
            LOGGER.debug("Skipping visualization of test '%s' because there is no parameter of type List<DataPoint>.".formatted(longTestName));
            invocation.proceed();
            return;
        }

        var envVariablePresent = Boolean.parseBoolean(System.getenv(DISABLING_ENVIRONMENT_VARIABLE));
        var annotationPresent = extensionContext.getRequiredTestClass().isAnnotationPresent(DisableTsdlTestVisualization.class);
        var shouldSkipVisualization = envVariablePresent || annotationPresent;
        if (shouldSkipVisualization) {
            LOGGER.debug("Skipping visualization of test '%s' because environment variable '%s' is set to 'true' or '@%s' annotation is present."
              .formatted(longTestName, DISABLING_ENVIRONMENT_VARIABLE, DisableTsdlTestVisualization.class.getSimpleName()));
            invocation.proceed();
            return;
        }

        var visualizationConfig = extensionContext.getRequiredTestMethod().getAnnotation(TsdlTestVisualization.class);
        var testInfo = TsdlTestInfo.of(shortTestName, longTestName, argumentSeries);

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