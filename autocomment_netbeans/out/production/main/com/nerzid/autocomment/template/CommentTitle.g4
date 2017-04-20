// Defining grammar for comment titles
grammar CommentTitle;

rule1
  : V NP PP? EOF
  ;
NP
  : 'NN' ' ' NP
  | 'NNS' ' ' NP
  | 'NNP' ' ' NP
  | 'NNPS' ' ' NP
  | 'NN'
  | 'NNS'
  | 'NNP'
  | 'NNPS'
  ;
V
  : 'VB'
  | 'VBD'
  | 'VBG'
  | 'VBN'
  | 'VBP'
  | 'VBZ'
  ;
PP
  : 'IN' ' ' NP
  ;
WS
  :
  [ \t\r\n]+ -> skip
  ; // skip spaces, tabs, newlines
