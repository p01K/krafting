package com.krafting.lox

import com.krafting.lox.LoxValue.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

class Interpreter {
    private val environment: Environment = Environment()

    companion object {
        val NUM_OPERATOR_MAP: Map<TokenType, (Double,Double) -> Double > = mapOf(
            TokenType.PLUS to { a,b -> a+b },
            TokenType.MINUS to { a,b -> a-b },
            TokenType.STAR to { a,b -> a*b },
            TokenType.SLASH to { a,b -> a/b }
        )
        val BOOL_OPERATOR_MAP: Map<TokenType, (Double,Double) -> Boolean > = mapOf(
            TokenType.GREATER to { a,b -> a > b },
            TokenType.GREATER_EQUAL to { a,b -> a >= b },
            TokenType.LESS to { a,b -> a < b },
            TokenType.LESS_EQUAL to { a,b -> a <= b }
        )
    }

    fun interpret(statements: List<Stmt>){
        for(stmt in statements){
            evaluate(stmt)
        }
    }

    fun evaluate(e: Expr): LoxValue {
        return when(e){
            is Expr.Literal -> e.o
            is Expr.Binary -> evaluate(e)
            is Expr.Grouping -> evaluate(e.expr)
            is Expr.Unary -> evaluate(e)
            is Expr.Variable -> environment.get(e.token.lexeme) ?: LoxValue.Null
            is Expr.Nop -> LoxValue.Null
        }
    }

    fun evaluate(e: Stmt): LoxValue {
        return when(e){
            is Stmt.Print -> {
                LoxValue.Null
            }
            is Stmt.Expression -> {
                evaluate(e)
                LoxValue.Null
            }
            is Stmt.Variable -> {
                val value = evaluate(e.init)
                environment.define(e.name, value)
                LoxValue.Null
            }
            else -> {
                TODO()
            }
        }
    }

    fun evaluate(e: Expr.Binary): LoxValue {
        val leftEval = evaluate(e.left)
        val rightEval = evaluate(e.right)

        return when(e.token.tokenType){
            TokenType.PLUS -> {
                return when(leftEval){
                    is Num -> {
                        val rightNum = checkInstanceOf(rightEval, Num::class)
                        Num(leftEval.n + rightNum.n)
                    }
                    is Literal -> {
                        val rightString = checkInstanceOf(rightEval, Literal::class)
                        Literal(leftEval.s+rightString)
                    }
                    else -> TODO()
                }
            }
            TokenType.MINUS -> numOperatorEval(TokenType.MINUS, leftEval, rightEval)
            TokenType.STAR -> numOperatorEval(TokenType.STAR, leftEval, rightEval)
            TokenType.SLASH -> numOperatorEval(TokenType.SLASH, leftEval, rightEval)
            TokenType.GREATER -> boolOperatorEval(TokenType.GREATER, leftEval, rightEval)
            TokenType.GREATER_EQUAL -> boolOperatorEval(TokenType.GREATER, leftEval, rightEval)
            TokenType.LESS -> boolOperatorEval(TokenType.LESS, leftEval, rightEval)
            TokenType.LESS_EQUAL -> boolOperatorEval(TokenType.LESS_EQUAL, leftEval, rightEval)
            TokenType.EQUAL -> Bool(leftEval==rightEval)
            TokenType.BANG_EQUAL -> Bool(leftEval!=rightEval)
            else -> throw IllegalStateException("What")
        }
    }

    fun evaluate(e: Expr.Unary) : LoxValue {
        val rightEval = evaluate(e.right).asNumber()
        return when(e.token.tokenType){
            TokenType.MINUS ->  LoxValue.Num(-rightEval)
            else -> throw IllegalStateException("What")
        }
    }

    private fun boolOperatorEval(type: TokenType, v1: LoxValue, v2: LoxValue): LoxValue.Bool {
        val leftNum = checkInstanceOf(v1, LoxValue.Num::class)
        val rightNum = checkInstanceOf(v2, LoxValue.Num::class)
        val applyFun = BOOL_OPERATOR_MAP[type]!!
        return LoxValue.Bool(applyFun(leftNum.n, rightNum.n))
    }

    private fun numOperatorEval(type: TokenType, v1: LoxValue, v2: LoxValue): LoxValue.Num {
        val leftNum = checkInstanceOf(v1, LoxValue.Num::class)
        val rightNum = checkInstanceOf(v2, LoxValue.Num::class)
        val applyFun = NUM_OPERATOR_MAP[type]!!
        return LoxValue.Num(applyFun(leftNum.n, rightNum.n))
    }

    private fun <T: LoxValue> checkInstanceOf(v: LoxValue, t: KClass<T>): T {
        require(t.isInstance(v))
        return t.cast(v)
    }
}