package org.tsdl.testutil.visualization.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.testutil.visualization.api.DisableTsdlTestVisualization;
import org.tsdl.testutil.visualization.api.TimeSeriesTestVisualizer;
import org.tsdl.testutil.visualization.api.TsdlTestInfo;
import org.tsdl.testutil.visualization.api.TsdlTestVisualization;

/**
 * A JUnit5 extension intercepting the start of test methods. Used to visually preview time series data supplied to a test.
 */
@Slf4j
public class TsdlTestVisualizer implements InvocationInterceptor {
  private static final String DISABLING_ENVIRONMENT_VARIABLE = "TSDL_SKIP_TEST_VISUALIZATION";

  private final TimeSeriesTestVisualizer testVisualizer = TimeSeriesTestVisualizer.instance();

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
      log.debug("Skipping visualization of test '{}' because there is no parameter of type List<DataPoint>.", longTestName);
      invocation.proceed();
      return;
    }

    var envVariablePresent = Boolean.parseBoolean(System.getenv(DISABLING_ENVIRONMENT_VARIABLE));
    var annotationPresent = isAnnotationPresent(extensionContext.getRequiredTestClass());
    var shouldSkipVisualization = envVariablePresent || annotationPresent;
    if (shouldSkipVisualization) {
      log.debug("Skipping visualization of test '{}' because environment variable '{}' is set to 'true' or '@{}' annotation is present.",
          longTestName, DISABLING_ENVIRONMENT_VARIABLE, DisableTsdlTestVisualization.class.getSimpleName());
      invocation.proceed();
      return;
    }

    var visualizationConfig = extensionContext.getRequiredTestMethod().getAnnotation(TsdlTestVisualization.class);
    var testInfo = TsdlTestInfo.of(shortTestName, longTestName, argumentSeries);

    var executeTest = testVisualizer.visualizeBlocking(testInfo, visualizationConfig);
    if (executeTest) {
      invocation.proceed();
    } else {
      log.info("Test '{}' was skipped by user input in TsdlTestVisualizer dialog.", longTestName);
      invocation.skip();
    }
  }

  private boolean isAnnotationPresent(Class<?> innermostTestClass) {
    var clazz = innermostTestClass;
    do {
      if (clazz.isAnnotationPresent(DisableTsdlTestVisualization.class)) {
        return true;
      }
      clazz = clazz.getEnclosingClass();
    } while (clazz != null);

    return false;
  }

  @SuppressWarnings("unchecked") // unchecked operation required due to type erasure
  private List<List<DataPoint>> extractTimeSeriesArguments(List<Object> arguments) {
    var dataPointArguments = new ArrayList<List<DataPoint>>();
    for (var argument : arguments) {
      if (argument instanceof List<?> argList && !argList.isEmpty() && argList.get(0) instanceof DataPoint) {
        dataPointArguments.add((List<DataPoint>) argument);
      }
    }
    return dataPointArguments;
  }
}