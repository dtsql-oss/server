package org.tsdl.implementation.model.common;

/**
 * A component formatting a TSDL query component to be printed, invoked by a {@link TsdlFormattable} component.
 *
 * @param <T> type of the component to format
 */
public interface TsdlOutputFormatter<T extends TsdlFormattable<?>> {
  String format(T obj);

  String[] args();
}
