package org.tsdl.implementation.model.event.definition;

import java.util.List;
import org.tsdl.infrastructure.model.DataPoint;

public interface EventConnective {
  List<EventFunction> events();

  // TODO should be removed - solve differently for SinglePointEventStrategy!
  default boolean isSatisfied(DataPoint dp) {
    return false;
  }
}
