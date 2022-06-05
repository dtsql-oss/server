package org.tsdl.implementation.evaluation;

import java.util.List;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.parsing.TsdlQueryParser;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;

/**
 * Default implementation of {@link QueryService}.
 */
public class TsdlQueryService implements QueryService {
  private final TsdlQueryParser parser = ObjectFactory.INSTANCE.queryParser();

  @Override
  public QueryResult query(List<DataPoint> data, String query) {
    Conditions.checkNotNull(Condition.ARGUMENT, data, "Data must not be null.");
    Conditions.checkNotNull(Condition.ARGUMENT, query, "Query string must not be null.");

    var parsedQuery = parser.parseQuery(query);
    var resultingDataPoints = parsedQuery.filter().evaluateFilters(data);

    return QueryResult.of(resultingDataPoints);
  }
}
