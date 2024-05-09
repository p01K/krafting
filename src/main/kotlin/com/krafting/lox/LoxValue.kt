package com.krafting.lox

sealed interface LoxValue {
    fun asNumber() = (this as Num).n

    data class Num(val n: Double): LoxValue

    data class Bool(val b: Boolean): LoxValue

    data class Literal(val s: String): LoxValue

    data class Function(val callable: LoxCallable): LoxValue

    data object Null: LoxValue
}