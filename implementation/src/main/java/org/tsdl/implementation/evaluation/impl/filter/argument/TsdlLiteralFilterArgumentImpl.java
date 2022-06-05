package org.tsdl.implementation.evaluation.impl.filter.argument;

import org.tsdl.implementation.model.filter.argument.TsdlLiteralFilterArgument;

/**
 * Default implementation of {@link TsdlLiteralFilterArgument}.
 */
public record TsdlLiteralFilterArgumentImpl(Double value) implements TsdlLiteralFilterArgument {
}
