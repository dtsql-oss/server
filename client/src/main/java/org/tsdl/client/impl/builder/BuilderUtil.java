package org.tsdl.client.impl.builder;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import org.tsdl.client.util.TsdlQueryBuildException;

/**
 * Provides utility functions used throughout the builder implementation.
 */
final class BuilderUtil {
  private BuilderUtil() {
  }

  public static Instant requireInstant(String str) {
    try {
      return Instant.parse(str);
    } catch (DateTimeParseException e) {
      throw new TsdlQueryBuildException("Could not parse '%s' as Instant.".formatted(str), e);
    }
  }
}
