package com.krafting.lox

import com.krafting.lox.LoxValue.*
import kotlin.reflect.KClass
import kotlin.reflect.cast


class ReturnValue(val value: LoxValue?): RuntimeException()

class Interpreter {
    private var environment: Environment = Environment()
    //Expression to scope depth where the variable was defined
    private val locals: MutableMap<Expr, Int> = mutableMapOf()
    private val globals: Environment = Environment()

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

    fun resolve(e: Expr, depth: Int){
        locals[e] = depth
    }

    fun evaluateExpression(e: Expr): LoxValue {
        return when(e){
            is Expr.Literal -> e.o
            is Expr.Binary -> evaluate(e)
            is Expr.Grouping -> evaluateExpression(e.expr)
            is Expr.Unary -> evaluate(e)
            is Expr.Variable -> evaluateVariableExpr(e)
            is Expr.Assignment -> evaluateAssignment(e)
            is Expr.Logical -> evaluate(e)
            is Expr.Call -> evaluateCall(e)
            is Expr.Nop -> LoxValue.Null
        }
    }

    fun evaluateVariableExpr(e: Expr.Variable): LoxValue {
        return locals[e]?.let { environment.getAtDepth(it, e.token.lexeme) } ?: globals.get(e.token.lexeme) ?: LoxValue.Null
    }

    fun evaluateCall(e: Expr.Call): LoxValue {
        val calleeEval = (evaluateExpression(e.callee) as LoxValue.Function).callable
        val arguments = mutableListOf<LoxValue>()
        for(arg in e.arguments){
            arguments += evaluateExpression(arg)
        }
        check(calleeEval.arity() == arguments.size)
        return calleeEval.call(this, arguments)
    }

    fun evaluateFunctionStmt(stmt: Stmt.Function): LoxValue {
        environment.define(stmt.name, Function(LoxFunction(stmt, environment)))
        return LoxValue.Null
    }

    fun evaluate(e: Stmt): LoxValue {
        return when(e){
            is Stmt.Print -> {
                println(evaluateExpression(e.e))
                LoxValue.Null
            }
            is Stmt.Expression -> {
                evaluateExpression(e.e)
            }
            is Stmt.Variable -> {
                val value = e.init?.let{ evaluateExpression(it) } ?: LoxValue.Null
                environment.define(e.name, value)
                LoxValue.Null
            }
            is Stmt.Block -> {
                evaluateBlock(e, Environment(environment))
            }
            is Stmt.If -> {
                if(isTruthful(evaluateExpression(e.condition))){
                    evaluate(e.thenBranch)
                } else {
                    e.elseBranch?.let { evaluate(it) } ?: LoxValue.Null
                }
            }
            is Stmt.While -> {
                while(isTruthful(evaluateExpression(e.condition))){
                    evaluate(e.body)
                }
                LoxValue.Null
            }
            is Stmt.Function -> {
                evaluateFunctionStmt(e)
            }
            is Stmt.Return -> {
                evaluateReturn(e)
            }
            else -> TODO()
        }
    }

    fun evaluateReturn(stmt: Stmt.Return): LoxValue {
        val returnVal = stmt.e?.let { evaluateExpression(it) }
        throw ReturnValue(returnVal)
    }

    fun evaluateBlock(block: Stmt.Block, env: Environment): LoxValue {
        val previousEnv = this.environment
        try {
            this.environment = env
            for(stmt in block.statements){
                evaluate(stmt)
            }
        } finally {
            this.environment = previousEnv
        }
        return LoxValue.Null
    }

    fun evaluate(e: Expr.Binary): LoxValue {
        val leftEval = evaluateExpression(e.left)
        val rightEval = evaluateExpression(e.right)

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
            TokenType.EQUAL_EQUAL -> Bool(leftEval==rightEval)
            TokenType.BANG_EQUAL -> Bool(leftEval!=rightEval)
            else -> throw IllegalStateException("What ${e.token.tokenType}")
        }
    }

    fun evaluate(e: Expr.Unary) : LoxValue {
        val rightEval = evaluateExpression(e.right).asNumber()
        return when(e.token.tokenType){
            TokenType.MINUS ->  Num(-rightEval)
            else -> throw IllegalStateException("What")
        }
    }

    private fun evaluateAssignment(expr: Expr.Assignment): LoxValue {
        val v: LoxValue = evaluateExpression(expr.value)
        if(locals[expr] !=null){
            environment.assignAtDepth(locals[expr]!!, expr.token.lexeme, v)
        } else {
            globals.assign(expr.token.lexeme, v)
        }
        return v
    }

    private fun evaluate(expr: Expr.Logical): Bool {
        return when(expr.op){
            TokenType.OR -> isTruthful(evaluateExpression(expr.left)) || isTruthful(evaluateExpression(expr.right))
            TokenType.AND -> isTruthful(evaluateExpression(expr.left)) && isTruthful(evaluateExpression(expr.right))
            else -> throw IllegalStateException("Op ${expr.op}")
        }.let { Bool(it) }
    }

    private fun isTruthful(value: LoxValue) = value is Bool && value.b==true

    private fun boolOperatorEval(type: TokenType, v1: LoxValue, v2: LoxValue): Bool {
        val leftNum = checkInstanceOf(v1, Num::class)
        val rightNum = checkInstanceOf(v2, Num::class)
        val applyFun = BOOL_OPERATOR_MAP[type]!!
        return Bool(applyFun(leftNum.n, rightNum.n))
    }

    private fun numOperatorEval(type: TokenType, v1: LoxValue, v2: LoxValue): Num {
        val leftNum = checkInstanceOf(v1, Num::class)
        val rightNum = checkInstanceOf(v2, Num::class)
        val applyFun = NUM_OPERATOR_MAP[type]!!
        return Num(applyFun(leftNum.n, rightNum.n))
    }

    private fun <T: LoxValue> checkInstanceOf(v: LoxValue, t: KClass<T>): T {
        require(t.isInstance(v))
        return t.cast(v)
    }

    internal fun isDefined(name: String): Boolean = environment.get(name) != null

    internal fun getValue(name: String): LoxValue? = environment.get(name)
}