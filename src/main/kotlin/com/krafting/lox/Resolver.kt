package com.krafting.lox

import java.util.Stack

class Resolver(private val interpreter: Interpreter) {
    private val scopes: Stack<MutableMap<String, Boolean>> = Stack()

    private fun resolve(stmt: Stmt){

    }

    private fun resolve(expr: Expr){

    }

    fun visitBlock(block: Stmt.Block){
        beginScope()
        resolve(block.statements)
        endScope()
    }

    fun resolve(stmts: List<Stmt>){
        for(stmt in stmts){
            resolve(stmt)
        }
    }

    private fun visitVarStmt(stmt: Stmt.Variable){
        declare(stmt.name)
        stmt.init?.let { resolve(it) }
        define(stmt.name)
    }

    private fun visitFunctionStmt(stmt: Stmt.Function){
        declare(stmt.name)
        define(stmt.name)
        resolveFunction(stmt)
    }

    fun resolveFunction(function: Stmt.Function) {
        beginScope()
        for(param in function.params){
            declare(param)
            define(param)
        }
        resolve(function.body)
        endScope()
    }

    private fun visitExpressionStmt(stmt: Stmt.Expression){
        resolve(stmt.e)
    }

    private fun visitIfStmt(ifStmt: Stmt.If){
        resolve(ifStmt.condition)
        resolve(ifStmt.thenBranch)
        ifStmt.elseBranch?.let { resolve(it) }
    }

    private fun visitPrintStmt(printStmt: Stmt.Print){
        resolve(printStmt.e)
    }

    private fun visitReturnStmt(returnStmt: Stmt.Return){
        returnStmt.e?.let { resolve(it) }
    }

    private fun visitWhileStmt(whileStmt: Stmt.While){
        resolve(whileStmt.condition)
        resolve(whileStmt.body)
    }

    private fun visitBinaryExpr(expr: Expr.Binary){
        resolve(expr.left)
        resolve(expr.right)
    }

    private fun visitLogicalExpr(expr: Expr.Logical){
        resolve(expr.left)
        resolve(expr.right)
    }

    private fun visitLogicalExpr(expr: Expr.Unary){
        resolve(expr.right)
    }

    private fun visitBinaryExpr(expr: Expr.Grouping){
        resolve(expr.expr)
    }

    private fun visitCallExpr(expr: Expr.Call){
        resolve(expr.callee)
        expr.arguments.forEach{ resolve(it) }
    }

    private fun visitVariableExpr(e: Expr.Variable){
        require( scopes.isEmpty() || scopes.peek()[e.token.lexeme]!!)
        resolveLocal(e, e.token.lexeme)
    }

    private fun visitAssignmentExpr(e: Expr.Assignment){
        resolve(e.value)
        resolveLocal(e, e.token.lexeme)
    }

    fun resolveLocal(expr: Expr, name: String) {
        for(i in scopes.size-1 downTo 0){
            if(scopes[i].containsKey(name)){
                interpreter.resolve(expr, scopes.size-i-1)
                return
            }
        }
    }

    private fun declare(name: String){
        if(scopes.isNotEmpty()){
            scopes.peek()[name] = false
        }
    }

    private fun define(name: String){
        if(scopes.isNotEmpty()){
            scopes.peek()[name] = true
        }
    }

    private fun beginScope(){
        scopes.push(HashMap())
    }

    private fun endScope(){
        scopes.pop()
    }
}