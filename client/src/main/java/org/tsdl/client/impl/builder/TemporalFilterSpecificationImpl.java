package org.tsdl.client.impl.builder;

import java.time.Instant;
import org.tsdl.client.api.builder.FilterSpecification;
import org.tsdl.client.api.builder.FilterSpecification.TemporalFilterSpecification;

/**
 * Default implementation of {@link TemporalFilterSpecification}.
 */
public final class TemporalFilterSpecificationImpl implements TemporalFilterSpecification {
  private final boolean negated;
  private final Instant argument;
  private final TemporalFilterType type;

  private TemporalFilterSpecificationImpl(boolean negated, Instant argument, TemporalFilterType type) {
    this.negated = negated;
    this.argument = argument;
    this.type = type;
  }

  @Override
  public boolean isNegated() {
    return negated;
  }

  @Override
  public Instant argument() {
    return argument;
  }

  @Override
  public TemporalFilterType type() {
    return type;
  }

  @Override
  public FilterSpecification negate() {
    return new TemporalFilterSpecificationImpl(!negated, argument, type);
  }

  public static TemporalFilterSpecification before(Instant argument) {
    return before(argument, false);
  }

  public static TemporalFilterSpecification before(String argument) {
    return before(BuilderUtil.requireInstant(argument), false);
  }

  public static TemporalFilterSpecification before(String argument, boolean negated) {
    return before(BuilderUtil.requireInstant(argument), negated);
  }

  public static TemporalFilterSpecification before(Instant argument, boolean negated) {
    return new TemporalFilterSpecificationImpl(negated, argument, TemporalFilterType.BEFORE);
  }

  public static TemporalFilterSpecification after(Instant argument) {
    return after(argument, false);
  }

  public static TemporalFilterSpecification after(String argument) {
    return after(BuilderUtil.requireInstant(argument), false);
  }

  public static TemporalFilterSpecification after(String argument, boolean negated) {
    return after(BuilderUtil.requireInstant(argument), negated);
  }

  public static TemporalFilterSpecification after(Instant argument, boolean negated) {
    return new TemporalFilterSpecificationImpl(negated, argument, TemporalFilterType.AFTER);
  }
}
