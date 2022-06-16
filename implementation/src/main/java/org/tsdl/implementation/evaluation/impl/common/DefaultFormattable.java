package org.tsdl.implementation.evaluation.impl.common;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import org.tsdl.implementation.model.common.TsdlFormattable;
import org.tsdl.implementation.model.common.TsdlOutputFormatter;
import org.tsdl.infrastructure.model.TsdlLogEvent;

/**
 * Mixin-like default implementation of {@link TsdlFormattable} that delegates actual formatting to the respective {@link TsdlOutputFormatter}.
 *
 * @param <T> type of component to be formatted
 */
public interface DefaultFormattable<T extends TsdlFormattable<T>> extends TsdlFormattable<T> {
  /**
   * Provides a default implementation of {@link TsdlFormattable#echo(OutputStream)} that only echos if the corresponding {@link java.util.Optional}
   * of {@link TsdlOutputFormatter} is not empty. It also writes an additional new line into the stream {@code out}.
   *
   * @param out the output stream to echo into
   * @throws IOException if an I/O error occurs
   */
  @SuppressWarnings("unchecked")
  default void echo(OutputStream out) throws IOException {
    if (formatter().isEmpty()) {
      return;
    }

    var representation = formatter().get().format((T) this);
    out.write(representation.getBytes(StandardCharsets.UTF_8));
    out.write("\n".getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Provides a default implementation of {@link TsdlFormattable#echo(Collection)} that only echos into the specified {@code target} collection if
   * the corresponding {@link java.util.Optional} of {@link TsdlOutputFormatter} is not empty.
   *
   * @param target the output stream to echo into
   */
  @SuppressWarnings("unchecked")
  @Override
  default void echo(Collection<TsdlLogEvent> target) {
    if (formatter().isEmpty()) {
      return;
    }

    var representation = formatter().get().format((T) this);
    var logEvent = TsdlLogEvent.of(Instant.now(), representation);
    target.add(logEvent);
  }
}
