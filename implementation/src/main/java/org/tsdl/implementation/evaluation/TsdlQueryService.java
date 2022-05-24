package org.tsdl.implementation.evaluation;

import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.parsing.TsdlParser;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;

import java.util.List;

public class TsdlQueryService implements QueryService {
    private final TsdlParser parser = ObjectFactory.INSTANCE.getParser();

    @Override
    public QueryResult query(List<DataPoint> data, String query) {
        Conditions.checkNotNull(Condition.ARGUMENT, data, "Data must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, query, "Query string must not be null.");

        var parsedQuery = parser.parseQuery(query);
        var resultingDataPoints = parsedQuery.filter().evaluateFilters(data);

        return QueryResult.of(resultingDataPoints);
    }
}
