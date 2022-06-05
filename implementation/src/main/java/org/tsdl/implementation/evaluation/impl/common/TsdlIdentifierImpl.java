package org.tsdl.implementation.evaluation.impl.common;

import org.tsdl.implementation.model.common.TsdlIdentifier;

/**
 * Default implementation of {@link TsdlIdentifier}.
 */
public record TsdlIdentifierImpl(String name) implements TsdlIdentifier {
}
