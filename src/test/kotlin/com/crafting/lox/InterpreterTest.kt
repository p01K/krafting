package com.crafting.lox

import com.krafting.lox.ExprParser
import com.krafting.lox.Interpreter
import com.krafting.lox.LoxValue
import com.krafting.lox.StmtParser
import com.krafting.lox.TokenScanner
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InterpreterTest {
    @Test
    fun `should execute print`(){
        val source = "print 1+2+3;"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        Interpreter().interpret(parser.parse())
    }

    @Test
    fun `should declare variable`(){
        val source = "var a;"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
        assertTrue(interpreter.isDefined("a"))
        assertFalse(interpreter.isDefined("b"))
    }

    @Test
    fun `should declare variable with initialization`(){
        val source = "var a = 1;"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
        assertTrue(interpreter.isDefined("a"))
        assertEquals(interpreter.getValue("a"), LoxValue.Num(1.0))
    }

    @Test
    fun `should handle 2 variables`(){
        val source = "var b; var a = 1;"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
        assertTrue(interpreter.isDefined("a"))
        assertTrue(interpreter.isDefined("b"))
        assertEquals(interpreter.getValue("a"), LoxValue.Num(1.0))
        assertEquals(interpreter.getValue("b"), LoxValue.Null)
    }

    @Test
    fun `should obey precedence rules`(){
        val source = "var a = 1 + 2 + 3;"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
        assertTrue(interpreter.isDefined("a"))
        assertEquals(interpreter.getValue("a"), LoxValue.Num(6.0))
    }

    @Test
    fun `should print var in nested block`(){
        val source = "var a; { print a; }"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
//        assertTrue(interpreter.isDefined("a"))
//        assertEquals(interpreter.getValue("a"), LoxValue.Num(2.0))
    }

    @Test
    fun `should initialize var in nested block`(){
        val source = "var a; { a = 2; }"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
        assertTrue(interpreter.isDefined("a"))
        assertEquals(interpreter.getValue("a"), LoxValue.Num(2.0))
    }

    @Test
    fun `should handle nested var initialization`(){
        val source = "var a; var b = (a = 2);"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
        assertTrue(interpreter.isDefined("a"))
        assertEquals(interpreter.getValue("a"), LoxValue.Num(2.0))
    }

    @Test
    fun `if statement with print`(){
        val source = "if(2==3){ print 2; } else { print 3; }"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
//        assertTrue(interpreter.isDefined("a"))
//        assertEquals(interpreter.getValue("a"), LoxValue.Num(2.0))
    }

    @Test
    fun `assignment with logical expression`(){
        val source = "var a = (2==3 or 3==3);"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
        assertTrue(interpreter.isDefined("a"))
        assertEquals(interpreter.getValue("a"), LoxValue.Bool(true))
    }

    @Test
    fun `print with logical expression`(){
        val source = "print (2==3 or 3==3);"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
//        assertTrue(interpreter.isDefined("a"))
//        assertEquals(interpreter.getValue("a"), LoxValue.Bool(true))
    }

    @Test
    fun `reassign expression`(){
        val source = "var a = 1; a = a + 1; "
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
        assertTrue(interpreter.isDefined("a"))
        assertEquals(LoxValue.Num(2.0), interpreter.getValue("a"))
    }

    @Test
    fun `double reassign expression`(){
        val source = "var a = 1; var b; a = b = 2; "
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
        assertTrue(interpreter.isDefined("a"))
        assertEquals(LoxValue.Num(2.0), interpreter.getValue("a"))
    }

    @Test
    fun `while expression`(){
        val source = "var a = 1; while(a<5){ a = a + 1; }"
        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        interpreter.interpret(parser.parse())
        assertTrue(interpreter.isDefined("a"))
        assertEquals(LoxValue.Num(5.0), interpreter.getValue("a"))
    }

    @Test
    fun testFun(){
        val source = """
            | fun f(){
            |   return 1+2;
            |}
            |var b = f();
            |print b;
        """.trimMargin().trim()

        val tokenParser = TokenScanner(source)
        val parser = StmtParser(ExprParser(tokenParser.parseTokens()))
        val interpreter = Interpreter()
        println(interpreter.interpret(parser.parse()))
    }
}