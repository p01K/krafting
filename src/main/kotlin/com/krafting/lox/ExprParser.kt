package com.krafting.lox

/**
 * Grammar Definition
 * expression -> equality ;
 * equality -> comparison ( ("!=" | "==") comparison ) *
 * comparison -> term ( (">" | ">=" | "<" | "<=") term ) *
 * term -> factor ( ("-" | "+") factor ) *
 * factor -> unary ( ("*" | "/") unary ) *
 * unary -> ("!" | "-") unary | primary ;
 * primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")"
 */
class ExprParser(private val tokens: List<Token>) {
    private var current = 0

    private fun primary(): Expr {
        return when {
            match(TokenType.FALSE) -> Expr.Literal(false as Object)
            match(TokenType.TRUE) -> Expr.Literal(true as Object)
            match(TokenType.NIL) -> Expr.Literal(Null() as Object)
            match(TokenType.NUMBER, TokenType.STRING) -> Expr.Literal(previous().literal!!)
            match(TokenType.LEFT_PAREN) -> {
                val expr: Expr = expression()
                consume(TokenType.RIGHT_PAREN, "expected right paren")
                return Expr.Grouping(expr)
            }
            else -> throw IllegalStateException(".....")
        }
    }

    private fun term(): Expr {
        var expr: Expr = factor()
        while(match(TokenType.STAR, TokenType.SLASH)){
            val token: Token = previous()
            val right: Expr = factor()
            expr = Expr.Binary(expr, token, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr: Expr = unary()
        while(match(TokenType.STAR, TokenType.SLASH)){
            val token: Token = previous()
            val right: Expr = unary()
            expr = Expr.Binary(expr, token, right)
        }
        return expr
    }

    private fun unary(): Expr {
        if(match(TokenType.BANG, TokenType.MINUS)){
            val token: Token = previous()
            val right: Expr = unary()
            return Expr.Unary(token, right)
        }
        return primary()
    }

    private fun expression(): Expr = equality()

    private fun comparison(): Expr {
        var expr: Expr = term()
        while(match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)){
            val token: Token = previous()
            val right: Expr = term()
            expr = Expr.Binary(expr, token, right)
        }
        return expr
    }

    private fun equality(): Expr {
        var expr: Expr = comparison()
        while(match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)){
            val token: Token = previous()
            val right: Expr = comparison()
            expr = Expr.Binary(expr, token, right)
        }
        return expr
    }

    private fun consume(t: TokenType, msg: String){
        if(!checkType(t)){
            throw ParserError(msg)
        }
        advance()
    }

    private fun match(vararg types: TokenType): Boolean {
        for(t in types){
            if(checkType(t)){
                advance()
                return true
            }
        }
        return false
    }

    private fun advance(){
        if(!isAtEnd()) {
            current++
        }
    }

    private fun checkType(types: Array<TokenType>): Boolean {
        return types.any{ checkType(it) }
    }

    private fun checkType(type: TokenType): Boolean {
        return if(isAtEnd()) false else peek().tokenType == type
    }

    private fun isAtEnd(): Boolean = peek().tokenType == TokenType.EOF

    private fun peek(): Token = tokens[current-1]

    private fun previous(): Token = tokens[current]
}