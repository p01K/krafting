package com.krafting.lox

class AstPrinter {
    fun asString(e: Expr): String {
        return when(e){
            is Expr.Literal -> if(e.o.equals(Null())) "nil"  else e.o.toString()
            is Expr.Unary -> parenthesize(e.token.lexeme, e.right)
            is Expr.Grouping -> parenthesize("group", e.expr)
            is Expr.Binary -> parenthesize(e.token.lexeme, e.left, e.right)
        }
    }

    fun parenthesize(name: String, vararg expr: Expr): String {
        val builder: StringBuilder = StringBuilder()
        builder.append("(").append(name)
        for(e in expr){
            builder.append(" ")
            builder.append(asString(e))
        }
        builder.append(")")
        return builder.toString()
    }
}