// Defining grammar for comment titles
grammar CommentTitle;

rule1
  : V NPR PP? EOF
  ;

boolean_one_verb_rule
  : V NPR EOF
  ;

boolean_two_verb_rule
  : V NPR V NPR? EOF
  ;

NPR // Recursive Noun Phrase Variable
  : 'NN'
  | 'NNS'
  | 'NNP'
  | 'NNPS'
  | 'NN' ' ' NPR
  | 'NNS' ' ' NPR
  | 'NNP' ' ' NPR
  | 'NNPS' ' ' NPR
  ;

V // Non-Recursive Verb Variable
  : 'VB'
  | 'VBD'
  | 'VBG'
  | 'VBN'
  | 'VBP'
  | 'VBZ'
  ;

VR // Recursive Verb Variable
  : 'VB'
  | 'VBD'
  | 'VBG'
  | 'VBN'
  | 'VBP'
  | 'VBZ'
  | 'VB' ' ' VR
  | 'VBD' ' ' VR
  | 'VBG' ' ' VR
  | 'VBN' ' ' VR
  | 'VBP' ' ' VR
  | 'VBZ' ' ' VR
  ;

PP
  : 'IN' ' ' NPR
  ;

WS
  :
  [ \t\r\n]+ -> skip
  ; // skip spaces, tabs, newlines
