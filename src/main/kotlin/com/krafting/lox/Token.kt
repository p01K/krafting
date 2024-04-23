package com.krafting.lox

class Token(val tokenType: TokenType, val lexeme: String, val literal: Object?, val line: Int) {
}