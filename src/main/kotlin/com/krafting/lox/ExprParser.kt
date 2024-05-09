package com.krafting.lox

import kotlin.collections.mutableListOf

/**
 * Grammar Definition
 * expression -> assignment;
 * assignment -> IDENTIFIER = assignment | logic_or
 * logic_or -> logic_and (or logic_and)*
 * logic_and -> equality ( and equality ) *
 * equality -> comparison ( ("!=" | "==") comparison ) *
 * comparison -> term ( (">" | ">=" | "<" | "<=") term ) *
 * term -> factor ( ("-" | "+") factor ) *
 * factor -> unary ( ("*" | "/") unary ) *
 * unary -> ("!" | "-") unary | call ;
 * call -> primary ( ( arguments? ) )*
 * primary -> NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")"
 * arguments -> expression ( , expression )*;
 */
class ExprParser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): Expr = expression()

    fun expression(): Expr = assignment()

    fun assignment(): Expr {
        val expr = or()
        if(match(TokenType.EQUAL)){
            val value: Expr = assignment()
            if (expr is Expr.Variable){
                return Expr.Assignment(expr.token, value)
            }
            throw IllegalStateException("...")
        }
        return expr
    }

    private fun primary(): Expr {
        return when {
            match(TokenType.FALSE) -> Expr.Literal(LoxValue.Bool(false))
            match(TokenType.TRUE) -> Expr.Literal(LoxValue.Bool(true))
            match(TokenType.NIL) -> Expr.Literal(LoxValue.Null)
            match(TokenType.NUMBER, TokenType.STRING) -> Expr.Literal(previous().literal)
            match(TokenType.LEFT_PAREN) -> {
                val expr: Expr = expression()
                consume(TokenType.RIGHT_PAREN, "expected right paren")
                return Expr.Grouping(expr)
            }
            match(TokenType.IDENTIFIER) -> {
                return Expr.Variable(previous())
            }
            else -> throw IllegalStateException("Illegal token "+peek())
        }
    }

    private fun term(): Expr {
        var expr: Expr = factor()
        while(match(TokenType.MINUS, TokenType.PLUS)){
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
        return call()
    }

    private fun call(): Expr {
        var expr = primary()

        while (match(TokenType.LEFT_PAREN)){
            expr = finishCall(expr)
        }

        return expr
    }

    private fun finishCall(callee: Expr): Expr {
        val arguments = mutableListOf<Expr>()
        if(!checkType(TokenType.RIGHT_PAREN)){
            do {
                if(arguments.size>=255){
                    throw IllegalArgumentException("Can't have more than 255 arguments")
                }
                arguments += expression()
            } while (match(TokenType.COMMA))
        }
        consume(TokenType.RIGHT_PAREN, "expected ) after arguments")
        return Expr.Call(callee, peek(), arguments)
    }

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

    private fun or(): Expr {
        var expr: Expr = and()
        while(match(TokenType.OR)){
            val right: Expr = and()
            expr = Expr.Logical(expr, TokenType.OR, right)
        }
        return expr
    }

    private fun and(): Expr {
        var expr: Expr = equality()
        while(match(TokenType.AND)){
            val right: Expr = equality()
            expr = Expr.Logical(expr, TokenType.AND, right)
        }
        return expr
    }

    fun consume(t: TokenType, msg: String){
        if(!checkType(t)){
            throw ParserError(msg)
        }
        advance()
    }

    fun match(vararg types: TokenType): Boolean {
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

    fun checkType(type: TokenType): Boolean {
        return if(isAtEnd()) false else peek().tokenType == type
    }

    fun isAtEnd(): Boolean = peek().tokenType == TokenType.EOF

    fun peek(): Token = tokens[current]

    private fun previous(): Token = tokens[current-1]
}