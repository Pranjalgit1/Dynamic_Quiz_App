package quiz.controller;

import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import quiz.SceneManager;
import quiz.model.Question;
import quiz.model.QuizResult;
import quiz.model.QuizSession;

public class ResultController {

    @FXML
    private ImageView trophyImage;
    @FXML
    private Label playerLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label percentLabel;
    @FXML
    private Label gradeIcon;
    @FXML
    private Label gradeLabel;
    @FXML
    private Label feedbackLabel;
    @FXML
    private HBox gradeBox;
    @FXML
    private VBox statsPanel;
    @FXML
    private HBox statsRow;
    @FXML
    private Canvas chartCanvas;
    @FXML
    private Canvas bgCanvas;
    @FXML
    private Label insightLine1;
    @FXML
    private Label insightLine2;

    private int totalQuestions;
    private int correctCount;
    private int incorrectCount;
    private double accuracy;

    @FXML
    public void initialize() {
        QuizSession session = SceneManager.getCurrentSession();
        String name = SceneManager.getPlayerName();

        int score = session.getScore();
        int maxScore = session.getMaxScore();
        int userId = session.getUserId();

        QuizResult result = new QuizResult(
                0, userId, session.getSessionId(), score, maxScore, "JAVA");

        double percentage = result.getPercentage();
        String grade = result.getGrade();

        List<Question> questions = session.getQuestions();
        List<Integer> userAns = session.getUserAnswers();
        totalQuestions = questions.size();
        correctCount = 0;
        for (int i = 0; i < totalQuestions; i++) {
            if (userAns.get(i) != -1 && questions.get(i).isCorrect(userAns.get(i))) {
                correctCount++;
            }
        }
        incorrectCount = totalQuestions - correctCount;
        accuracy = percentage;

        playerLabel.setText("Well played.");
        scoreLabel.setText(correctCount + " / " + totalQuestions);
        percentLabel.setText(String.format("%.1f%%", percentage));
        gradeLabel.setText("Grade: " + grade);
        gradeIcon.setText(grade);
        feedbackLabel.setText(getFeedback(grade));

        styleGrade(grade);
        buildInsights(session);
        drawBackground();
        playEntryAnimations();
    }

