package org.tsdl.testutil.visualization.api;

import org.tsdl.testutil.visualization.impl.JFreeChartTimeSeriesTestVisualizer;

public interface TimeSeriesTestVisualizer {
    boolean visualizeBlocking(TsdlTestInfo testInformation, TsdlTestVisualization visualizationConfiguration);

    static TimeSeriesTestVisualizer instance() {
        return new JFreeChartTimeSeriesTestVisualizer();
    }
}
