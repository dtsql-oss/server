grammar Tsdl;

tsdl : line+;

line : key_value_pair;
key_value_pair : operator | threshold;
operator : OPERATOR_LABEL SEPARATOR OPERATOR_ID;
threshold : THRESHOLD_LABEL SEPARATOR threshold_value;
threshold_value: NUMBER;

OPERATOR_LABEL : 'operator';
THRESHOLD_LABEL : 'threshold';
OPERATOR_ID : 'gt' | 'lt';
SEPARATOR : '=';
NUMBER : INT | FLOAT;

// numbers
fragment DIGIT : [0-9];
fragment SIGN : '-'?;

INT : SIGN? DIGIT+;
FLOAT : SIGN? DIGIT+ '.' DIGIT+;

TERMINATOR : [\r\n]+ -> channel(HIDDEN);