package org.tsdl.client.api.builder;

import java.util.Optional;

public interface ChoiceOperand {
  Optional<Range> tolerance();

  interface EventChoiceOperand extends ChoiceOperand {
    String eventIdentifier();
  }
}
