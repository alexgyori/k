module META-K 
  syntax Set ::= #variables(K)        [function, hook(#META-K:#variables)]
  syntax K ::= #rename(K, Set)    [function, hook(#META-K:#rename)]
endmodule
