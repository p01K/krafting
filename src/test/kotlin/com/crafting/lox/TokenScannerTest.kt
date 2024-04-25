package com.crafting.lox

import com.krafting.lox.Token
import com.krafting.lox.TokenScanner
import kotlin.test.Test
import kotlin.test.assertEquals

class TokenScannerTest {
    @Test
    fun testInit(){
        val source = " + - /"
        val scanner = TokenScanner(source)
        val tokens = scanner.parseTokens()
        assert(tokens.size == 4)
        assert(tokens[0].lexeme == "+")
        assert(tokens[1].lexeme == "-")
        assert(tokens[2].lexeme == "/")
    }

    @Test
    fun `should parse arithmetic expression`(){
        val source = "1 + 2 + 3"
        val scanner = TokenScanner(source)
        val tokens = scanner.parseTokens()
        assert(tokens.size == 6)
        assertEquals(tokens[0], Token.firstLineNumber(1))
        assertEquals(tokens[1], Token.firstLinePlusOperator())
        assertEquals(tokens[2], Token.firstLineNumber(2))
        assertEquals(tokens[3], Token.firstLinePlusOperator())
        assertEquals(tokens[4], Token.firstLineNumber(3))
    }

    @Test
    fun `should parse print expression`(){
        val source = "print 1 + 2 + 3"
        val scanner = TokenScanner(source)
        val tokens = scanner.parseTokens()
        println(tokens)
//        assert(tokens.size == 6)
//        assertEquals(tokens[0], Token.firstLineNumber(1))
//        assertEquals(tokens[1], Token.firstLinePlusOperator())
//        assertEquals(tokens[2], Token.firstLineNumber(2))
//        assertEquals(tokens[3], Token.firstLinePlusOperator())
//        assertEquals(tokens[4], Token.firstLineNumber(3))
    }
}