package org.tsdl.infrastructure.common;

import java.util.function.Consumer;

/**
 * <p>
 * Represents a consumer of an argument. It can be used to implement any generic block of code that takes a single input, returns no output and
 * potentially throws a {@link Throwable}.
 * </p>
 * <p>
 * This interface is similar to {@link Consumer}, with the added support of checked exceptions.
 * </p>
 *
 * @param <T> the type of input consumed by this consumer
 * @param <U> the type of the - potentially checked -  exception that may be thrown when executing the block of code
 */
@FunctionalInterface
public interface ThrowingConsumer<T, U extends Throwable> {
  /**
   * Performs the operation on the given argument, potentially throwing an exception.
   *
   * @param t the input argument
   * @throws U if the operation throws an exception
   */
  void accept(T t) throws U;
}
