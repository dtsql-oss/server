lexer grammar TsdlLexer;

WHITESPACE  :  WHITESPACE_CHARACTER+  ;
fragment WHITESPACE_CHARACTER  :  ' '  |  '\r'  |  '\n'  |  '\r\n'  |  '\t'  ;

SAMPLES_CLAUSE  :  'WITH SAMPLES'  ;
EVENTS_CLAUSE  :  'USING EVENTS'  ;
FILTER_CLAUSE  :  'APPLY FILTER'  ;
CHOOSE_CLAUSE  :  'CHOOSE'  ;

YIELD  :  'YIELD'  ;
YIELD_ALL_PERIODS  :  'all periods'  ;
YIELD_LONGEST_PERIOD  :  'longest period'  ;
YIELD_SHORTEST_PERIOD  :  'shortest period'  ;
YIELD_DATA_POINTS  :  'data points'  ;
YIELD_SAMPLE  :  'sample'  ;
YIELD_SAMPLE_SET :  'samples'  ;

CONNECTIVE_NOT  :  'NOT'  ;
CONNECTIVE_IDENTIFIER
  :  'AND'
  |  'OR'
  ;

THRESHOLD_FILTER_TYPE
  :  'gt'
  |  'lt'
  ;

TEMPORAL_FILTER_TYPE
  :  'before'
  |  'after'
  ;

DEVIATION_FILTER_TYPE
  :  'around'
  ;

AROUND_FILTER_TYPE
  :  'rel'
  |  'abs'
  ;

PARENTHESIS_OPEN  :  '('  ;
PARENTHESIS_CLOSE  :  ')'  ;
COLON  :  ':'  ;
fragment COMMA  :  ','  ;
LIST_SEPARATOR
  :  WHITESPACE? COMMA WHITESPACE?
  ;

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

TEMPORAL_RELATION
  :  'precedes'
  |  'follows'
  ;

AGGREGATOR_FUNCTION
  :  'avg'
  |  'max'
  |  'min'
  |  'sum'
  |  'count'
  |  'integral'
  |  'stddev'
  ;

// INT >= 0 (app validation)
TIME_TOLERANCE  :  TIME_TOLERANCE_WITHIN WHITESPACE DURATION_RANGE_OPEN WHITESPACE? INT? LIST_SEPARATOR INT? DURATION_RANGE_CLOSE  ;
fragment TIME_TOLERANCE_WITHIN  :  'WITHIN'  ;
fragment TIME_TOLERANCE_OPEN  :  PARENTHESIS_OPEN  |  '['  ;
fragment TIME_TOLERANCE_CLOSE  :  PARENTHESIS_CLOSE  | ']'  ;

// INT >= 0 (app validation)
EVENT_DURATION  :  DURATION_FOR WHITESPACE DURATION_RANGE_OPEN WHITESPACE? INT? LIST_SEPARATOR INT? DURATION_RANGE_CLOSE  ;
fragment DURATION_FOR  :  'FOR'  ;
fragment DURATION_RANGE_OPEN  :  PARENTHESIS_OPEN  |  '['  ;
fragment DURATION_RANGE_CLOSE  :  PARENTHESIS_CLOSE  | ']'  ;

TIME_UNIT
  :  'weeks'
  |  'days'
  |  'hours'
  |  'minutes'
  |  'seconds'
  |  'millis'
  ;

STRING_LITERAL  :  '"' STRING_CHARACTERS? '"'  ;
fragment STRING_CHARACTERS  :  STRING_CHARACTER+  ;
fragment STRING_CHARACTER  :  ~["\\\r\n]  ;

IDENTIFIER
  :  IDENTIFIER_FIRST_CHARACTER IDENTIFIER_CHARACTER*
  ;

ECHO_ARGUMENT
  :  NUMBER
  |  STRING_LITERAL
  ;

fragment IDENTIFIER_FIRST_CHARACTER  :  LETTER_CHARACTER  ;
fragment IDENTIFIER_CHARACTER  :  LETTER_CHARACTER  |  DIGIT_CHARACTER  ;

fragment DIGIT_CHARACTER  :  DIGIT  ;
fragment LETTER_CHARACTER  :  [A-Za-z]  ;
