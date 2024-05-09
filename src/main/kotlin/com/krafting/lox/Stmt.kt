package com.krafting.lox

sealed interface Stmt {
    data class Print(val e: Expr): Stmt

    data class Expression(val e: Expr): Stmt

    data class Return(val e: Expr?): Stmt

    data class Variable(val name: String, val init: Expr) : Stmt

    data class Block(val statements: List<Stmt>) : Stmt

    data class If(val condition: Expr, val thenBranch: Stmt, val elseBranch: Stmt?): Stmt

    data class While(val condition: Expr, val body: Stmt): Stmt

    data class For(val init: Stmt?, val condition: Expr?, val increment: Expr?, val body: Stmt): Stmt

    data class Function(val name: String, val params: List<String>, val body: Block): Stmt
}