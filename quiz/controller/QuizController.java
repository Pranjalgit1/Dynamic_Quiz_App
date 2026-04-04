package quiz.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;
import quiz.SceneManager;
import quiz.model.Question;
import quiz.model.QuizSession;

/**
 * Controller for the Quiz screen.
 * Manages question loading, option selection, 60-second timer,
 * progress tracking, and transition to the Result screen.
 */
public class QuizController {

    @FXML
    private Label questionCounter;
    @FXML
    private Label timerLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label questionText;
    @FXML
    private RadioButton optA;
    @FXML
    private RadioButton optB;
    @FXML
    private RadioButton optC;
    @FXML
    private RadioButton optD;
    @FXML
    private Button prevBtn;
    @FXML
    private Button nextBtn;
    @FXML
    private Label warningLabel;

    private ToggleGroup optionGroup;
    private QuizSession session;
    private Timeline timer;
    private int timeLeft;

    /** Called automatically after FXML is loaded */
    @FXML
    public void initialize() {
        session = SceneManager.getCurrentSession();

        // Create a ToggleGroup so only one option can be selected
        optionGroup = new ToggleGroup();
        optA.setToggleGroup(optionGroup);
        optB.setToggleGroup(optionGroup);
        optC.setToggleGroup(optionGroup);
        optD.setToggleGroup(optionGroup);

        // Load the first question
        loadQuestion();
    }

    /** Populate the UI with the current question's data */
    private void loadQuestion() {
        Question q = session.getCurrentQuestion();
        int current = session.getCurrentIndex() + 1; // 1-based
        int total = session.getTotalQuestions();

        // Header: question counter + progress bar
        questionCounter.setText("Q" + current + " / " + total);
        progressBar.setProgress((double) current / total);
        prevBtn.setDisable(current == 1);
        // Question text
        questionText.setText(q.getquesText());

        // Option labels
        String[] opts = q.getOptions();
        optA.setText("A.  " + opts[0]);
        optB.setText("B.  " + opts[1]);
        optC.setText("C.  " + opts[2]);
        optD.setText("D.  " + opts[3]);

        // Clear previous selection
        optionGroup.selectToggle(null);

        // Restore previously selected answer for this question, if any
        int[] userAnswers = session.getUserAnswers();
        int currentIndex = session.getCurrentIndex();
        if (userAnswers != null
                && currentIndex >= 0
                && currentIndex < userAnswers.length) {
            int savedAnswer = userAnswers[currentIndex];
            RadioButton toSelect = null;
            switch (savedAnswer) {
                case 0:
                    toSelect = optA;
                    break;
                case 1:
                    toSelect = optB;
                    break;
                case 2:
                    toSelect = optC;
                    break;
                case 3:
                    toSelect = optD;
                    break;
                default:
                    // No valid saved answer; leave selection cleared
                    break;
            }
            if (toSelect != null) {
                optionGroup.selectToggle(toSelect);
            }
        }
        // Hide warning
        warningLabel.setVisible(false);
        warningLabel.setManaged(false);

        // Change button text on the last question
        if (current == total) {
            nextBtn.setText("Submit");
        } else {
            nextBtn.setText("Next");
        }

        // Restart the countdown timer
        startTimer();
    }

    /** Start (or restart) the 60-second countdown timer */
    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        timeLeft = 60;
        timerLabel.setText("60");
        timerLabel.setStyle(
                "-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeLeft--;
            timerLabel.setText(String.valueOf(timeLeft));

            // Turn red when 10 seconds or less remain
            if (timeLeft <= 10) {
                timerLabel.setStyle(
                        "-fx-text-fill: #ff5252; -fx-font-size: 18; -fx-font-weight: bold;");
            }

            // Auto-advance when time runs out
            if (timeLeft <= 0) {
                timer.stop();
                advanceQuestion();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    @FXML
    private void handlePrevious() {
        if (session.prevQues()) {
            loadQuestion();
        }
    }

    /** Called when user clicks "Next" or "Submit" */
    @FXML
    private void handleNext() {
        // Require an option to be selected
        Toggle selected = optionGroup.getSelectedToggle();
        if (selected == null) {
            warningLabel.setVisible(true);
            warningLabel.setManaged(true);
            return;
        }

        // Map selected RadioButton to option index (0-3)
        int selectedIndex = -1;
        if (selected == optA)
            selectedIndex = 0;
        else if (selected == optB)
            selectedIndex = 1;
        else if (selected == optC)
            selectedIndex = 2;
        else if (selected == optD)
            selectedIndex = 3;

        // Record the answer
        session.submitAns(selectedIndex);

        advanceQuestion();
    }

    /** Move to the next question, or finish the quiz */
    private void advanceQuestion() {
        if (timer != null) {
            timer.stop();
        }

        if (session.nextQues()) {
            // More questions remaining
            loadQuestion();
        } else {
            // Quiz is done — calculate score and show results
            session.finish();
            SceneManager.switchScene("result.fxml");
        }
    }
}
