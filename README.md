# jaroi

Java implementation of the Aroi programming language.



## Grammar of Aroi

```
program        -> declaration* EOF ;

declaration    -> varDecl | statement ;
varDecl        -> "var" IDENTIFIER ( "=" expression )? ";" ;

statement      -> exprStmt | printStmt ;
exprStmt       -> expression ";" ;
printStmt      -> "scream" expression ";" ; 
        
expression     -> assignment ;
assignment     -> IDENTIFIER "=" assignment | equality ;
equality       -> comparison ( ( "!=" | "==" ) comparison )* ;
comparison     -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           -> factor ( ( "-" | "+" ) factor )* ;
factor         -> unary ( ( "/" | "*" ) unary )* ;
unary          -> ( "!" | "-" ) unary | primary ;

primary        -> "true" | "false" | "nil" 
                | NUMBER | STRING
                | "(" expression ")"
                | IDENTIFIER ;
```

Symbol Explanations
```
*    -> allow repetition zero or more times
+    -> allow repetition one or more times
?    -> can appear zero or one time
```

## Acknowledgements

