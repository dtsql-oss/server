package org.tsdl.implementation.evaluation;

import org.tsdl.implementation.model.TsdlOperator;
import org.tsdl.implementation.parsing.TsdlParser;
import org.tsdl.infrastructure.api.QueryService;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;
import org.tsdl.infrastructure.model.DataPoint;
import org.tsdl.infrastructure.model.QueryResult;

import java.util.List;
import java.util.function.BiFunction;

public class TsdlQueryService implements QueryService {

    @Override
    public QueryResult query(List<DataPoint> data, String query) {
        Conditions.checkNotNull(Condition.ARGUMENT, data, "Data must not be null.");
        Conditions.checkNotNull(Condition.ARGUMENT, query, "Query must not be null.");

        var parsedQuery = TsdlParser.INSTANCE.parseQuery(query);
        var matchingDataPoints = data.stream()
          .filter(x -> evaluator(parsedQuery.operator()).apply(x.asDecimal(), parsedQuery.threshold()))
          .toList();

        return QueryResult.of(matchingDataPoints);
    }

    private BiFunction<Double, Double, Boolean> evaluator(TsdlOperator operator) {
        return switch (operator) {
            case GT -> (n1, n2) -> n1 > n2;
            case LT -> (n1, n2) -> n1 < n2;
        };
    }
}
