package org.tsdl.implementation.evaluation.impl.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

/**
 * Default implementation of {@link TsdlIdentifier}.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public final class TsdlIdentifierImpl implements TsdlIdentifier {
  private final String name;

  public TsdlIdentifierImpl(String name) {
    Conditions.checkNotNull(Condition.ARGUMENT, name, "Name of identifier must not be null.");
    Conditions.checkIsFalse(Condition.ARGUMENT, name.trim().length() == 0, "Name of identifier must not be blank.");
    this.name = name;
  }
}
