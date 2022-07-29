package org.tsdl.implementation.model.event;

import org.tsdl.implementation.model.common.Identifiable;

/**
 * Identifies valid units of duration constraints for events.
 */
public enum EventDurationUnit implements Identifiable {
  WEEKS("weeks"), DAYS("days"), HOURS("hours"), MINUTES("minutes"), SECONDS("seconds"), MILLISECONDS("millis");

  private final String representation;

  EventDurationUnit(String representation) {
    this.representation = representation;
  }

  public String representation() {
    return representation;
  }
}
