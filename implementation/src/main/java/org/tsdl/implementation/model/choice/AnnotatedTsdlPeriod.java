package org.tsdl.implementation.model.choice;

import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.infrastructure.model.TsdlPeriod;

/**
 * A composite combining a an {@link TsdlPeriod} instance with the {@link TsdlIdentifier} of the event the period represents (is an example of).
 */
public interface AnnotatedTsdlPeriod {
  TsdlPeriod period();

  TsdlIdentifier event();
}
