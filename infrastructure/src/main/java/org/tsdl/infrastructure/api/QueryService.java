package org.tsdl.infrastructure.api;

import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;

import java.util.List;

public interface QueryService {
    QueryResult query(List<DataPoint> data, String query);
}
