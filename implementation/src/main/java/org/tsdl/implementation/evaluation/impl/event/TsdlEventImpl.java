package org.tsdl.implementation.evaluation.impl.event;

import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.event.TsdlEvent;

public record TsdlEventImpl(SinglePointFilterConnective definition, TsdlIdentifier identifier) implements TsdlEvent {
}
