package org.tsdl.testutil.visualization.api;

import org.tsdl.testutil.visualization.impl.JFreeChartTimeSeriesTestVisualizer;

public interface TimeSeriesTestVisualizer {
  static TimeSeriesTestVisualizer instance() {
    return new JFreeChartTimeSeriesTestVisualizer();
  }

  boolean visualizeBlocking(TsdlTestInfo testInformation, TsdlTestVisualization visualizationConfiguration);
}
