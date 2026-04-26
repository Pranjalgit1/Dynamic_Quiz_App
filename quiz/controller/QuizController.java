package quiz.controller;

import java.util.List;

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

public class QuizController {

@FXML private Label questionCounter;
@FXML private Label timerLabel;
@FXML private ProgressBar progressBar;
@FXML private Label questionText;
@FXML private RadioButton optA;
@FXML private RadioButton optB;
@FXML private RadioButton optC;
@FXML private RadioButton optD;
@FXML private Button prevBtn;
@FXML private Button nextBtn;
@FXML private Label warningLabel;

private ToggleGroup optionGroup;
private QuizSession session;
private Timeline timer;
private int timeLeft;

@FXML
public void initialize() {
    session = SceneManager.getCurrentSession();

    optionGroup = new ToggleGroup();
    optA.setToggleGroup(optionGroup);
    optB.setToggleGroup(optionGroup);
    optC.setToggleGroup(optionGroup);
    optD.setToggleGroup(optionGroup);

    loadQuestion();
}

private void loadQuestion() {
    Question q = session.getCurrentQuestion();
    int current = session.getCurrentIndex() + 1;
    int total = session.getTotalQuestions();

    questionCounter.setText("Q" + current + " / " + total);
    progressBar.setProgress((double) current / total);
    prevBtn.setDisable(current == 1);

    questionText.setText(q.getquesText());

    String[] opts = q.getOptions();
    optA.setText("A.  " + opts[0]);
    optB.setText("B.  " + opts[1]);
    optC.setText("C.  " + opts[2]);
    optD.setText("D.  " + opts[3]);

    optionGroup.selectToggle(null);

    
    List<Integer> userAnswers = session.getUserAnswers();
    int currentIndex = session.getCurrentIndex();

    if (userAnswers != null
            && currentIndex >= 0
            && currentIndex < userAnswers.size()) {

        int savedAnswer = userAnswers.get(currentIndex);
        RadioButton toSelect = null;

        switch (savedAnswer) {
            case 0: toSelect = optA; break;
            case 1: toSelect = optB; break;
            case 2: toSelect = optC; break;
            case 3: toSelect = optD; break;
        }

        if (toSelect != null) {
            optionGroup.selectToggle(toSelect);
        }
    }

    warningLabel.setVisible(false);
    warningLabel.setManaged(false);

    nextBtn.setText(current == total ? "Submit" : "Next");

    startTimer();
}

private void startTimer() {
    if (timer != null) {
        timer.stop();
    }

    timeLeft = 60;
    timerLabel.setText("60");
    timerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

    timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
        timeLeft--;
        timerLabel.setText(String.valueOf(timeLeft));

        if (timeLeft <= 10) {
            timerLabel.setStyle("-fx-text-fill: #ff5252; -fx-font-size: 18; -fx-font-weight: bold;");
        }

        if (timeLeft <= 0) {
            timer.stop();

            Toggle selected = optionGroup.getSelectedToggle();
            if (selected != null) {
                int selectedIndex = -1;

                if (selected == optA) selectedIndex = 0;
                else if (selected == optB) selectedIndex = 1;
                else if (selected == optC) selectedIndex = 2;
                else if (selected == optD) selectedIndex = 3;

                session.submitAns(selectedIndex);
            }

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

@FXML
private void handleNext() {
    Toggle selected = optionGroup.getSelectedToggle();

    if (selected == null) {
        session.submitAns(-1);
        advanceQuestion();
        return;
    }

    int selectedIndex = -1;
    if (selected == optA) selectedIndex = 0;
    else if (selected == optB) selectedIndex = 1;
    else if (selected == optC) selectedIndex = 2;
    else if (selected == optD) selectedIndex = 3;

    session.submitAns(selectedIndex);

    advanceQuestion();
}

private void advanceQuestion() {
    if (timer != null) {
        timer.stop();
    }

    if (session.nextQues()) {
        loadQuestion();
    } else {
        session.finish();
        SceneManager.setCurrentSession(session);
        SceneManager.switchScene("result.fxml");
    }
}

}
