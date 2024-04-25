package com.krafting.lox

sealed interface LoxValue {
    fun asNumber() = (this as Num).n

    fun asString() = (this as Literal).s

    data class Num(val n: Double): LoxValue

    data class Bool(val b: Boolean): LoxValue

    data class Literal(val s: String): LoxValue

    data object Null: LoxValue
}