package org.tsdl.infrastructure.common;

/**
 * <p>
 * Represents a supplier of results. It can be used to implement any generic block of code that returns an object and potentially throws
 * a {@link Throwable}.
 * </p>
 * <p>
 * This interface is similar to {@link java.util.function.Supplier}, with the added support of checked exceptions.
 * </p>
 *
 * @param <T> the type of results supplied by this supplier
 * @param <U> the type of the - potentially checked -  exception that may be thrown when executing the block of code
 */
@FunctionalInterface
public interface ThrowingSupplier<T, U extends Throwable> {
  /**
   * Get the result, potentially throwing an exception.
   *
   * @return the result
   * @throws U if the operation throws an exception
   */
  T get() throws U;
}
