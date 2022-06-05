package org.tsdl.implementation.parsing.enums;

import org.tsdl.implementation.model.common.Identifiable;

public enum ConnectiveIdentifier implements Identifiable {
  AND("AND"), OR("OR");

  private final String representation;

  ConnectiveIdentifier(String representation) {
    this.representation = representation;
  }

  public String representation() {
    return representation;
  }
}
