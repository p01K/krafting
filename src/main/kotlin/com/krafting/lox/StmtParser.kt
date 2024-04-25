package com.krafting.lox

class StmtParser(private val expressionParser: ExprParser) {
    fun parse(): List<Stmt> {
        val result = mutableListOf<Stmt>()
        while(!expressionParser.isAtEnd()){
            result.add(declaration())
        }
        return result
    }

    private fun declaration(): Stmt {
        if(expressionParser.match(TokenType.VAR)){
            return varDeclaration()
        }
        return statement()
    }

    private fun varDeclaration(): Stmt {
        val name = expressionParser.peek().lexeme
        expressionParser.consume(TokenType.IDENTIFIER, "Expected variable name")
        val initExpression: Expr = if(expressionParser.match(TokenType.EQUAL)) {
            expressionParser.expression()
        } else {
            Expr.Nop
        }

        expressionParser.consume(TokenType.SEMICOLON, "; expected")
        return Stmt.Variable(name, initExpression)
    }

    private fun statement(): Stmt {
        if(expressionParser.match(TokenType.PRINT)){
            return printStatement()
        }
        return expressionStatement()
    }

    private fun printStatement(): Stmt {
        val expr = expressionParser.expression()
        expressionParser.consume(TokenType.SEMICOLON, "; expected")
        return Stmt.Print(expr)
    }

    private fun expressionStatement(): Stmt {
        val expr = expressionParser.expression()
        expressionParser.consume(TokenType.SEMICOLON, "; expected")
        return Stmt.Expression(expr)
    }
}