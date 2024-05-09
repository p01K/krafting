package com.krafting.lox

import kotlin.math.exp

/**
 * statement -> exprStmt
 * | forStmt
 * | ifStmt
 * | printStmt
 * | block
 * | whileStmt
 * | returnStmt
 *
 * returnStmt -> "return" expression? ";"
 * block -> "{" declaration* }
 * declaration -> functionDecl | variableDecl | statement
 * functionDecl -> fun function ;
 * function -> IDENTIFIER ( parameters? ) block
 * parameters -> IDENTIFIER ( , IDENTIFIER ) * ;
 * whileStmt -> while ( expression ) statement;
 */
const val MAX_PARAMS_SIZE = 255
class StmtParser(private val expressionParser: ExprParser) {
    fun parse(): List<Stmt> {
        val result = mutableListOf<Stmt>()
        while (!expressionParser.isAtEnd()) {
            result.add(declaration())
        }
        return result
    }

    private fun declaration(): Stmt {
        if(expressionParser.match(TokenType.FUN)){
            return function()
        }
        if (expressionParser.match(TokenType.VAR)) {
            return varDeclaration()
        }
        return statement()
    }

    fun block(): Stmt.Block {
        val statements = mutableListOf<Stmt>()
        while (!expressionParser.checkType(TokenType.RIGHT_BRACE) && !expressionParser.isAtEnd()) {
            statements.add(declaration())
        }
        expressionParser.consume(TokenType.RIGHT_BRACE, "Expected } after block")
        return Stmt.Block(statements)
    }

    private fun varDeclaration(): Stmt {
        val name = expressionParser.peek().lexeme
        expressionParser.consume(TokenType.IDENTIFIER, "Expected variable name")
        val initExpression: Expr = if (expressionParser.match(TokenType.EQUAL)) {
            expressionParser.assignment()
        } else {
            Expr.Nop
        }
        expressionParser.consume(TokenType.SEMICOLON, "; expected")
        return Stmt.Variable(name, initExpression)
    }

    private fun function(): Stmt {
        val name = expressionParser.peek().lexeme
        expressionParser.consume(TokenType.IDENTIFIER, "Expected variable name")
        expressionParser.consume(TokenType.LEFT_PAREN, "Expected ( after function name")
        val params = mutableListOf<String>()
        if(!expressionParser.checkType(TokenType.RIGHT_PAREN)){
            do {
                if(params.size >= MAX_PARAMS_SIZE){
                    error("Max number of params reached")
                }
                val newParam = expressionParser.peek().lexeme
                expressionParser.consume(TokenType.IDENTIFIER, "expected parameter name")
                params.add(newParam)
            } while (expressionParser.match(TokenType.COMMA))
        }
        expressionParser.consume(TokenType.RIGHT_PAREN, "Expected )")
        expressionParser.consume(TokenType.LEFT_BRACE, "Expected {")
        return Stmt.Function(name, params, block())
    }

    private fun statement(): Stmt {
        if (expressionParser.match(TokenType.FOR)) {
            return forStatement()
        } else if (expressionParser.match(TokenType.IF)) {
            return ifStatement()
        } else if (expressionParser.match(TokenType.PRINT)) {
            return printStatement()
        } else if (expressionParser.match(TokenType.RETURN)) {
            return returnStatement()
        } else if (expressionParser.match(TokenType.WHILE)) {
            return whileStatement()
        } else if (expressionParser.match(TokenType.LEFT_BRACE)) {
            return block()
        }
        return expressionStatement()
    }

    private fun ifStatement(): Stmt {
        expressionParser.consume(TokenType.LEFT_PAREN, "Expected ) after if")
        val condition: Expr = expressionParser.expression()
        expressionParser.consume(TokenType.RIGHT_PAREN, "Expected ) after if")
        val thenBranch: Stmt = statement()
        val elseBranch: Stmt? = if (expressionParser.match(TokenType.ELSE)) {
            statement()
        } else {
            null
        }
        return Stmt.If(condition, thenBranch, elseBranch)
    }

    private fun forStatement(): Stmt {
        expressionParser.consume(TokenType.LEFT_PAREN, "Expected ( after for")
        val initializer: Stmt? = when {
            expressionParser.match(TokenType.SEMICOLON) -> null
            expressionParser.match(TokenType.VAR) -> varDeclaration()
            else -> statement()
        }
        val condition: Expr? = when {
            !expressionParser.checkType(TokenType.SEMICOLON) -> expressionParser.expression()
            else -> null
        }
        expressionParser.consume(TokenType.SEMICOLON, "Expected ; after for condition")
        val increment: Expr? = when {
            !expressionParser.checkType(TokenType.RIGHT_PAREN) -> expressionParser.expression()
            else -> null
        }
        expressionParser.consume(TokenType.RIGHT_PAREN, "Expected ) after for")

        val body: Stmt = statement()

        return Stmt.For(initializer, condition, increment, body)
    }

    private fun whileStatement(): Stmt {
        expressionParser.consume(TokenType.LEFT_PAREN, "Expected ( after while")
        val condition: Expr = expressionParser.expression()
        expressionParser.consume(TokenType.RIGHT_PAREN, "Expected ) after while")
        val body: Stmt = statement()

        return Stmt.While(condition, body)
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

    fun returnStatement(): Stmt {
        val e = if(!expressionParser.checkType(TokenType.SEMICOLON)){
            expressionParser.expression()
        } else {
            null
        }
        expressionParser.consume(TokenType.SEMICOLON, "; expected")
        return Stmt.Return(e)
    }
}