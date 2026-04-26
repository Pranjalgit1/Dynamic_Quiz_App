package quiz.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import quiz.SceneManager;
import quiz.manager.DatabaseManager;
import quiz.model.Question;
import quiz.model.QuizSession;

import java.util.List;

public class StartController {

@FXML
private TextField nameField;
@FXML
private ComboBox<String> topicCombo;
@FXML
private Label errorLabel;
@FXML
private Label loadingLabel;

private long startTime;
private static final int QUIZ_QUESTION_COUNT = 10;

@FXML
public void initialize() {
    topicCombo.getItems().clear();
    topicCombo.getItems().add("All Topics");
    topicCombo.getItems().addAll(DatabaseManager.getAvailableTopics());
    topicCombo.getSelectionModel().selectFirst();
}

@FXML
private void handleStart() {
    String name = nameField.getText().trim();
    String selectedTopic = topicCombo.getValue();

    if (name.isEmpty()) {
        showError("Please enter your name!");
        shakeNode(nameField);
        return;
    }

    if (selectedTopic == null || selectedTopic.isBlank()) {
        showError("Please select a topic!");
        shakeNode(topicCombo);
        return;
    }

    errorLabel.setVisible(false);
    startTime = System.currentTimeMillis();

    // Load questions directly from local DB — no API calls, no network, instant.
    List<Question> questions = DatabaseManager.getQuestionsByTopic(selectedTopic, QUIZ_QUESTION_COUNT);

    if (questions.isEmpty()) {
        showError("No questions available for \"" + selectedTopic + "\". Please add questions to the database.");
        return;
    }

    int userId = DatabaseManager.saveUser(name);
    if (userId == -1) {
        showError("Failed to start quiz.");
        return;
    }

    int sessionId = DatabaseManager.createSession(userId);
    if (sessionId == -1) {
        showError("Failed to create session.");
        return;
    }

    QuizSession session = new QuizSession(sessionId, userId, questions);

    long latency = System.currentTimeMillis() - startTime;

    loadingLabel.setText("Loaded " + questions.size() + " questions in " + latency + " ms");
    loadingLabel.setVisible(true);
    loadingLabel.setManaged(true);

    SceneManager.setPlayerName(name);
    SceneManager.setCurrentSession(session);

    javafx.animation.PauseTransition pause =
            new javafx.animation.PauseTransition(javafx.util.Duration.seconds(0.6));

    pause.setOnFinished(ev -> SceneManager.switchScene("quiz.fxml"));
    pause.play();
}

private void showError(String msg) {
    errorLabel.setText(msg);
    errorLabel.setVisible(true);
    errorLabel.setManaged(true);
}

private void shakeNode(Node node) {
    TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
    tt.setFromX(0);
    tt.setByX(10);
    tt.setCycleCount(6);
    tt.setAutoReverse(true);
    tt.setOnFinished(e -> node.setTranslateX(0));
    tt.play();
}

}
