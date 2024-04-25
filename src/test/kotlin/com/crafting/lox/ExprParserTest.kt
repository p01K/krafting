package com.crafting.lox

import com.krafting.lox.AstPrinter
import com.krafting.lox.ExprParser
import com.krafting.lox.TokenScanner
import kotlin.test.Test

class ExprParserTest {
    @Test
    fun parserTest(){
        val source = "1 + 2 / 3"
        val tokens = TokenScanner(source).parseTokens()
        val expression = ExprParser(tokens).parse()
        val printer: AstPrinter = AstPrinter()
        println(printer.asString(expression))
    }
}