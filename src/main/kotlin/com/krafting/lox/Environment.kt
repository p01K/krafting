package com.krafting.lox

class Environment {
    constructor(){
        this.parent = null
    }
    constructor(e: Environment){
        this.parent = e
    }

    private val parent: Environment?
    private val values: MutableMap<String, LoxValue> = mutableMapOf()

    fun define(name: String, value: LoxValue){
        values[name] = value
    }

    fun assign(name: String, value: LoxValue){
        println("Assign $name $value")
        if(values.containsKey(name)) {
            values[name] = value
        } else {
            parent?.assign(name, value)
        }
    }

    fun get(name: String): LoxValue? {
        return values[name] ?: parent?.get(name)
    }
}