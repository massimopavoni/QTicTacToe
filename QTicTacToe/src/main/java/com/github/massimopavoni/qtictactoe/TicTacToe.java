package com.github.massimopavoni.qtictactoe; 

/**
 * Class for the main game logic of Tic Tac Toe.
 */
public class TicTacToe {
    /**
     * The positions for a winning straight line.
     */
    private static final int[] WINNING_STRAIGHT_LINE = {0, 1, 2};
    /**
     * The positions for a winning diagonal.
     */
    private static final int[] WINNING_DIAGONAL = {0, 1, 2};
    /**
     * The positions for a winning anti-diagonal.
     */
    private static final int[] WINNING_ANTIDIAGONAL = {2, 1, 0};
    /**
     * The board of this game.
     */
    private final Token[][] board = new Token[3][3];
    /**
     * The current status of this game.
     */
    private GameStatus status = GameStatus.STILL_PLAYING;
    /**
     * The current player of this game.
     */
    private Token currentPlayer = Token.X;


    /**
     * Constructor initializing the current game's board with empty tokens.
     */
    public TicTacToe() {
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                board[row][col] = Token.EMPTY;
    }

    /**
     * Static utility method to change the current player.
     *
     * @param player the current player
     * @return the other player
     */
    public static Token changePlayer(Token player) {
        return player == Token.X ? Token.O : Token.X;
    }

    /**
     * Getter for the current game status.
     *
     * @return game status
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Play a move in the current game.
     *
     * @param row    row of the move
     * @param col    column of the move
     * @param player player making the move
     * @return true if the move was successful, false otherwise
     */
    public boolean playMove(int row, int col, Token player) {
        if (status != GameStatus.STILL_PLAYING ||
                !isEmpty(row, col) ||
                player != currentPlayer)
            return false;
        board[row][col] = player;
        currentPlayer = changePlayer(player);
        updateStatus();
        return true;
    }

    /**
     * Checks to see if a location is empty and therefore a valid move.
     * Note that we can use the short-circuiting nature of the boolean and operator
     * to avoid an IndexOutOfBoundsException when accessing the board array.
     *
     * @param row row of the location to check
     * @param col column of the location to check
     * @return true if the location is empty, false otherwise
     */
    public boolean isEmpty(int row, int col) {
        return row >= 0 && row <= 2 &&
                col >= 0 && col <= 2 &&
                board[row][col] == Token.EMPTY;
    }

    /**
     * Update the status of the game based on the current board.
     */
    private void updateStatus() {
        if (isWinner(Token.X))
            status = GameStatus.X_WINS;
        else if (isWinner(Token.O))
            status = GameStatus.O_WINS;
        else if (isCats())
            status = GameStatus.CATS;
        else
            status = GameStatus.STILL_PLAYING;
    }

    /**
     * Check if a player has won the game.
     *
     * @param player the player to check
     * @return true if the player has won, false otherwise
     */
    private boolean isWinner(Token player) {
        for (int line = 0; line < 3; line++)
            if (testLine(new int[]{line, line, line}, WINNING_STRAIGHT_LINE, player) ||
                    testLine(WINNING_STRAIGHT_LINE, new int[]{line, line, line}, player))
                return true;

        return testLine(WINNING_DIAGONAL, WINNING_DIAGONAL, player)
                || testLine(WINNING_ANTIDIAGONAL, WINNING_DIAGONAL, player);
    }

    /**
     * Test if a line of the board is occupied by a player.
     *
     * @param rows   rows of the line cells
     * @param cols   columns of the line cells
     * @param player player to check
     * @return true if the line is occupied by the player, false otherwise
     */
    private boolean testLine(int[] rows, int[] cols, Token player) {
        return board[rows[0]][cols[0]] == player &&
                board[rows[1]][cols[1]] == player &&
                board[rows[2]][cols[2]] == player;
    }

    /**
     * Check if the game ended in a tie.
     *
     * @return true if the game ended in a tie, false otherwise
     */
    private boolean isCats() {
        for (Token[] col : board)
            for (Token token : col)
                if (token == Token.EMPTY)
                    return false;
        return true;
    }

    /**
     * ToString method for the current board, used for storing the game state.
     *
     * @return the string representation of the current board
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Token[] row : board) {
            for (Token token : row)
                sb.append(token.symbol);
        }
        return sb.toString();
    }
}
