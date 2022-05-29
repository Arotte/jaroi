# jaroi

Java implementation of the Aroi programming language.



## Grammar of Aroi

```
program        -> declaration* EOF ;

declaration    -> varDecl | statement ;
varDecl        -> "var" IDENTIFIER ( "=" expression )? ";" ;

statement      -> exprStmt
                | ifStmt
                | printStmt
                | whileStmt
                | forStmt
                | block ;
                
exprStmt       -> expression ";" ;
ifStmt         -> "if" "(" expression ")" statement
                  ( "else" statement )? ;
printStmt      -> "scream" expression ";" ;
whileStmt      -> "while" "(" expression ")" statement ;
forStmt        -> "for" "(" ( varDecl | exprStmt | ";" )
                  expression? ";"
                  expression? ")" statement; 
block          -> "{" declaration* "}" ; 
        
expression     -> assignment ;
assignment     -> IDENTIFIER "=" assignment
                | logic_or ;

logic_or       -> logic_and ( "or" logic_and )* ;
logic_and      -> equality ( "and" equality )* ;

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

