parser grammar TsdlParser;

options
{
  tokenVocab = TsdlLexer;
}

tsdlQuery
  :  whitespace
       (filtersDeclaration mandatoryWhitespace)?
       (samplesDeclaration mandatoryWhitespace)?
       (eventsDeclaration mandatoryWhitespace)?
       (chooseDeclaration mandatoryWhitespace)?
       yieldDeclaration
  ;

samplesDeclaration
  :  SAMPLES_CLAUSE COLON mandatoryWhitespace aggregatorsDeclarationStatement
  ;

eventsDeclaration
  :  EVENTS_CLAUSE COLON mandatoryWhitespace eventsDeclarationStatement
  ;

eventsDeclarationStatement
  :  eventList
  ;

eventList
  :  events eventSeparator eventDeclaration     // either two or more events
  |  eventDeclaration                           // or exactly one
  ;

events
  :  eventDeclaration (eventSeparator eventDeclaration)*   // one event plus [0..n] additional events
  ;

eventDeclaration
  :  filterConnective whitespace identifierDeclaration
  ;

eventSeparator
  :  COMMA whitespace
  ;

chooseDeclaration
  :  CHOOSE_CLAUSE COLON mandatoryWhitespace choiceStatement
  ;

choiceStatement
  :  identifier mandatoryWhitespace temporalRelation mandatoryWhitespace identifier
  ;

temporalRelation
  :  TEMPORAL_PRECEDES
  |  TEMPORAL_FOLLOWS
  ;

yieldDeclaration
  : YIELD COLON mandatoryWhitespace yieldType
  ;

yieldType
  :  YIELD_ALL_PERIODS
  |  YIELD_LONGEST_PERIOD
  |  YIELD_SHORTEST_PERIOD
  |  YIELD_DATA_POINTS
  ;

filtersDeclaration
  :  FILTER_CLAUSE COLON mandatoryWhitespace filterConnective
  ;

filterConnective
  :  connectiveIdentifier PARENTHESIS_OPEN whitespace singlePointFilterList whitespace PARENTHESIS_CLOSE
  ;

aggregatorsDeclarationStatement
  :  aggregatorList
  ;

aggregatorList
  :  aggregators aggregatorSeparator aggregatorDeclaration      // either two or more aggregators
  |  aggregatorDeclaration                                      // or exactly one
  ;

aggregators
  :  aggregatorDeclaration (aggregatorSeparator aggregatorDeclaration)*    // one aggregator plus [0..n] additional aggregators
  ;

aggregatorDeclaration
  :  aggregatorFunctionDeclaration whitespace identifierDeclaration
  ;

aggregatorFunctionDeclaration
  :  aggregatorFunction PARENTHESIS_OPEN whitespace aggregatorInput whitespace PARENTHESIS_CLOSE
  ;

aggregatorSeparator
  :  COMMA whitespace
  ;

aggregatorFunction
  :  AGGREGATOR_AVG
  |  AGGREGATOR_MAX
  |  AGGREGATOR_MIN
  |  AGGREGATOR_SUM
  ;

aggregatorInput
  :  INPUT_VARIABLE
  ;

identifierDeclaration
  : AS whitespace identifier
  ;

identifier
  : IDENTIFIER
  ;

connectiveIdentifier
  :  CONNECTIVE_AND
  |  CONNECTIVE_OR
  ;

singlePointFilterList
  :  singlePointFilters filterSeparator singlePointFilterDeclaration     // either two or more parameters
  |  singlePointFilterDeclaration                                        // or exactly one
  ;

singlePointFilters
  :  singlePointFilterDeclaration (filterSeparator singlePointFilterDeclaration)* // one parameter plus [0..n] additional parameters
  ;

singlePointFilterDeclaration
  :  singlePointFilter
  |  negatedSinglePointFilter
  ;

singlePointFilter
  :  filterType PARENTHESIS_OPEN whitespace singlePointFilterArgument whitespace PARENTHESIS_CLOSE
  ;

negatedSinglePointFilter
  :  CONNECTIVE_NOT PARENTHESIS_OPEN whitespace singlePointFilter whitespace PARENTHESIS_CLOSE
  |  singlePointFilter
  ;

singlePointFilterArgument
  :  NUMBER
  |  identifier
  ;

filterType
  :  OPERATOR_GT
  |  OPERATOR_LT
  ;

whitespace
  :  WHITESPACE*
  ;

mandatoryWhitespace
  :  WHITESPACE+
  ;

filterSeparator
  :  COMMA whitespace
  ;
