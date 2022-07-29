package org.tsdl.implementation.model.choice;

import java.util.Optional;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * A composite combining a {@link TsdlPeriod} instance with the {@link TsdlIdentifier} of the event the period represents (is an example of).
 */
public interface AnnotatedTsdlPeriod {
  TsdlPeriod period();

  TsdlIdentifier event();

  Optional<DataPoint> priorDataPoint();

  Optional<DataPoint> subsequentDataPoint();
}
