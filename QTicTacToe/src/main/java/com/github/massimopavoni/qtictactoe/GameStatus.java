package com.github.massimopavoni.qtictactoe;

/**
 * Enumeration for the possible game statuses.
 */
public enum GameStatus {
    /**
     * The game is still playing.
     */
    STILL_PLAYING("Still playing..."),
    /**
     * The X player won.
     */
    X_WINS("X wins!"),
    /**
     * The O player won.
     */
    O_WINS("O wins!"),
    /**
     * The game ended in a tie.
     */
    CATS("It's a cat's game!");

    /**
     * The message for the game status.
     */
    public final String message;

    /**
     * Constructor for the game status.
     *
     * @param message the message for the game status
     */
    GameStatus(String message) {
        this.message = message;
    }
}
