package org.tsdl.implementation.evaluation.impl.filter.argument;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.tsdl.implementation.model.filter.argument.TsdlLiteralFilterArgument;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlLiteralFilterArgument}.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class TsdlLiteralFilterArgumentImpl implements TsdlLiteralFilterArgument {
  private final Double value;

  public TsdlLiteralFilterArgumentImpl(Double value) {
    Conditions.checkNotNull(Condition.ARGUMENT, value, "Value of literal filter argument must not be null.");
    this.value = value;
  }
}
