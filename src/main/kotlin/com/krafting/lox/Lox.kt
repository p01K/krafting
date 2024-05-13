package com.krafting.lox

class Lox() {
    private val interpreter: Interpreter = Interpreter()
    private val resolver: Resolver = Resolver(interpreter)

    fun exec(source: String) {
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val statements = parser.parse()
        resolver.resolve(statements)
        return interpreter.interpret(statements)
    }
}