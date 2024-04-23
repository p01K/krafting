package com.krafting.lox

class KeywordsMap {
    companion object {
        val keywordsMap = mapOf<String, TokenType>(
            "and" to TokenType.AND,
            "class" to TokenType.CLASS,
            "else" to TokenType.ELSE,
            "false" to TokenType.FALSE,
            "fun" to TokenType.FUN,
            "for" to TokenType.FOR,
            "if" to TokenType.IF,
            "nil" to TokenType.NIL,
            "or" to TokenType.OR,
            "print" to TokenType.PRINT,
            "return" to TokenType.RETURN,
            "super" to TokenType.SUPER,
            "this" to TokenType.THIS,
            "true" to TokenType.TRUE,
            "var" to TokenType.VAR,
            "while" to TokenType.WHILE,
        )

        fun find(s: String): TokenType? = keywordsMap[s]
    }
}