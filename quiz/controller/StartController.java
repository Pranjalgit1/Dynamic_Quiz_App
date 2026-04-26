package quiz.controller;

import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import quiz.SceneManager;
import quiz.model.Question;
import quiz.model.QuizSession;

import java.util.List;

public class StartController {

@FXML private TextField nameField;
@FXML private Label errorLabel;
@FXML private Label loadingLabel;

private long startTime;

@FXML
private void handleStart() {
    String name = nameField.getText().trim();

    if (name.isEmpty()) {
        showError("Please enter your name!");
        shakeNode(nameField);
        return;
    }

    loadingLabel.setText("Loading questions...");
    loadingLabel.setVisible(true);
    loadingLabel.setManaged(true);

    errorLabel.setVisible(false);
    nameField.setDisable(true);

    startTime = System.currentTimeMillis();

    Task<List<Question>> task = new Task<>() {
        @Override
        protected List<Question> call() throws Exception {
            return quiz.manager.QuestionApiClient.fetchQuestions(10);
        }
    };

task.setOnSucceeded(e -> {
    long endTime = System.currentTimeMillis();
    long latency = endTime - startTime;

    loadingLabel.setText("Loaded in " + latency + " ms");

    List<Question> questions = task.getValue();

    int userId = quiz.manager.DatabaseManager.saveUser(name);
    if (userId == -1) {
        showError("Failed to start quiz.");
        return;
    }

    int sessionId = quiz.manager.DatabaseManager.createSession(userId);
    if (sessionId == -1) {
        showError("Failed to create session.");
        return;
    }

    QuizSession session = new QuizSession(sessionId, userId, questions);

    SceneManager.setPlayerName(name);
    SceneManager.setCurrentSession(session);

    // Delay before switching scene 
    javafx.animation.PauseTransition pause =
            new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));

    pause.setOnFinished(ev -> SceneManager.switchScene("quiz.fxml"));
    pause.play();
});

    task.setOnFailed(e -> {
        showError("Failed to load questions. Check internet!");
        nameField.setDisable(false);
        loadingLabel.setVisible(false);
    });

    new Thread(task).start();
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