    @FXML
    private void handleStats() {
        statsPanel.setVisible(true);
        statsPanel.setManaged(true);
        buildStatsRow();
        drawChart();

        SceneManager.getPrimaryStage().setWidth(1080);

        FadeTransition ft = new FadeTransition(Duration.millis(350), statsPanel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        TranslateTransition tt = new TranslateTransition(Duration.millis(350), statsPanel);
        tt.setFromX(40);
        tt.setToX(0);
        tt.play();
    }

    @FXML
    private void handleCloseStats() {
        FadeTransition ft = new FadeTransition(Duration.millis(250), statsPanel);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            statsPanel.setVisible(false);
            statsPanel.setManaged(false);
            SceneManager.getPrimaryStage().setWidth(560);
        });
        ft.play();
    }

    private void buildStatsRow() {
        statsRow.getChildren().clear();

        String[][] data = {
                { "📋", String.valueOf(totalQuestions), "Total Questions", "#4fc3f7" },
                { "✅", String.valueOf(correctCount), "Correct", "#66bb6a" },
                { "❌", String.valueOf(incorrectCount), "Incorrect", "#ef5350" },
                { "📈", String.format("%.0f%%", accuracy), "Accuracy", "#ab47bc" },
        };

        for (String[] d : data) {
            VBox card = new VBox(4);
            card.setAlignment(Pos.CENTER);
            card.setPrefWidth(105);
            card.setPadding(new Insets(12, 8, 12, 8));
            card.getStyleClass().add("stat-mini-card");

            Label icon = new Label(d[0]);
            icon.setStyle("-fx-font-size: 22;");

            Label value = new Label(d[1]);
            value.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");

            Label label = new Label(d[3].equals("#66bb6a") ? d[2]
                    : d[3].equals("#ef5350") ? d[2] : d[3].equals("#ab47bc") ? d[2] : d[2]);
            label.setStyle("-fx-font-size: 11; -fx-text-fill: " + d[3] + "; -fx-font-weight: bold;");

            card.getChildren().addAll(icon, value, label);
            statsRow.getChildren().add(card);
        }
    }

    private void drawChart() {
        QuizSession session = SceneManager.getCurrentSession();
        List<Question> questions = session.getQuestions();
        List<Integer> userAns = session.getUserAnswers();

        int total = questions.size();
        int correct = 0, answered = 0, unanswered = 0;
        int easy = 0, medium = 0, hard = 0;

        for (int i = 0; i < total; i++) {
            Question q = questions.get(i);
            int ans = userAns.get(i);

            if (ans == -1) {
                unanswered++;
            } else {
                answered++;
                if (q.isCorrect(ans))
                    correct++;
            }

            switch (q.getDifficulty().toLowerCase()) {
                case "easy":
                    easy++;
                    break;
                case "medium":
                    medium++;
                    break;
                case "hard":
                    hard++;
                    break;
            }
        }

        GraphicsContext gc = chartCanvas.getGraphicsContext2D();
        double W = chartCanvas.getWidth();
        double H = chartCanvas.getHeight();

        gc.clearRect(0, 0, W, H);

        gc.setFill(Color.web("#0d0b1e"));
        gc.fillRoundRect(0, 0, W, H, 16, 16);

        double padL = 40, padR = 16, padT = 30, padB = 36;
        double chartW = W - padL - padR;
        double chartH = H - padT - padB;

        String[] labels = { "Answered", "Unanswered", "Correct",
                "Easy", "Medium", "Hard" };
        int[] values = { answered, unanswered, correct,
                easy, medium, hard };
        Color[] colors = {
                Color.web("#00d2ff"),
                Color.web("#7a7a90"),
                Color.web("#00e676"),
                Color.web("#7b2ff7"),
                Color.web("#ffab40"),
                Color.web("#ff5252"),
        };

        int maxVal = 0;
        for (int v : values)
            if (v > maxVal)
                maxVal = v;
        if (maxVal == 0)
            maxVal = 1;

        int yMax = ((maxVal / 2) + 1) * 2;
        if (yMax < maxVal)
            yMax = maxVal + 1;

        double groupW = chartW / labels.length;
        double barW = groupW * 0.50;
        double barGap = (groupW - barW) / 2.0;

        int ySteps = Math.min(yMax, 10);
        if (ySteps <= 0)
            ySteps = 1;
        for (int i = 0; i <= ySteps; i++) {
            double y = padT + chartH - (chartH * i / (double) ySteps);
            int yVal = (int) Math.round((double) yMax * i / (double) ySteps);
            gc.setStroke(Color.web("rgba(255,255,255,0.06)"));
            gc.setLineWidth(1);
            gc.strokeLine(padL, y, padL + chartW, y);
            gc.setFill(Color.web("#7a7a90"));
            gc.setFont(Font.font("System", 10));
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.fillText(String.valueOf(yVal), padL - 6, y + 4);
        }

        for (int i = 0; i < values.length; i++) {
            double barH = values[i] == 0 ? 3
                    : (values[i] / (double) yMax) * chartH;
            double x = padL + i * groupW + barGap;
            double y = padT + chartH - barH;

            gc.setFill(colors[i].deriveColor(0, 1, 1, 0.2));
            gc.fillRoundRect(x - 1, y + 1, barW + 2, barH, 6, 6);

            gc.setFill(colors[i]);
            gc.fillRoundRect(x, y, barW, barH, 6, 6);

            gc.setFill(colors[i].brighter());
            gc.setFont(Font.font("System", FontWeight.BOLD, 12));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText(String.valueOf(values[i]), x + barW / 2, y - 6);

            gc.setFill(Color.web("#9e9eb8"));
            gc.setFont(Font.font("System", 10));
            gc.fillText(labels[i], x + barW / 2, padT + chartH + 16);
        }

        gc.setStroke(Color.web("rgba(255,255,255,0.12)"));
        gc.setLineWidth(1);
        gc.strokeLine(padL, padT, padL, padT + chartH);
        gc.strokeLine(padL, padT + chartH, padL + chartW, padT + chartH);
    }

    private void buildInsights(QuizSession session) {
        List<Question> questions = session.getQuestions();
        List<Integer> userAns = session.getUserAnswers();

        int easyCorrect = 0, easyTotal = 0;
        int medCorrect = 0, medTotal = 0;
        int hardCorrect = 0, hardTotal = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            boolean correct = userAns.get(i) != -1 && q.isCorrect(userAns.get(i));
            switch (q.getDifficulty().toLowerCase()) {
                case "easy":
                    easyTotal++;
                    if (correct)
                        easyCorrect++;
                    break;
                case "medium":
                    medTotal++;
                    if (correct)
                        medCorrect++;
                    break;
                case "hard":
                    hardTotal++;
                    if (correct)
                        hardCorrect++;
                    break;
            }
        }

        insightLine1.setText("Review incorrect answers to improve your score.");

        double ep = easyTotal > 0 ? (easyCorrect * 100.0 / easyTotal) : 0;
        double mp = medTotal > 0 ? (medCorrect * 100.0 / medTotal) : 0;
        double hp = hardTotal > 0 ? (hardCorrect * 100.0 / hardTotal) : 0;

        String best = "Easy";
        if (mp >= ep && mp >= hp)
            best = "Medium";
        if (hp >= ep && hp >= mp)
            best = "Hard";

        insightLine2.setText("You did great in " + best + " questions. Keep practicing!");
    }

    private void drawBackground() {
        GraphicsContext gc = bgCanvas.getGraphicsContext2D();
        double W = bgCanvas.getWidth();
        double H = bgCanvas.getHeight();

        gc.clearRect(0, 0, W, H);

        RadialGradient glow1 = new RadialGradient(0, 0, W * 0.28, H * 0.45, 300,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("rgba(80,40,200,0.12)")),
                new Stop(1, Color.TRANSPARENT));
        gc.setFill(glow1);
        gc.fillRect(0, 0, W, H);

        RadialGradient glow2 = new RadialGradient(0, 0, W * 0.72, H * 0.45, 280,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("rgba(0,150,255,0.08)")),
                new Stop(1, Color.TRANSPARENT));
        gc.setFill(glow2);
        gc.fillRect(0, 0, W, H);

        gc.setFill(Color.web("rgba(120,100,255,0.15)"));
        double[][] particles = {
                { 80, 60, 6 }, { W - 50, 80, 4 }, { W - 90, H - 60, 7 },
                { 60, H - 100, 5 }, { W / 2, 40, 3 }, { W * 0.3, H - 40, 4 },
                { W * 0.7, H - 30, 5 }, { W * 0.85, H * 0.3, 3 },
        };
        for (double[] p : particles) {
            gc.setFill(Color.web("rgba(120,100,255,0.12)"));

            gc.save();
            gc.translate(p[0], p[1]);
            gc.rotate(45);
            gc.fillRect(-p[2], -p[2], p[2] * 2, p[2] * 2);
            gc.restore();
        }

        gc.setFill(Color.web("rgba(0,210,255,0.10)"));
        double[][] dots = {
                { 150, 100, 2 }, { W - 120, 150, 2.5 }, { W * 0.5, H - 60, 2 },
                { 200, H * 0.7, 1.5 }, { W * 0.6, 80, 2 },
        };
        for (double[] d : dots) {
            gc.fillOval(d[0], d[1], d[2] * 2, d[2] * 2);
        }
    }

    private void playEntryAnimations() {

        ScaleTransition trophy = new ScaleTransition(Duration.millis(600), trophyImage);
        trophy.setFromX(0.3);
        trophy.setFromY(0.3);
        trophy.setToX(1.0);
        trophy.setToY(1.0);
        trophy.setCycleCount(1);
        trophy.play();

        Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(trophyImage.scaleXProperty(), 1.0),
                        new KeyValue(trophyImage.scaleYProperty(), 1.0)),
                new KeyFrame(Duration.millis(1200),
                        new KeyValue(trophyImage.scaleXProperty(), 1.08),
                        new KeyValue(trophyImage.scaleYProperty(), 1.08)),
                new KeyFrame(Duration.millis(2400),
                        new KeyValue(trophyImage.scaleXProperty(), 1.0),
                        new KeyValue(trophyImage.scaleYProperty(), 1.0)));
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setDelay(Duration.millis(700));
        pulse.play();
    }

    private String getFeedback(String grade) {
        switch (grade) {
            case "A+":
                return "Outstanding! You are a Java expert!";
            case "A":
                return "Excellent work! You have strong Java skills!";
            case "B":
                return "Good job! Keep learning and you'll master it!";
            case "C":
                return "Not bad! Review the topics and try again.";
            default:
                return "Don't give up! Practice makes you perfect.";
        }
    }

    private void styleGrade(String grade) {
        String baseColor;
        switch (grade) {
            case "A+":
            case "A":
                baseColor = "#00e676";
                break;
            case "B":
                baseColor = "#ffab40";
                break;
            case "C":
                baseColor = "#ffd740";
                break;
            default:
                baseColor = "#ff5252";
                break;
        }

        gradeLabel.setStyle(
                "-fx-text-fill: " + baseColor + ";" +
                        "-fx-font-size: 22;" +
                        "-fx-font-weight: bold;");
        gradeIcon.setStyle(
                "-fx-text-fill: " + baseColor + ";" +
                        "-fx-font-size: 18; -fx-font-weight: bold;" +
                        "-fx-min-width: 36; -fx-min-height: 36;" +
                        "-fx-max-width: 36; -fx-max-height: 36;" +
                        "-fx-alignment: center;" +
                        "-fx-background-radius: 50;" +
                        "-fx-border-radius: 50;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-color: " + baseColor + ";");
        gradeBox.setStyle(
                "-fx-background-color: rgba(255,82,82,0.08);" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + baseColor + "30;" +
                        "-fx-border-radius: 14;" +
                        "-fx-border-width: 1;");
    }

    @FXML
    private void handleRestart() {
        SceneManager.switchScene("start.fxml");
    }

    @FXML
    private void handleAnalysis() {
        SceneManager.switchScene("analysis.fxml");
    }
}