package com.krafting.lox

class TokenScanner(private val source: String) {
    private var start = 0
    private var currentIdx = 0
    private var line = 1
    private val tokens: MutableList<Token> = mutableListOf()

    fun parseTokens(): List<Token> {
        while (!hasEnded()){
            start=currentIdx
            scanToken()
        }
        tokens.add(Token.eof(line))
        return tokens.toList()
    }

    private fun scanToken(){
        val currChar: Char = advanceAndFetch()
        when(currChar){
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            '!' -> addIfMatchesOrElse('=', TokenType.BANG_EQUAL, TokenType.BANG)
            '=' -> addIfMatchesOrElse('=', TokenType.EQUAL_EQUAL, TokenType.EQUAL)
            '<' -> addIfMatchesOrElse('=', TokenType.LESS_EQUAL, TokenType.LESS)
            '>' -> addIfMatchesOrElse('=', TokenType.GREATER_EQUAL, TokenType.GREATER)
            '/' -> {
                if(matches('/')){
                    advanceUntil('\n')
                } else {
                    addToken(TokenType.SLASH)
                }
            }
            in 'a'..'z', in 'A'..'Z' -> handleIdentifier()
            '\n' -> advanceLine()
            '"' -> addString()
            in '0'..'9' -> addNumber()
        }
    }

    private fun handleIdentifier(){
        advanceWhile { it.isLetterOrDigit() || it=='_'}
        val type = KeywordsMap.find(currentString()) ?: TokenType.IDENTIFIER
        addToken(type)
    }

    private fun addNumber() {
        advanceWhile { it.isDigit() }
        if(!hasEnded() && currentChar()=='.' && nextChar()?.isDigit() == true){
            advance()
            advanceWhile { it.isDigit() }
        }
        addToken(TokenType.NUMBER, LoxValue.Num(currentString().toDouble()))
    }

    private fun advanceWhile(pred: (Char) -> Boolean){
        while(!hasEnded() && pred.invoke(currentChar())){
            advance()
        }
    }

    private fun addString() {
        while(currentChar()!='"' && !hasEnded()){
            if(currentChar()=='\n'){
                advanceLine()
            }
            advance()
        }
        if(hasEnded()){
            throw IllegalStateException("Unterminated string")
        }
        advance() //closing "
        addToken(TokenType.STRING, source.substring(start+1 until currentIdx-1))
    }

    private fun addIfMatchesOrElse(c: Char, tokenType1: TokenType, tokenType2: TokenType){
        if(matches(c)){
            addToken(tokenType1)
            advance()
        } else {
            addToken(tokenType2)
        }
    }

    private fun addToken(type: TokenType){
        addToken(type, currentString())
    }

    private fun addToken(type: TokenType, o: LoxValue){
        addToken(type, currentString(), o)
    }

    private fun addToken(type: TokenType, value: String){
        addToken(type, value, null)
    }

    private fun addToken(type: TokenType, value: String, o: LoxValue?){
        tokens.add(Token(type, value, o ?: LoxValue.Null, line))
    }

    private fun matches(c: Char): Boolean = !hasEnded() && currentChar()==c

    private fun currentChar(): Char = source[currentIdx]

    private fun nextChar(): Char? = if(currentIdx<source.length) { source[currentIdx+1] } else null

    private fun advanceAndFetch(): Char = source[currentIdx++]

    private fun advance() {
        currentIdx++
    }

    private fun advanceLine() {
        line++
    }

    private fun advanceUntil(c: Char) {
        while(!hasEnded() && currentChar()!=c){
            advance()
        }
    }

    private fun currentString(): String = source.substring(start until currentIdx)

    private fun hasEnded(): Boolean = currentIdx >= source.length
}