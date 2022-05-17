package org.tsdl.implementation.parsing.impl;


import org.tsdl.grammar.TsdlBaseListener;
import org.tsdl.grammar.TsdlParser;
import org.tsdl.implementation.factory.ObjectFactory;
import org.tsdl.implementation.factory.TsdlElementFactory;
import org.tsdl.implementation.model.TsdlQuery;
import org.tsdl.implementation.model.connective.SinglePointFilterConnective;
import org.tsdl.implementation.model.filter.SinglePointFilter;
import org.tsdl.implementation.parsing.TsdlElementParser;
import org.tsdl.implementation.parsing.enums.ConnectiveIdentifier;
import org.tsdl.infrastructure.common.Condition;
import org.tsdl.infrastructure.common.Conditions;

import java.util.ArrayList;
import java.util.List;

// TODO: use visitor instead of listener?
public class TsdlListenerImpl extends TsdlBaseListener {
    private final TsdlElementParser elementParser = ObjectFactory.INSTANCE.elementParser();
    private final TsdlElementFactory elementFactory = ObjectFactory.INSTANCE.elementFactory();

    private TsdlQuery query;
    private SinglePointFilterConnective connective;
    private ConnectiveIdentifier connectiveIdentifier;
    List<SinglePointFilter> filters;

    @Override
    public void exitFiltersDeclaration(TsdlParser.FiltersDeclarationContext ctx) {
        super.exitFiltersDeclaration(ctx);
    }

    @Override
    public void enterConnectiveIdentifier(TsdlParser.ConnectiveIdentifierContext ctx) {
        connectiveIdentifier = elementParser.parseConnectiveIdentifier(ctx.getText());
    }

    @Override
    public void exitFilterConnective(TsdlParser.FilterConnectiveContext ctx) {
        Conditions.checkNotNull(Condition.STATE, connectiveIdentifier, "Connective identifier must not be null.");
        Conditions.checkNotNull(Condition.STATE, connectiveIdentifier, "List of filters must not be null.");
        connective = elementFactory.getConnective(connectiveIdentifier, filters);
    }

    @Override
    public void enterSinglePointFilterDeclaration(TsdlParser.SinglePointFilterDeclarationContext ctx) {
        super.enterSinglePointFilterDeclaration(ctx);

        SinglePointFilter filter;
        if (ctx.negatedSinglePointFilter() == null) {
            filter = parseSinglePointFilter(ctx.singlePointFilter());
        } else {
            var innerFilter = parseSinglePointFilter((ctx.negatedSinglePointFilter().singlePointFilter()));
            filter = elementFactory.getNegatedFilter(innerFilter);
        }

        addFilter(filter);
    }

    @Override
    public void exitTsdl(TsdlParser.TsdlContext ctx) {
        Conditions.checkNotNull(Condition.STATE, connective, "Connective to make up query must not be null.");
        query = elementFactory.getQuery(connective);
    }

    private SinglePointFilter parseSinglePointFilter(TsdlParser.SinglePointFilterContext ctx) {
        var filterType = elementParser.parseFilterType(ctx.filterType().getText());
        var filterArgument = elementParser.parseNumber(ctx.NUMBER().getText());
        return elementFactory.getFilter(filterType, filterArgument);
    }

    private void addFilter(SinglePointFilter filter) {
        if (filters == null) {
            filters = new ArrayList<>();
        }
        filters.add(filter);
    }

    public TsdlQuery getQuery() {
        return query;
    }
}
