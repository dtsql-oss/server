package org.tsdl.implementation.model.common;

import org.tsdl.infrastructure.common.TsdlTimeUnit;

/**
 * Identifies valid units of duration constraints for events.
 */
public enum ParsableTsdlTimeUnit implements Identifiable {
  WEEKS("weeks", TsdlTimeUnit.WEEKS), DAYS("days", TsdlTimeUnit.DAYS), HOURS("hours", TsdlTimeUnit.HOURS), MINUTES("minutes", TsdlTimeUnit.MINUTES),
  SECONDS("seconds", TsdlTimeUnit.SECONDS), MILLISECONDS("millis", TsdlTimeUnit.MILLISECONDS);

  private final String representation;
  private final TsdlTimeUnit modelEquivalent;

  ParsableTsdlTimeUnit(String representation, TsdlTimeUnit modelEquivalent) {
    this.representation = representation;
    this.modelEquivalent = modelEquivalent;
  }

  public String representation() {
    return representation;
  }

  public TsdlTimeUnit modelEquivalent() {
    return modelEquivalent;
  }
}
