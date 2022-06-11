package org.tsdl.implementation.evaluation.impl.result;

import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link YieldStatement}.
 */
public record YieldStatementImpl(YieldFormat format, TsdlIdentifier sample) implements YieldStatement {
  /**
   * Initializes this {@link YieldStatementImpl} instance.
   */
  public YieldStatementImpl {
    Conditions.checkNotNull(Condition.ARGUMENT, format, "Result format must not be null");
    if (format == YieldFormat.SAMPLE) {
      Conditions.checkNotNull(Condition.ARGUMENT, sample,
          "If result format is '%s', then the sample must not be null.", YieldFormat.SAMPLE.representation());
    } else {
      Conditions.checkIsTrue(Condition.ARGUMENT, sample == null,
          "If result format is not '%s', then the sample must be null.", YieldFormat.SAMPLE.representation());
    }
  }
}
