package quiz.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import quiz.SceneManager;
import quiz.manager.QuestionManager;
import quiz.model.Question;
import quiz.model.QuizSession;

import java.util.List;

// Validates name input, seeds the question bank, and starts the quiz.

public class StartController {

    @FXML
    private TextField nameField;
    @FXML
    private Label errorLabel;

    /** Called when user clicks "Start Quiz" */
    @FXML
    private void handleStart() {
        String name = nameField.getText().trim();

        // Validate — name must not be empty
        if (name.isEmpty()) {
            showError("Please enter your name!");
            shakeNode(nameField);
            return;
        }

        // Pick 10 random questions from OpenTDB
        List<Question> questions;
        try {
            questions = quiz.manager.QuestionApiClient.fetchQuestions(10);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load questions. Check internet!");
            return;
        }

        int userId = quiz.manager.DatabaseManager.saveUser(name);
        int sessionId = quiz.manager.DatabaseManager.createSession(userId);

        QuizSession session = new QuizSession(sessionId, userId, questions);

        // Store shared state and switch to quiz screen
        SceneManager.setPlayerName(name);
        SceneManager.setCurrentSession(session);
        SceneManager.switchScene("quiz.fxml");
    }

    /** Show an error message below the input */
    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    /** Shake animation for invalid input feedback */
    private void shakeNode(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setFromX(0);
        tt.setByX(10);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.play();
    }

    // 15 questions data removed as we now fetch dynamically
}
