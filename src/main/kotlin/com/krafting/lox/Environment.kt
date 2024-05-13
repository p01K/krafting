package com.krafting.lox

class Environment {
    private val parent: Environment?
    private val values: MutableMap<String, LoxValue> = mutableMapOf()

    constructor(){
        this.parent = null
    }
    constructor(e: Environment){
        this.parent = e
    }

    fun define(name: String, value: LoxValue){
        values[name] = value
    }

    fun assign(name: String, value: LoxValue){
        if(values.containsKey(name)) {
            values[name] = value
        } else {
            parent?.assign(name, value)
        }
    }

    fun get(name: String): LoxValue? {
        return values[name] ?: parent?.get(name)
    }

    fun assignAtDepth(depth: Int, name: String, value: LoxValue){
        ancestor(depth)!!.let { it.assign(name, value) }
    }

    fun getAtDepth(depth: Int, name: String): LoxValue? {
        return ancestor(depth)?.let { it.get(name) }
    }

    fun ancestor(depth: Int): Environment? {
        var env: Environment = this
        repeat(depth){
            if(env.parent==null){
                return null
            }
            env = env.parent
        }
        return env
    }
}