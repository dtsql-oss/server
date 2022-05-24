package org.tsdl.implementation.model.filter;

/**
 * A special {@link SinglePointFilter} that returns the inverted result of a given {@link SinglePointFilter#evaluate()} implementation.
 */
public interface NegatedSinglePointFilter extends SinglePointFilter {
    SinglePointFilter filter();
}
