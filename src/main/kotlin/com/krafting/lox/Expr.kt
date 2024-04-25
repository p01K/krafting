package com.krafting.lox

data class ParserError(val msg: String): IllegalArgumentException(msg)

class Null

sealed interface Expr {
    data class Binary(val left: Expr, val token: Token, val right: Expr) : Expr

    data class Grouping(val expr: Expr) : Expr

    data class Literal(val o: LoxValue) : Expr

    data class Variable(val token: Token): Expr

    data class Unary(val token: Token, val right: Expr) : Expr

    data object Nop: Expr
}