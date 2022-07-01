package org.tsdl.infrastructure.common;

/**
 * <p>
 * Represents an operation. It can be used to implement any generic block of code that returns nothing and potentially throws a {@link Throwable}.
 * </p>
 * <p>
 * This interface is similar to {@link Runnable}, but it is not specifically meant for the context of threading and additionally supports implementing
 * methods that throw checked exceptions.
 * </p>
 *
 * @param <T> the type of the - potentially checked -  exception that may be thrown when executing the block of code
 */
@FunctionalInterface
public interface ThrowingRunnable<T extends Throwable> {
  /**
   * Runs the block of code, potentially throwing an exception.
   *
   * @throws T if the operation throws an exception
   */
  void run() throws T;
}
