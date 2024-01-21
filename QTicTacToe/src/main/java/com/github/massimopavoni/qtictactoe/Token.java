package com.github.massimopavoni.qtictactoe;

/**
 * Enumeration for the possible tokens in the board.
 */
public enum Token {
    /**
     * The X token.
     */
    X("X"),
    /**
     * The O token.
     */
    O("O"),
    /**
     * The empty token.
     */
    EMPTY("_");

    /**
     * The symbol of this token.
     */
    public final String symbol;

    /**
     * Constructor for the token.
     *
     * @param symbol the symbol of this token
     */
    Token(String symbol) {
        this.symbol = symbol;
    }
}
