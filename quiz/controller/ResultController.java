package quiz.controller;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import quiz.SceneManager;
import quiz.model.Question;
import quiz.model.QuizResult;
import quiz.model.QuizSession;

public class ResultController {

    @FXML private Label playerLabel;
    @FXML private Label scoreLabel;
    @FXML private Label percentLabel;
    @FXML private Label gradeLabel;
    @FXML private Label feedbackLabel;
    @FXML private StackPane popupOverlay;
    @FXML private Canvas chartCanvas;

    @FXML
    public void initialize() {
        QuizSession session = SceneManager.getCurrentSession();
        String name = SceneManager.getPlayerName();

        int score    = session.getScore();
        int maxScore = session.getMaxScore();
        int userId   = session.getUserId();

        QuizResult result = new QuizResult(
                0, userId, session.getSessionId(), score, maxScore, "JAVA");

        double percentage = result.getPercentage();
        String grade      = result.getGrade();

        playerLabel.setText("Well played, " + name + "!");
        scoreLabel.setText(score + " / " + maxScore);
        percentLabel.setText(String.format("%.1f%%", percentage));
        gradeLabel.setText("Grade: " + grade);
        feedbackLabel.setText(getFeedback(grade));

        styleGrade(grade);
    }

    // ── Stats popup ──────────────────────────────────────────

    @FXML
    private void handleStats() {
        popupOverlay.setVisible(true);
        drawChart();
    }

    @FXML
    private void handleCloseStats() {
        popupOverlay.setVisible(false);
    }

    private void drawChart() {
    QuizSession session = SceneManager.getCurrentSession();
    List<Question> questions = session.getQuestions();
    List<Integer>  userAns  = session.getUserAnswers();

    int total = questions.size();
    int correct = 0, answered = 0, unanswered = 0;
    int easy = 0, medium = 0, hard = 0;

    for (int i = 0; i < total; i++) {
        Question q   = questions.get(i);
        int      ans = userAns.get(i);

        if (ans == -1) {
            unanswered++;
        } else {
            answered++;
            if (q.isCorrect(ans)) correct++;
        }

        switch (q.getDifficulty().toLowerCase()) {
            case "easy":   easy++;   break;
            case "medium": medium++; break;
            case "hard":   hard++;   break;
        }
    }

    GraphicsContext gc = chartCanvas.getGraphicsContext2D();
    double W = chartCanvas.getWidth();
    double H = chartCanvas.getHeight();

    gc.clearRect(0, 0, W, H);

    double padL = 50, padR = 20, padT = 60, padB = 60;
    double chartW = W - padL - padR;
    double chartH = H - padT - padB;

    String[] labels = {"Answered", "Unanswered", "Correct",
                       "Easy",     "Medium",      "Hard"};
    int[]    values = {answered, unanswered, correct,
                       easy,     medium,      hard};
    Color[]  colors = {
        Color.web("#00d2ff"),   // answered — cyan
        Color.web("#9e9eb8"),   // unanswered — muted
        Color.web("#00e676"),   // correct — green
        Color.web("#7b2ff7"),   // easy — purple
        Color.web("#ffab40"),   // medium — orange
        Color.web("#ff5252"),   // hard — red
    };

    int maxVal = Math.max(total, 1);

    double groupW = chartW / labels.length;
    double barW   = groupW * 0.52;
    double barGap = (groupW - barW) / 2.0;

    // Gridlines + Y labels
    for (int i = 0; i <= 5; i++) {
        double y   = padT + chartH - (chartH * i / 5.0);
        int   yVal = (int) Math.round((double) maxVal * i / 5.0);
        gc.setStroke(Color.web("rgba(255,255,255,0.08)"));
        gc.setLineWidth(1);
        gc.strokeLine(padL, y, padL + chartW, y);
        gc.setFill(Color.web("#9e9eb8"));
        gc.setFont(Font.font("Arial", 11));
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText(String.valueOf(yVal), padL - 6, y + 4);
    }

    // Bars
    for (int i = 0; i < values.length; i++) {
        double barH = values[i] == 0 ? 4
                    : (values[i] / (double) maxVal) * chartH;
        double x = padL + i * groupW + barGap;
        double y = padT + chartH - barH;

        // Shadow
        gc.setFill(Color.web("rgba(0,0,0,0.25)"));
        gc.fillRoundRect(x + 2, y + 2, barW, barH, 8, 8);

        // Bar
        gc.setFill(colors[i]);
        gc.fillRoundRect(x, y, barW, barH, 8, 8);

        // Value on top
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(values[i]), x + barW / 2, y - 8);

        // X label
        gc.setFill(Color.web("#9e9eb8"));
        gc.setFont(Font.font("Arial", 11));
        gc.fillText(labels[i], x + barW / 2, padT + chartH + 20);
    }

    // Axes
    gc.setStroke(Color.web("rgba(255,255,255,0.2)"));
    gc.setLineWidth(1.5);
    gc.strokeLine(padL, padT, padL, padT + chartH);
    gc.strokeLine(padL, padT + chartH, padL + chartW, padT + chartH);

    // Divider between the 2 groups
    double divX = padL + 3 * groupW;
    gc.setStroke(Color.web("rgba(255,255,255,0.15)"));
    gc.setLineDashes(4, 4);
    gc.strokeLine(divX, padT, divX, padT + chartH);
    gc.setLineDashes(0);

    // Group labels above chart
    gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
    gc.setTextAlign(TextAlignment.CENTER);
    gc.setFill(Color.web("#00d2ff"));
    gc.fillText("ANSWERS", padL + (3 * groupW) / 2, padT - 22);
    gc.setFill(Color.web("#ffab40"));
    gc.fillText("DIFFICULTY", padL + 3 * groupW + (3 * groupW) / 2, padT - 22);
}

    // ── Existing methods (unchanged) ─────────────────────────

    private String getFeedback(String grade) {
        switch (grade) {
            case "A+": return "Outstanding! You are a Java expert!";
            case "A":  return "Excellent work! You have strong Java skills!";
            case "B":  return "Good job! Keep learning and you'll master it!";
            case "C":  return "Not bad! Review the topics and try again.";
            default:   return "Don't give up! Practice makes you perfect.";
        }
    }

    private void styleGrade(String grade) {
        String style;
        switch (grade) {
            case "A+": case "A":
                style = "-fx-text-fill: #00e676;" +
                        "-fx-background-color: rgba(0,230,118,0.15);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(0,230,118,0.3);" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 5 20;" +
                        "-fx-font-size: 28;" +
                        "-fx-font-weight: bold;";
                break;
            case "B":
                style = "-fx-text-fill: #ffab40;" +
                        "-fx-background-color: rgba(255,171,64,0.15);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(255,171,64,0.3);" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 5 20;" +
                        "-fx-font-size: 28;" +
                        "-fx-font-weight: bold;";
                break;
            case "C":
                style = "-fx-text-fill: #ffd740;" +
                        "-fx-background-color: rgba(255,215,64,0.15);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(255,215,64,0.3);" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 5 20;" +
                        "-fx-font-size: 28;" +
                        "-fx-font-weight: bold;";
                break;
            default:
                style = "-fx-text-fill: #ff5252;" +
                        "-fx-background-color: rgba(255,82,82,0.15);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(255,82,82,0.3);" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 5 20;" +
                        "-fx-font-size: 28;" +
                        "-fx-font-weight: bold;";
                break;
        }
        gradeLabel.setStyle(style);
    }

    @FXML private void handleRestart() { SceneManager.switchScene("start.fxml"); }
    @FXML private void handleAnalysis() { SceneManager.switchScene("analysis.fxml"); }
}