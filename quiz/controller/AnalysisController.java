package quiz.controller;

import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import quiz.SceneManager;
import quiz.model.Question;
import quiz.model.QuizSession;

public class AnalysisController {

    @FXML
    private VBox analysisContainer;

    @FXML
    public void initialize() {
        QuizSession session = SceneManager.getCurrentSession();
        List<Question> questions = session.getQuestions();
        List<Integer> userAnswers = session.getUserAnswers();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int userAnsIdx = userAnswers.get(i);
            boolean correct = q.isCorrect(userAnsIdx);
            int marksAwarded = correct ? q.getMarks() : 0;

            VBox card = buildCard(i + 1, q, userAnsIdx, correct, marksAwarded);
            analysisContainer.getChildren().add(card);
        }
    }

    private VBox buildCard(int num, Question q,
                           int userAnsIdx, boolean correct, int marksAwarded) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(18, 22, 18, 22));

        // Card glass style — matches your .card class
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.07);" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(255,255,255,0.12);" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.35),18,0,0,6);"
        );

        // ── Header row: Q number + difficulty + marks ──
        String diff = q.getDifficulty();
        int maxMarks = q.getMarks();
        String diffColor = diffColor(diff);

        Label header = new Label(
            "Q" + num + "  ·  " + capitalize(diff) + "  ·  " + maxMarks +
            (maxMarks == 1 ? " mark" : " marks")
        );
        header.setStyle(
            "-fx-text-fill: " + diffColor + ";" +
            "-fx-font-size: 13;" +
            "-fx-font-weight: bold;"
        );

        // ── Question text ──
        Label questionLabel = new Label(q.getquesText());
        questionLabel.setWrapText(true);
        questionLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 15;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 2 0 6 0;"
        );

        card.getChildren().addAll(header, questionLabel);

        // ── User's answer pill ──
        String userAnsText;
        if (userAnsIdx >= 0 && userAnsIdx <= 3) {
            userAnsText = optionLetter(userAnsIdx) + ".  " + q.getOptions()[userAnsIdx];
        } else {
            userAnsText = "Unanswered";
        }

        String correctAnsText =
            optionLetter(q.getcorrectOptionidx()) + ".  " +
            q.getOptions()[q.getcorrectOptionidx()];

        if (correct) {
            // ── Correct: show one green pill ──
            HBox row = buildAnswerRow(
                "Your answer: " + userAnsText,
                "rgba(0,230,118,0.18)",
                "rgba(0,230,118,0.45)",
                "#00e676",
                "+" + marksAwarded + (marksAwarded == 1 ? " mark" : " marks")
            );
            card.getChildren().add(row);

        } else {
            // ── Wrong / Unanswered: show red pill + green correct pill ──
            String wrongPrefix = userAnsIdx < 0 ? "—  " : "Your answer: ";
            HBox wrongRow = buildAnswerRow(
                wrongPrefix + userAnsText,
                "rgba(255,82,82,0.15)",
                "rgba(255,82,82,0.4)",
                "#ff5252",
                "+0 marks"
            );

            HBox correctRow = buildAnswerRow(
                "Correct answer: " + correctAnsText,
                "rgba(0,230,118,0.12)",
                "rgba(0,230,118,0.35)",
                "#00e676",
                null
            );

            card.getChildren().addAll(wrongRow, correctRow);
        }

        // ── Explanation (if available) ──
        /*String exp = q.getExplanation();
        if (exp != null && !exp.isBlank()) {
            Label expLabel = new Label("💡 " + exp);
            expLabel.setWrapText(true);
            expLabel.setStyle(
                "-fx-text-fill: #9e9eb8;" +
                "-fx-font-size: 13;" +
                "-fx-padding: 6 10;" +
                "-fx-background-color: rgba(255,255,255,0.04);" +
                "-fx-background-radius: 8;"
            );
            card.getChildren().add(expLabel);
        }*/

        return card;
    }

    /**
     * Builds a single answer pill row with optional marks label on the right.
     */
    private HBox buildAnswerRow(String text,
                                String bgColor,
                                String borderColor,
                                String textColor,
                                String marksText) {

        HBox row = new HBox();
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setSpacing(10);

        Label pill = new Label(text);
        pill.setWrapText(true);
        pill.setMaxWidth(580);
        pill.setStyle(
            "-fx-text-fill: " + textColor + ";" +
            "-fx-font-size: 14;" +
            "-fx-padding: 8 14;" +
            "-fx-background-color: " + bgColor + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + borderColor + ";" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        HBox.setHgrow(pill, Priority.ALWAYS);
        row.getChildren().add(pill);

        if (marksText != null) {
            Label marks = new Label(marksText);
            marks.setStyle(
                "-fx-text-fill: #9e9eb8;" +
                "-fx-font-size: 13;" +
                "-fx-font-weight: bold;"
            );
            row.getChildren().add(marks);
        }

        return row;
    }

    private String diffColor(String diff) {
        if (diff == null) return "#9e9eb8";
        switch (diff.toLowerCase()) {
            case "easy":   return "#00e676";
            case "medium": return "#ffab40";
            case "hard":   return "#ff5252";
            default:       return "#9e9eb8";
        }
    }

    private String optionLetter(int idx) {
        switch (idx) {
            case 0: return "A";
            case 1: return "B";
            case 2: return "C";
            case 3: return "D";
            default: return "?";
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    @FXML
    private void handleBack() {
        SceneManager.switchScene("result.fxml");
    }
}