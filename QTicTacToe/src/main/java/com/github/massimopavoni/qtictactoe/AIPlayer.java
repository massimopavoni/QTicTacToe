package com.github.massimopavoni.qtictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The AIPlayer class represents an AI player for the TicTacToe game
 * and provides methods to train the AI with Q-Learning.
 */
public class AIPlayer {
    /**
     * Random number generator for exploration chance and memory entry selection.
     */
    private static final Random rng = new Random();
    /**
     * The AI player's memory table.
     */
    private final ConcurrentMap<String, Double> memory = new ConcurrentHashMap<>();
    /**
     * The AI player's learning rate, handling how much effect new rewards have on Q-values.
     */
    private final double learningRate;
    /**
     * The AI player's discount factor, handling how much effect long term rewards are valued.
     */
    private final double discountFactor;
    /**
     * The AI player's exploration chance, handling how often the AI player will explore new moves.
     */
    private final double explorationChance;
    /**
     * The current game number.
     */
    private int currentGame;

    /**
     * Constructor for the AI player.
     *
     * @param learningRate      the learning rate
     * @param discountFactor    the discount factor
     * @param explorationChance the exploration chance
     */
    public AIPlayer(double learningRate, double discountFactor, double explorationChance) {
        this.learningRate = learningRate;
        this.discountFactor = discountFactor;
        this.explorationChance = explorationChance;
    }

    /**
     * Getter for the AI player's memory size.
     *
     * @return the memory size
     */
    public int getMemorySize() {
        return memory.size();
    }

    /**
     * Getter for the current game number.
     *
     * @return the current game number
     */
    public int getCurrentGame() {
        return currentGame;
    }

    /**
     * Choose a move for the AI player.
     *
     * @param game     the game state (must be still playing)
     * @param training whether the AI player is currently training
     * @return the chosen move as an integer
     */
    public int chooseMove(TicTacToe game, boolean training) {
        if (game.getStatus() != GameStatus.STILL_PLAYING) return -1;

        String state = game.toString();

        List<Entry<String, Double>> previousActions = memory.entrySet().stream()
                .filter(e -> e.getKey().startsWith(state))
                .toList();

        if (previousActions.isEmpty() || (training && rng.nextDouble() < explorationChance)) {
            List<Integer> possibleActions = listAllMoves(state);
            return possibleActions.get(rng.nextInt(possibleActions.size()));
        }

        double maxQValue = previousActions.stream()
                .map(Entry::getValue)
                .max(Double::compare).orElse(0.0);

        List<String> tiedActions = previousActions.stream()
                .filter(e -> e.getValue().equals(maxQValue))
                .map(Entry::getKey)
                .toList();

        String chosenAction = tiedActions.get(rng.nextInt(tiedActions.size()));

        return Character.getNumericValue(chosenAction.charAt(chosenAction.length() - 1));
    }

    /**
     * List all possible moves for a given game state.
     *
     * @param game the game state
     * @return a list of all possible moves
     */
    private List<Integer> listAllMoves(String game) {
        List<Integer> validMoves = new ArrayList<>();
        StringBuilder sb = new StringBuilder(game);
        for (int move = 0; move < 9; move++)
            if (sb.charAt(move) == '_')
                validMoves.add(move);
        return validMoves;
    }

    /**
     * Train the AI player by making it play against itself multiple times.
     *
     * @param numGames the number of games to play
     */
    public void train(int numGames) {
        System.out.println("Training AI with " + numGames + " games...");

        for (currentGame = 0; currentGame < numGames; currentGame++)
            playGame();

        System.out.println("Done training.");
        System.out.println(memory.size() + " state-actions pairs in memory.");
    }

    /**
     * Make the AI player play a game against itself.
     */
    private void playGame() {
        TicTacToe game = new TicTacToe();
        Token currentPlayer = Token.X;

        while (game.getStatus() == GameStatus.STILL_PLAYING) {
            int move = chooseMove(game, true);
            String stateAction = game + "-" + move;

            int row = move / 3;
            int col = move % 3;

            game.playMove(row, col, currentPlayer);

            String postState = game.toString();

            updateQValue(stateAction, getReward(game), postState);

            currentPlayer = TicTacToe.changePlayer(currentPlayer);
        }
    }

    /**
     * Update the reward for a given move choice.
     *
     * @param stateAction the current state-action pair
     * @param reward      the reward value
     * @param nextState   the next state
     */
    private void updateQValue(String stateAction, double reward, String nextState) {
        double currentQValue = memory.getOrDefault(stateAction, 0.0);

        double nextQValue = memory.entrySet().stream()
                .filter(e -> e.getKey().startsWith(nextState))
                .map(Entry::getValue)
                .max(Double::compare).orElse(0.0);

        double newQValue = currentQValue + learningRate * (reward - discountFactor * nextQValue - currentQValue);

        memory.put(stateAction, newQValue);
    }

    /**
     * Get a reward value for a given game state.
     *
     * @param game the game state
     * @return the reward value
     */
    private double getReward(TicTacToe game) {
        return switch (game.getStatus()) {
            case X_WINS, O_WINS -> 10.0;
            case CATS -> 5.0;
            default -> 0.0;
        };
    }
}
