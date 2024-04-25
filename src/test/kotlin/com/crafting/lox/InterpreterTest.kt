package com.crafting.lox

import com.krafting.lox.ExprParser
import com.krafting.lox.Interpreter
import com.krafting.lox.StmtParser
import com.krafting.lox.TokenScanner
import org.junit.jupiter.api.Test

class InterpreterTest {
    @Test
    fun `should execute print`(){
        val source = "print 1+2+3;"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        println(Interpreter().interpret(parser.parse()))
    }
}