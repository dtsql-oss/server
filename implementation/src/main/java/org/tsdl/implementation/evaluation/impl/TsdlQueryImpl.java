package org.tsdl.implementation.evaluation.impl;

import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;

public record TsdlQueryImpl(SinglePointFilterConnective filter) implements TsdlQuery {
}
