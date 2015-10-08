lexer grammar FWPolicyLexer;

@header {package org.oscm.app.iaas.fwpolicy; }

ARROW: '>' ;
COMMA: ',' ;
COLON: ':' ;
QUOTE: '"' ;
SEMICOLON: ';';
HASHMARK: '#';
MINUS: '-' -> channel(HIDDEN);
SLASH: '/' -> channel(HIDDEN);
LPAR: '(' -> channel(HIDDEN);
RPAR: ')' -> channel(HIDDEN);
        
ZONE:
      ALPHANUM+
  ;


PROTOCOL: HASHMARK ('tcp'|'udp'|'tcpudp'|'icmp') ;

PORT: 
        COLON NUMBER ((COMMA NUMBER)* | (MINUS NUMBER)?)
    ;

SERVICE:
           LPAR ALPHANUM+ RPAR
       ;

IP:
      OCTET '.' OCTET '.' OCTET '.' OCTET (SLASH OCTET)?
  |   QUOTE ~('"')* QUOTE
  ;


OCTET
  :  Digit Digit Digit
  |  Digit Digit
  |  Digit
  ;

NUMBER:
    Digit+
;

fragment ALPHANUM: [a-zA-Z0-9_-];

fragment Digit
  :  '0'..'9'
  ;

WS  : (' '|'\r'|'\n'|'\t')+ -> channel(HIDDEN) ;

