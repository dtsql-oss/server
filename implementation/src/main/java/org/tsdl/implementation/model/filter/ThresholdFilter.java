package org.tsdl.implementation.model.filter;

import org.tsdl.implementation.model.filter.argument.TsdlFilterArgument;

public interface ThresholdFilter extends SinglePointFilter {
    TsdlFilterArgument threshold();
}
