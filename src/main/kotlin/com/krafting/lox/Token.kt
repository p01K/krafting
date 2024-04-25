package com.krafting.lox

data class Token(val tokenType: TokenType, val lexeme: String, val literal: LoxValue, val line: Int){
    companion object {
        fun eof(line: Int) = Token(TokenType.EOF, "", LoxValue.Null, line)

        fun firstLineNumber(n: Double): Token = Token(TokenType.NUMBER, n.toString(), LoxValue.Num(n), 1)

        fun firstLineNumber(n: Int): Token = Token(TokenType.NUMBER, n.toString(), LoxValue.Num(n.toDouble()), 1)

        fun firstLinePlusOperator(): Token = Token(TokenType.PLUS, "+", LoxValue.Null, 1)
    }
}