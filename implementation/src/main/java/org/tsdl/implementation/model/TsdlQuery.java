package org.tsdl.implementation.model;

import org.tsdl.implementation.model.connective.SinglePointFilterConnective;

public interface TsdlQuery {
    SinglePointFilterConnective filter();
}
