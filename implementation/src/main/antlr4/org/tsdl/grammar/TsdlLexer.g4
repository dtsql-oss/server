lexer grammar TsdlLexer;

WHITESPACE
  :  ' '
  |  '\r'
  |  '\n'
  |  '\r\n'
  ;

SAMPLES_CLAUSE  :  'WITH SAMPLES'  ;
EVENTS_CLAUSE  :  'USING EVENTS'  ;
FILTER_CLAUSE  :  'FILTER'  ;
CHOOSE_CLAUSE  :  'CHOOSE'  ;

YIELD  :  'YIELD'  ;
YIELD_ALL_PERIODS  :  'all periods'  ;
YIELD_LONGEST_PERIOD  :  'longest period'  ;
YIELD_SHORTEST_PERIOD  :  'shortest period'  ;
YIELD_DATA_POINTS  :  'data points'  ;
YIELD_SAMPLE  :  'sample'  ;
YIELD_SAMPLE_SET :  'samples'  ;

CONNECTIVE_NOT  :  'NOT'  ;
CONNECTIVE_AND  :  'AND'  ;
CONNECTIVE_OR  :  'OR'  ;

OPERATOR_GT  :  'gt'  ;
OPERATOR_LT  :  'lt'  ;

COMMA  :  ','  ;
PARENTHESIS_OPEN  :  '('  ;
PARENTHESIS_CLOSE  :  ')'  ;

COLON  :  ':'  ;

NUMBER
  :  INT
  |  FLOAT
  ;

fragment DIGIT  :  [0-9]  ;
fragment SIGN  :  '-'?  ;

INT
  :  SIGN? DIGIT+
  ;

FLOAT
  :  SIGN? DIGIT+ '.' DIGIT+
  ;

AS  :  'AS'  ;

ECHO_ARROW  :  '->'  ;
ECHO_LABEL  :  'echo'  ;

TEMPORAL_PRECEDES  :  'precedes'  ;
TEMPORAL_FOLLOWS  :  'follows'  ;

AGGREGATOR_AVG  :  'avg'  ;
AGGREGATOR_MAX  :  'max'  ;
AGGREGATOR_MIN  :  'min'  ;
AGGREGATOR_SUM  :  'sum'  ;
AGGREGATOR_COUNT  :  'count'  ;
INPUT_VARIABLE  :  '_input'  ;

IDENTIFIER
  :  IDENTIFIER_FIRST_CHARACTER IDENTIFIER_PART_CHARACTER*
  ;

ECHO_ARGUMENT
  :  (IDENTIFIER_PART_CHARACTER)+
  ;

fragment IDENTIFIER_FIRST_CHARACTER  :  LETTER_CHARACTER  ;

fragment IDENTIFIER_PART_CHARACTER
  :  LETTER_CHARACTER
  |  DIGIT_CHARACTER
  ;

fragment DIGIT_CHARACTER  : DIGIT  ;
fragment LETTER_CHARACTER  : [A-Za-z]  ;

//TERMINATOR : [\r\n]+ -> channel(HIDDEN);