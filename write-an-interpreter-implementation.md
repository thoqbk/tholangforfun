# Writing an Interpreter: Implementation

## Lexer

The Lexer serves as the most basic element. Its primary function involves iterating through the characters present in the source code. It may combine certain characters to create a single token, and subsequently generate a token object with its associated type. This object is then added to the resulting list.

More in-depth information regarding the implementation can be found [here](https://github.com/thoqbk/tholangforfun/blob/master/src/main/java/io/thoqbk/tholangforfun/Lexer.java)

## Parser

The parser is the most complex component in an interpreter. Before we delve into it, let's understand the difference between an expression and a statement:
- Expression: An expression is a combination of values, variables, operators, and function calls that come together to give a single value. It's like a calculation that produces a result.
- Statement: A statement is a complete section of code that carries out an action or a series of actions. It represents an instruction or a command that the program follows. Expressions can be part of a statement.

When we parse an expression, we generate an Abstract Syntax Tree (AST). This is the trickiest part because we need to handle operator precedence (which operations happen first) and the types of operators (like unary or ternary). It becomes even more challenging when expressions involve variables and function calls with their own arguments, which can also be expressions.

Some examples:

```
1 + 2;
1 + (2 - 3) * 3;
1 + (2 - a) * 3 + sum(2, 3 + 2 * b);
```

### Pratt parser

The Pratt parser is a flexible and efficient method for parsing expressions, especially in languages with complex rules for operator precedence and associativity. It relies on Pratt parsing functions, which are specialized parsing functions associated with operators. Each operator in the language has its own parsing function that determines how expressions involving that operator should be parsed.

Here are some advantages of the Pratt parser:
- Operator Precedence: Each operator is assigned a precedence level, ensuring that expressions are evaluated correctly according to the language's precedence rules.
- Left-Associativity and Right-Associativity: The parser can handle both types of associativity. For example, in the expression 3 ^ 2 ^ 3, it correctly interprets it as 3 ^ (2 ^ 3) instead of (3 ^ 2) ^ 3.
Extensibility: It is easy to add new operators by specifying their precedence and implementing their respective parsing function.

Additional resources for more information:
- [Top Down Operator Precedence](https://tdop.github.io/)
- [Pratt Parsers: Expression Parsing Made Easy](https://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/)

### Pratt parser: Implementation

Pratt parser sounds complex but its implementation is quite simple. The full implementation can be found [here](https://github.com/thoqbk/tholangforfun/blob/master/src/main/java/io/thoqbk/tholangforfun/Parser.java). To summarize:
- First we need to configure parser functions for operators: prefixParsers and infixParsers
- Configure precedences for operators: `precedences`
- Using the configurations above to implement `parseExpression`

The Pratt parser may sound complicated, but its implementation is actually quite straightforward. You can find the complete implementation [here](https://github.com/thoqbk/tholangforfun/blob/master/src/main/java/io/thoqbk/tholangforfun/Parser.java). Here's a summary of how it works:
- First, we need to set up parser functions for operators: prefixParsers for infix operators, and infixParsers for prefix ones. It's doable but the implementation doesn't support suffix for now
- We configure the precedence levels for operators using the precedences setup.
- With the above configurations in place, we can now implement the parseExpression function, which parses expressions based on the defined operator rules.

That's the basic idea behind the Pratt parser. You can check out the provided link for a more detailed implementation.


### Parse if-else statement

Now that we have implemented `parseExpression`, parsing functions for statements are relatively straightforward, as long as they have clear syntax. Let's take a look at how to parse an `if-else` statement. Assuming we have the following syntax in Backus-Naur Form (BNF):

```
<if-statement> ::= if ( <expression> ) { <statement> }
                | if ( <expression> ) { <statement> } else { <statement> }
```

The parse function for the if statement would look like this:
```
private Statement parseIfStatement() {
    var retVal = new If(lexer.currentToken());
    assertPeekTokenThenNext(TokenType.LPAREN);
    lexer.nextToken();
    retVal.setCondition(parseExpression());
    assertPeekTokenThenNext(TokenType.RPAREN);
    assertPeekTokenThenNext(TokenType.LBRACE);
    retVal.setIfBody(parseBlockStatement());
    if (peekTokenIs(TokenType.ELSE)) {
        lexer.nextToken();
        lexer.nextToken();
        retVal.setElseBody(parseBlockStatement());
    }
    return retVal;
}
```

The implementation closely reflects the defined syntax in BNF, making it easy to understand and follow.

## Evaluator

Details can be found [here](https://github.com/thoqbk/tholangforfun/blob/master/src/main/java/io/thoqbk/tholangforfun/Evaluator.java)

## References:
- [Top Down Operator Precedence](https://tdop.github.io/)
- [Pratt Parsers: Expression Parsing Made Easy](https://journal.stuffwithstuff.com/2011/03/19/pratt-parsers-expression-parsing-made-easy/)
- Book [Write a intepreter in Go](https://interpreterbook.com/)
