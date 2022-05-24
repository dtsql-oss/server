package org.tsdl.infrastructure.extension;

public interface TimeSeriesTestVisualizer {
    boolean visualizeBlocking(TsdlTestInfo testInformation, TsdlTestVisualization visualizationConfiguration);

    static TimeSeriesTestVisualizer INSTANCE() {
        return new JFreeChartTimeSeriesTestVisualizer();
    }
}
