package com.krafting.lox

sealed interface LoxCallable {
    fun arity(): Int

    fun call(interpreter: Interpreter, arguments: List<LoxValue>): LoxValue
}

class LoxFunction(private val function: Stmt.Function, val closure: Environment): LoxCallable {
    override fun arity(): Int = function.params.size

    override fun call(
        interpreter: Interpreter,
        arguments: List<LoxValue>
    ): LoxValue {
        val env = Environment(closure)
        for((arg,value) in function.params.zip(arguments)){
            env.define(arg, value)
        }
        return try {
            interpreter.evaluateBlock(function.body, env)
        } catch (e: ReturnValue){
            e.value ?: LoxValue.Null
        }
    }
}
