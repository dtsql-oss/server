package org.tsdl.implementation.model.event;

import org.tsdl.implementation.model.common.TsdlIdentifier;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;

public interface TsdlEvent {
    SinglePointFilterConnective definition();

    TsdlIdentifier identifier();
}
