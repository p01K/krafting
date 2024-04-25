package com.krafting.lox

sealed interface Stmt {
    data class Print(val e: Expr): Stmt

    data class Expression(val e: Expr): Stmt

    data class Variable(val name: String, val init: Expr) : Stmt
}