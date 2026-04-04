package quiz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import quiz.SceneManager;
import quiz.model.QuizResult;
import quiz.model.QuizSession;

//Controller for the Result screen.
//Displays score, percentage, letter grade, and feedback.

public class ResultController {

    @FXML
    private Label playerLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label percentLabel;
    @FXML
    private Label gradeLabel;
    @FXML
    private Label feedbackLabel;

    /** Called automatically after FXML is loaded */
    @FXML
    public void initialize() {
        QuizSession session = SceneManager.getCurrentSession();
        String name = SceneManager.getPlayerName();

        int score = session.getScore();
        int maxScore = session.getMaxScore();
        int userId = session.getUserId();

        // Use the existing QuizResult model for grade & percentage
        QuizResult result = new QuizResult(
                0, userId, session.getSessionId(), score, maxScore, "JAVA");

        double percentage = result.getPercentage();
        String grade = result.getGrade();

        // Populate labels
        playerLabel.setText("Well played, " + name + "!");
        scoreLabel.setText(score + " / " + maxScore);
        percentLabel.setText(String.format("%.1f%%", percentage));
        gradeLabel.setText("Grade: " + grade);
        feedbackLabel.setText(getFeedback(grade));

        // Color the grade badge based on performance
        styleGrade(grade);
    }

    private String getFeedback(String grade) {
        switch (grade) {
            case "A+":
                return "Outstanding! You are a Java expert!";
            case "A":
                return "Excellent work You have strong Java skills!";
            case "B":
                return "Good job Keep learning and you'll master it!";
            case "C":
                return "Not bad Review the topics and try again.";
            default:
                return "Don't give up! Practice makes you perfect.";
        }
    }

    // Dynamically color the grade label
    private void styleGrade(String grade) {
        String style;
        switch (grade) {
            case "A+":
            case "A":
                style = "-fx-text-fill: #00e676;"
                        + "-fx-background-color: rgba(0,230,118,0.15);"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: rgba(0,230,118,0.3);"
                        + "-fx-border-radius: 10;"
                        + "-fx-padding: 5 20;"
                        + "-fx-font-size: 28;"
                        + "-fx-font-weight: bold;";
                break;
            case "B":
                style = "-fx-text-fill: #ffab40;"
                        + "-fx-background-color: rgba(255,171,64,0.15);"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: rgba(255,171,64,0.3);"
                        + "-fx-border-radius: 10;"
                        + "-fx-padding: 5 20;"
                        + "-fx-font-size: 28;"
                        + "-fx-font-weight: bold;";
                break;
            case "C":
                style = "-fx-text-fill: #ffd740;"
                        + "-fx-background-color: rgba(255,215,64,0.15);"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: rgba(255,215,64,0.3);"
                        + "-fx-border-radius: 10;"
                        + "-fx-padding: 5 20;"
                        + "-fx-font-size: 28;"
                        + "-fx-font-weight: bold;";
                break;
            default: // F
                style = "-fx-text-fill: #ff5252;"
                        + "-fx-background-color: rgba(255,82,82,0.15);"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: rgba(255,82,82,0.3);"
                        + "-fx-border-radius: 10;"
                        + "-fx-padding: 5 20;"
                        + "-fx-font-size: 28;"
                        + "-fx-font-weight: bold;";
                break;
        }
        gradeLabel.setStyle(style);
    }

    /** Called when user clicks "Play Again" */
    @FXML
    private void handleRestart() {
        SceneManager.switchScene("start.fxml");
    }
}
