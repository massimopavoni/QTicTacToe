package com.github.massimopavoni.qtictactoe;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Random;

/**
 * The UI class is the main class for the AI-TicTacToe application.
 * It extends the JavaFX Application class, but also contains the controller methods.
 */
public class UI extends Application {
    /**
     * Alert to display at the end of the game.
     */
    private final Alert endgameAlert = new Alert(Alert.AlertType.INFORMATION);
    /**
     * Alert to display for AI training messages.
     */
    private final Alert trainingAlert = new Alert(Alert.AlertType.INFORMATION);
    /**
     * Random number generator for choosing the first player.
     */
    private final Random rng = new Random();
    /**
     * AI player instance.
     */
    private final AIPlayer aiPlayer = new AIPlayer(0.5, 0.9, 0.95);
    /**
     * Number of games to train the AI player.
     */
    private final int aiTrainingGamesNumber = 16000;
    /**
     * Flag for indicating if the AI is training in the background.
     */
    private boolean training = false;
    /**
     * GridPane for the TicTacToe game.
     */
    @FXML
    private GridPane gameGridPane;
    /**
     * Label for indicating the human player token.
     */
    @FXML
    private Label humanPlayerLabel;
    /**
     * TicTacToe game instance.
     */
    private TicTacToe game = new TicTacToe();
    /**
     * Current player token.
     */
    private Token currentPlayer = Token.X;

    /**
     * Main method for the TicTacToe application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start method for JavaFX application.
     *
     * @param stage the stage for the application
     * @throws Exception if any error occurs
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(
                FXMLLoader.load(
                        Objects.requireNonNull(
                                UI.class.getResource("UI.fxml")))));
        stage.sizeToScene();
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.setTitle("QTicTacToe");
        stage.show();
    }

    /**
     * Stop method for JavaFX application.
     */
    @Override
    public void stop() {
        Thread.currentThread().interrupt();
        System.exit(0);
    }

    /**
     * Initialization method for JavaFX application.
     */
    @FXML
    public void initialize() {
        endgameAlert.setTitle("Info");
        endgameAlert.setHeaderText("Game ended");
        trainingAlert.setTitle("Info");
        trainingAlert.setHeaderText("AI training");
    }

    /**
     * Game buttons onAction event handler.
     *
     * @param event action event
     */
    @FXML
    private void gameButton_onAction(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        int row = GridPane.getRowIndex(clickedButton);
        int col = GridPane.getColumnIndex(clickedButton);
        if (game.playMove(row, col, currentPlayer)) {
            updateButtonText(clickedButton);
            currentPlayer = TicTacToe.changePlayer(currentPlayer);
            playMoveAI();
            if (game.getStatus() != GameStatus.STILL_PLAYING) {
                gameGridPane.getChildren().forEach(node -> node.setDisable(true));
                endgameAlert.setContentText(game.getStatus().message);
                endgameAlert.showAndWait();
            }
        }
    }

    /**
     * New game button onAction event handler.
     */
    @FXML
    private void newGameButton_onAction() {
        game = new TicTacToe();
        gameGridPane.getChildren().forEach(node -> {
            Button button = (Button) node;
            button.setText("");
            button.setDisable(false);
        });
        currentPlayer = Token.X;
        if (rng.nextBoolean())
            playMoveAI();
        String tempLabelText = humanPlayerLabel.getText();
        humanPlayerLabel.setText(tempLabelText
                .substring(0, tempLabelText.length() - 1) + currentPlayer.symbol);
    }

    /**
     * Update the text of a button after a move.
     *
     * @param clickedButton the button that was clicked
     */
    private void updateButtonText(Button clickedButton) {
        clickedButton.setText(currentPlayer.symbol);
        clickedButton.setDisable(true);
    }

    /**
     * Play a move for the AI player.
     */
    private void playMoveAI() {
        int move = aiPlayer.chooseMove(game, false);
        int row = move / 3;
        int col = move % 3;
        if (game.playMove(row, col, currentPlayer)) {
            updateButtonText((Button) gameGridPane.getChildren().get(move));
            currentPlayer = TicTacToe.changePlayer(currentPlayer);
        }
    }

    /**
     * Train AI button onAction event handler.
     */
    @FXML
    private void trainAIButton_onAction() {
        if (training) {
            trainingAlert.setContentText("The AI agent is currently training\n" +
                    "(" + (aiPlayer.getCurrentGame() + 1) + "/" + aiTrainingGamesNumber + " games played).\n" +
                    "Please wait until it is done.");
            trainingAlert.showAndWait();
            return;
        }

        if (aiPlayer.getMemorySize() != 0) {
            trainingAlert.setContentText("The AI agent is already trained\n" +
                    "(" + aiPlayer.getMemorySize() + " state-action pairs in memory).\n" +
                    "Restart the application to train from scratch.");
            trainingAlert.showAndWait();
            return;
        }

        Task<Void> task = backgroundAITraining();
        new Thread(task).start();

        trainingAlert.setContentText("The AI agent will be trained in a parallel thread.\n" +
                "You will be notified when it is done.");
        trainingAlert.showAndWait();
    }

    /**
     * Create a background task for training the AI player.
     *
     * @return the training task
     */
    private Task<Void> backgroundAITraining() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                training = true;
                aiPlayer.train(aiTrainingGamesNumber);
                training = false;
                return null;
            }
        };

        task.setOnSucceeded(event -> {
            if (trainingAlert.isShowing())
                trainingAlert.hide();

            trainingAlert.setContentText("Training completed.\n" +
                    "The AI agent saved\n" +
                    aiPlayer.getMemorySize() + " state-action pairs in its memory.\n");

            Platform.runLater(trainingAlert::showAndWait);
        });

        return task;
    }
}
