package org.tsdl.testutil.visualization.api;

import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.testutil.visualization.impl.TsdlTestInfoImpl;

import java.util.List;

public interface TsdlTestInfo {
    String shortName();

    String longName();

    List<List<DataPoint>> timeSeries();

    static TsdlTestInfo of(String shortName, String longName, List<List<DataPoint>> timeSeries) {
        return new TsdlTestInfoImpl(shortName, longName, timeSeries);
    }
}
