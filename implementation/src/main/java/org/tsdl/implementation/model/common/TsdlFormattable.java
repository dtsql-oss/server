package org.tsdl.implementation.model.common;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Optional;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Interface denoting components of a TSDL query which support having a presentation of themselves printed.
 *
 * @param <T> type of the component to format
 */
public interface TsdlFormattable<T extends TsdlFormattable<T>> {
  Optional<TsdlOutputFormatter<T>> formatter();

  default void echo() throws IOException {
    echo(System.out);
  }

  void echo(OutputStream out) throws IOException;

  void echo(Collection<TsdlLogEvent> target);
}
