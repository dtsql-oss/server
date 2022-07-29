package org.tsdl.implementation.model.filter.temporal;

import java.time.Instant;
import org.tsdl.implementation.model.filter.SinglePointFilter;

/**
 * A temporal single point filter, i.e., the eligibility of a data point for the result set depends on the time component.
 */
public interface TemporalFilter extends SinglePointFilter {
  Instant argument();
}
