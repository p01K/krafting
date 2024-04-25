package com.krafting.lox

class Environment {
    private val values: MutableMap<String, LoxValue> = mutableMapOf()

    fun define(name: String, value: LoxValue){
        values[name] = value
    }

    fun get(name: String): LoxValue? = values[name]
}