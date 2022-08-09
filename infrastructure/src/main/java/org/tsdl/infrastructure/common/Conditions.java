package org.tsdl.infrastructure.common;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import lombok.NonNull;

/**
 * Provides utility methods verifying state and/or method arguments.
 */
public final class Conditions {
  private Conditions() {
  }

  public static void checkIsTrue(Condition conditionType, boolean expression, String messageTemplate, Object... messageArguments) {
    if (!expression) {
      throw exception(conditionType, String.format(messageTemplate, messageArguments));
    }
  }

  public static void checkIsTrue(Condition conditionType, Boolean expression) {
    checkIsTrue(conditionType, expression, "Boolean expression must evaluate to true.");
  }

  public static void checkIsTrue(Condition conditionType, BooleanSupplier predicate, String messageTemplate, Object... messageArguments) {
    checkIsTrue(conditionType, predicate.getAsBoolean(), messageTemplate, messageArguments);
  }

  public static void checkIsTrue(Condition conditionType, BooleanSupplier predicate) {
    checkIsTrue(conditionType, predicate, "Boolean predicate must evaluate to true.");
  }

  public static void checkIsFalse(Condition conditionType, Boolean expression, String messageTemplate, Object... messageArguments) {
    checkIsTrue(conditionType, !expression, messageTemplate, messageArguments);
  }

  public static void checkIsFalse(Condition conditionType, Boolean expression) {
    checkIsFalse(conditionType, expression, "Boolean expression must evaluate to false.");
  }

  public static void checkIsFalse(Condition conditionType, BooleanSupplier predicate, String messageTemplate, Object... messageArguments) {
    checkIsFalse(conditionType, predicate.getAsBoolean(), messageTemplate, messageArguments);
  }

  public static void checkIsFalse(Condition conditionType, BooleanSupplier predicate) {
    checkIsFalse(conditionType, predicate, "Boolean predicate must evaluate to false.");
  }

  public static void checkEquals(Condition conditionType, Object obj1, Object obj2, String messageTemplate, Object... messageArguments) {
    checkIsTrue(conditionType, Objects.equals(obj1, obj2), messageTemplate, messageArguments);
  }

  public static void checkEquals(Condition conditionType, Object obj1, Object obj2) {
    checkEquals(conditionType, obj1, obj2, "Operands must be equal.");
  }

  public static void checkNull(Condition conditionType, Object value, String messageTemplate, Object... messageArguments) {
    checkIsTrue(conditionType, value == null, messageTemplate, messageArguments);
  }

  public static void checkNull(Condition conditionType, Object value) {
    checkNull(conditionType, value, "Operand must be null.");
  }

  @NonNull
  public static <T> T checkNotNull(Condition conditionType, T value, String messageTemplate, Object... messageArguments) {
    checkIsTrue(conditionType, value != null, messageTemplate, messageArguments);
    return value;
  }

  @NonNull
  public static <T> T checkNotNull(Condition conditionType, T value) {
    return checkNotNull(conditionType, value, "Operand must not be null.");
  }

  public static <T> void checkContains(Condition conditionType, Collection<T> collection, T value, String messageTemplate,
                                       Object... messageArguments) {
    checkIsTrue(conditionType, collection.contains(value), messageTemplate, messageArguments);
  }

  public static <T> void checkContains(Condition conditionType, Collection<T> collection, T value) {
    checkContains(conditionType, collection, value, "Value must be item of given collection.");
  }

  public static void checkValidIndex(Condition conditionType, Collection<?> collection, int index, String messageTemplate,
                                     Object... messageArguments) {
    checkIsTrue(conditionType, index >= 0 && collection.size() > index, messageTemplate, messageArguments);
  }

  public static void checkValidIndex(Condition conditionType, Collection<?> collection, int index) {
    checkIsTrue(conditionType, index >= 0 && collection.size() > index, "Index must be within collection range.");
  }

  public static void checkIsGreaterThanOrEqual(Condition conditionType, int i1, int i2, String messageTemplate, Object... messageArguments) {
    checkIsGreaterThanOrEqual(conditionType, i1, (long) i2, messageTemplate, messageArguments);
  }

  public static void checkIsGreaterThanOrEqual(Condition conditionType, int i1, int i2) {
    checkIsGreaterThanOrEqual(conditionType, i1, (long) i2);
  }

  public static void checkIsGreaterThanOrEqual(Condition conditionType, long i1, long i2, String messageTemplate, Object... messageArguments) {
    checkIsTrue(conditionType, i1 >= i2, messageTemplate, messageArguments);
  }

  public static void checkIsGreaterThanOrEqual(Condition conditionType, long i1, long i2) {
    checkIsTrue(conditionType, i1 >= i2, "First integer must be greater than second.");
  }

  public static void checkIsGreaterThan(Condition conditionType, int i1, int i2, String messageTemplate, Object... messageArguments) {
    checkIsGreaterThan(conditionType, i1, (long) i2, messageTemplate, messageArguments);
  }

  public static void checkIsGreaterThan(Condition conditionType, int i1, int i2) {
    checkIsGreaterThan(conditionType, i1, Long.valueOf(i2));
  }

  public static void checkIsGreaterThan(Condition conditionType, long i1, long i2, String messageTemplate, Object... messageArguments) {
    checkIsTrue(conditionType, i1 > i2, messageTemplate, messageArguments);
  }

  public static void checkIsGreaterThan(Condition conditionType, long i1, long i2) {
    checkIsTrue(conditionType, i1 > i2, "First integer must be greater than second.");
  }

  public static void checkSizeExactly(Condition conditionType, Object[] array, int requiredSize, String messageTemplate, Object... messageArguments) {
    checkIsTrue(conditionType, Objects.equals(array.length, requiredSize), messageTemplate, messageArguments);
  }

  public static void checkSizeExactly(Condition conditionType, Object[] array, int requiredSize) {
    checkIsTrue(conditionType, Objects.equals(array.length, requiredSize), "Collection size must be equactly %s", requiredSize);
  }

  public static void checkSizeExactly(Condition conditionType, Collection<?> collection, int requiredSize, String messageTemplate,
                                      Object... messageArguments) {
    checkIsTrue(conditionType, Objects.equals(collection.size(), requiredSize), messageTemplate, messageArguments);
  }

  public static void checkSizeExactly(Condition conditionType, Collection<?> collection, int requiredSize) {
    checkIsTrue(conditionType, Objects.equals(collection.size(), requiredSize), "Collection size must be equactly %s", requiredSize);
  }

  public static RuntimeException exception(Condition condition, String messageTemplate, Object... messageArguments) {
    return exception(condition, String.format(messageTemplate, messageArguments));
  }

  private static RuntimeException exception(Condition condition, String message) {
    return switch (condition) {
      case STATE -> new IllegalStateException(message);
      case ARGUMENT -> new IllegalArgumentException(message);
    };
  }
}
