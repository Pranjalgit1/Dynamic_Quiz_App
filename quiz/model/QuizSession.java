package quiz.model;

import java.util.ArrayList;
import java.util.List;

public class QuizSession {
    private int session_id;
    private int user_id;
    private List<Question> questions; // arraylist of question class
    private List<Integer> user_ans; // unanswered questions = -1
    private int current_idx;
    private int score;
    private boolean finished;

    public int getMaxScore() {
        int max = 0;
        for (Question q : questions)
            max += q.getMarks();
        return max;
    }

    public QuizSession(int sId, int uId, List<Question> ques) {
        session_id = sId;
        user_id = uId;
        questions = ques;
        this.current_idx = 0;
        this.score = 0;
        this.finished = false;
        this.user_ans = new ArrayList<>();
        // initializing unanswered questions with -1 first
        for (int i = 0; i < questions.size(); i++)
            user_ans.add(-1);
    }

    public void submitAns(int selected) {
        user_ans.set(current_idx, selected);
    }

    public boolean prevQues() {
        if (current_idx > 0) {
            current_idx--;
            return true;
        }
        return false;
    }

    public boolean nextQues() {
        if (current_idx < questions.size() - 1) {
            current_idx++;
            return true;
        }
        return false;
    }

    public void finish() {
        finished = true;
        score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int ansIdx = user_ans.get(i);
            boolean isCorrect = q.isCorrect(ansIdx);
            
            if (isCorrect) {
                score += q.getMarks();
            }
            
            String selectedText = ansIdx >= 0 && ansIdx <= 3 ? q.getOptions()[ansIdx] : "Unanswered";
            String correctText = q.getOptions()[q.getcorrectOptionidx()];
            
            quiz.manager.DatabaseManager.saveAnswer(session_id, q.getquesText(), selectedText, correctText, isCorrect);
        }
        quiz.manager.DatabaseManager.updateSessionScore(session_id, score);
    }

    public Question getCurrentQuestion() {
        return questions.get(current_idx);
    }

    public int getSessionId() {
        return session_id;
    }

    public int getUserId() {
        return user_id;
    }

    public int getScore() {
        return score;
    }

    public int getCurrentIndex() {
        return current_idx;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public List<Integer> getUserAnswers() {
        return user_ans;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public boolean isFinished() {
        return finished;
    }
}
