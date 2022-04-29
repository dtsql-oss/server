package org.tsdl.infrastructure.common;

import java.util.Collection;
import java.util.function.Supplier;

public final class Conditions {
    private Conditions() {
    }

    public static void check(Condition conditionType, Boolean expression, String messageTemplate, Object... messageArguments) {
        if (!expression) {
            throw exception(conditionType, String.format(messageTemplate, messageArguments));
        }
    }

    public static void check(Condition conditionType, Boolean expression) {
        check(conditionType, expression, "");
    }

    public static void check(Condition conditionType, Supplier<Boolean> predicate, String messageTemplate, Object... messageArguments) {
        check(conditionType, predicate.get(), messageTemplate, messageArguments);
    }

    public static void check(Condition conditionType, Supplier<Boolean> predicate) {
        check(conditionType, predicate, "");
    }

    public static <T> T checkNotNull(Condition conditionType, T value, String messageTemplate, Object... messageArguments) {
        if (value == null) {
            throw exception(conditionType, String.format(messageTemplate, messageArguments));
        }
        return value;
    }

    public static <T> T checkNotNull(Condition conditionType, T value) {
        return checkNotNull(conditionType, value, "");
    }

    public static <T> void checkContains(Condition conditionType, Collection<T> collection, T value, String messageTemplate, Object... messageArguments) {
        if (!collection.contains(value)) {
            throw exception(conditionType, String.format(messageTemplate, messageArguments));
        }
    }

    public static <T> void checkContains(Condition conditionType, Collection<T> collection, T value) {
        checkContains(conditionType, collection, value, "");
    }

    private static RuntimeException exception(Condition condition, String message) {
        return switch (condition) {
            case STATE -> new IllegalStateException(message);
            case ARGUMENT -> new IllegalArgumentException(message);
        };
    }
}
