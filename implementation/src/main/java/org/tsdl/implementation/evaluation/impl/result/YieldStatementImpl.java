package org.tsdl.implementation.evaluation.impl.result;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.result.YieldFormat;
import org.tsdl.implementation.model.result.YieldStatement;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link YieldStatement}.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class YieldStatementImpl implements YieldStatement {
  private final YieldFormat format;
  private final List<TsdlIdentifier> samples;

  /**
   * Initializes this {@link YieldStatementImpl} instance.
   */
  public YieldStatementImpl(YieldFormat format, List<TsdlIdentifier> samples) {
    Conditions.checkNotNull(Condition.ARGUMENT, format, "Result format must not be null");
    if (format == YieldFormat.SAMPLE) {
      Conditions.checkNotNull(Condition.ARGUMENT, samples,
          "If result format is '%s', then the sample must not be null.", YieldFormat.SAMPLE.representation());
      Conditions.checkSizeExactly(Condition.ARGUMENT, samples, 1,
          "If result format is '%s', then there must be exactly one sample", YieldFormat.SAMPLE.representation());
    } else if (format == YieldFormat.SAMPLE_SET) {
      Conditions.checkNotNull(Condition.ARGUMENT, samples,
          "If result format is '%s', then the sample must not be null.", YieldFormat.SAMPLE_SET.representation());
      Conditions.checkIsGreaterThan(Condition.ARGUMENT, samples.size(), 0,
          "If result format is '%s', then there must be at least one sample", YieldFormat.SAMPLE_SET.representation());
    } else {
      Conditions.checkIsTrue(Condition.ARGUMENT, samples == null,
          "If result format is neither '%s' nor '%s', then the sample must be null.",
          YieldFormat.SAMPLE.representation(), YieldFormat.SAMPLE_SET.representation());
    }

    this.format = format;
    this.samples = samples;
  }
}
