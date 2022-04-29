package org.tsdl.infrastructure.common;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

public final class Conditions {
    private Conditions() {
    }

    public static void checkIsTrue(Condition conditionType, Boolean expression, String messageTemplate, Object... messageArguments) {
        if (!expression) {
            throw exception(conditionType, String.format(messageTemplate, messageArguments));
        }
    }

    public static void checkIsTrue(Condition conditionType, Boolean expression) {
        checkIsTrue(conditionType, expression, "Boolean expression must evaluate to true.");
    }

    public static void checkIsTrue(Condition conditionType, Supplier<Boolean> predicate, String messageTemplate, Object... messageArguments) {
        checkIsTrue(conditionType, predicate.get(), messageTemplate, messageArguments);
    }

    public static void checkIsTrue(Condition conditionType, Supplier<Boolean> predicate) {
        checkIsTrue(conditionType, predicate, "Boolean predicate must evaluate to true.");
    }

    public static void checkIsFalse(Condition conditionType, Boolean expression, String messageTemplate, Object... messageArguments) {
        checkIsTrue(conditionType, !expression, messageTemplate, messageArguments);
    }

    public static void checkIsFalse(Condition conditionType, Boolean expression) {
        checkIsFalse(conditionType, expression, "Boolean expression must evaluate to false.");
    }

    public static void checkIsFalse(Condition conditionType, Supplier<Boolean> predicate, String messageTemplate, Object... messageArguments) {
        checkIsFalse(conditionType, predicate.get(), messageTemplate, messageArguments);
    }

    public static void checkIsFalse(Condition conditionType, Supplier<Boolean> predicate) {
        checkIsFalse(conditionType, predicate, "Boolean predicate must evaluate to false.");
    }

    public static void checkEquals(Condition conditionType, Object obj1, Object obj2, String messageTemplate, Object... messageArguments) {
        checkIsTrue(conditionType, Objects.equals(obj1, obj2), messageTemplate, messageArguments);
    }

    public static void checkEquals(Condition conditionType, Object obj1, Object obj2) {
        checkEquals(conditionType, obj1, obj2, "Operands must be equal.");
    }

    public static <T> T checkNotNull(Condition conditionType, T value, String messageTemplate, Object... messageArguments) {
        checkIsTrue(conditionType, value != null, messageTemplate, messageArguments);
        return value;
    }

    public static <T> T checkNotNull(Condition conditionType, T value) {
        return checkNotNull(conditionType, value, "Operand must not be null.");
    }

    public static <T> void checkContains(Condition conditionType, Collection<T> collection, T value, String messageTemplate, Object... messageArguments) {
        checkIsTrue(conditionType, collection.contains(value), messageTemplate, messageArguments);
    }

    public static <T> void checkContains(Condition conditionType, Collection<T> collection, T value) {
        checkContains(conditionType, collection, value, "Value must be item of given collection.");
    }

    private static RuntimeException exception(Condition condition, String message) {
        return switch (condition) {
            case STATE -> new IllegalStateException(message);
            case ARGUMENT -> new IllegalArgumentException(message);
        };
    }
}
