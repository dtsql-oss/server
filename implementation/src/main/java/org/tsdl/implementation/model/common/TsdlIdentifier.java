package org.tsdl.implementation.model.common;

/**
 * A query element that is identifiable by some string.
 */
public interface TsdlIdentifier extends Identifiable {
  String name();

  /**
   * By default, this property returns the same value as {@link TsdlIdentifier#name()}.
   *
   * @return a representation of the {@link  Identifiable}.
   */
  @Override
  default String representation() {
    return name();
  }
}
