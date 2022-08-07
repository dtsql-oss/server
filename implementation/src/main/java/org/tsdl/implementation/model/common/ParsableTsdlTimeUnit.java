package org.tsdl.implementation.model.common;

/**
 * Identifies valid units of duration constraints for events.
 */
public enum ParsableTsdlTimeUnit implements Identifiable {
  WEEKS("weeks"), DAYS("days"), HOURS("hours"), MINUTES("minutes"), SECONDS("seconds"), MILLISECONDS("millis");

  private final String representation;

  ParsableTsdlTimeUnit(String representation) {
    this.representation = representation;
  }

  public String representation() {
    return representation;
  }
}
