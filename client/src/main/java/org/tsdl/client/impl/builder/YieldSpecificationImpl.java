package org.tsdl.client.impl.builder;

import java.util.Arrays;
import java.util.List;
import org.tsdl.client.api.builder.YieldSpecification;
import org.tsdl.client.util.TsdlQueryBuildException;

/**
 * Default implementation of {@link YieldSpecification}.
 */
public final class YieldSpecificationImpl implements YieldSpecification {
  private static final YieldSpecification DATA_POINTS = new YieldSpecificationImpl(null, null, YieldType.DATA_POINTS);
  private static final YieldSpecification ALL_PERIODS = new YieldSpecificationImpl(null, null, YieldType.ALL_PERIODS);
  private static final YieldSpecification LONGEST_PERIOD = new YieldSpecificationImpl(null, null, YieldType.LONGEST_PERIOD);
  private static final YieldSpecification SHORTEST_PERIOD = new YieldSpecificationImpl(null, null, YieldType.SHORTEST_PERIOD);

  private final String sample;
  private final List<String> samples;
  private final YieldType type;

  private YieldSpecificationImpl(String sample, List<String> samples, YieldType type) {
    this.sample = sample;
    this.samples = samples;
    this.type = type;
  }

  public String sample() {
    return sample;
  }

  public List<String> samples() {
    return samples;
  }

  public YieldType type() {
    return type;
  }

  public static YieldSpecification dataPoints() {
    return DATA_POINTS;
  }

  public static YieldSpecification allPeriods() {
    return ALL_PERIODS;
  }

  public static YieldSpecification longestPeriod() {
    return LONGEST_PERIOD;
  }

  public static YieldSpecification shortestPeriod() {
    return SHORTEST_PERIOD;
  }

  // CHECKSTYLE.OFF: OverloadMethodsDeclarationOrder - violation occurs due to clash with (non-relevant) non-static getters 'sample' and 'samples'

  public static YieldSpecification sample(String sampleIdentifier) {
    if (sampleIdentifier == null) {
      throw new TsdlQueryBuildException("Sample identifier must not be null.");
    }
    return new YieldSpecificationImpl(sampleIdentifier, null, YieldType.SAMPLE);
  }

  public static YieldSpecification samples(List<String> sampleIdentifiers) {
    if (sampleIdentifiers == null || sampleIdentifiers.isEmpty()) {
      throw new TsdlQueryBuildException("Sample identifiers must neither be null nor empty.");
    }
    return new YieldSpecificationImpl(null, sampleIdentifiers, YieldType.SAMPLES);
  }

  public static YieldSpecification samples(String... sampleIdentifiers) {
    return samples(Arrays.stream(sampleIdentifiers).toList());
  }

  // CHECKSTYLE.ON: OverloadMethodsDeclarationOrder
}
