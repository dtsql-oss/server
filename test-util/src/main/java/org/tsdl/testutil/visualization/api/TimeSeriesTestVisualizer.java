package org.tsdl.testutil.visualization.api;

import org.tsdl.testutil.visualization.impl.JfreeChartTimeSeriesTestVisualizer;

/**
 * Provides functionality for visualizing time series data.
 */
public interface TimeSeriesTestVisualizer {
  static TimeSeriesTestVisualizer instance() {
    return new JfreeChartTimeSeriesTestVisualizer();
  }

  boolean visualizeBlocking(TsdlTestInfo testInformation, TsdlTestVisualization visualizationConfiguration);
}
