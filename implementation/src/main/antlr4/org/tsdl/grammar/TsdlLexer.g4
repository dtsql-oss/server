lexer grammar TsdlLexer;

WHITESPACE
  :  ' '
  |  '\r'
  |  '\n'
  |  '\r\n'
  ;

FILTER_CLAUSE
  : 'FILTER'
  ;

FILTER_ALL
  : 'ALL'
  ;

FILTER_ANY
  : 'ANY'
  ;

YIELD
  :  'YIELD *'
  ;

CONNECTIVE_NOT
  :  'NOT'
  ;

CONNECTIVE_AND
  :  'AND'
  ;

CONNECTIVE_OR
  :  'OR'
  ;

OPERATOR_GT
  :  'gt'
  ;

OPERATOR_LT
  :  'lt'
  ;

COMMA
  :  ','
  ;

PARENTHESIS_OPEN
  :  '('
  ;

PARENTHESIS_CLOSE
  :  ')'
  ;

COLON
  : ':'
  ;

NUMBER : INT | FLOAT;

fragment DIGIT
  :  [0-9]
  ;

fragment SIGN
  :  '-'?
  ;

INT
  :  SIGN? DIGIT+
  ;

FLOAT
  :  SIGN? DIGIT+ '.' DIGIT+
  ;

TERMINATOR : [\r\n]+ -> channel(HIDDEN);