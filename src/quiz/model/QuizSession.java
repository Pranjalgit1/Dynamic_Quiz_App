package quiz.model;

import java.util.ArrayList;
import java.util.List;

public class QuizSession {
    private int session_id;
    private int user_id;
    private List<Question> questions; //arraylist of question class 
    private List<Integer> user_ans;  // unanswered questions = -1
    private int current_idx;
    private int score;
    private boolean finished;

    public QuizSession(int sId, int uId, List<Question> ques) {
        session_id   = sId;
        user_id     = uId;
        questions   = ques;
        this.current_idx = 0;
        this.score       = 0; 
        this.finished    = false; 
        this.user_ans = new ArrayList<>();
        //initializing unanswered questions with -1 first
        for (int i = 0; i < questions.size(); i++) 
            user_ans.add(-1);
    }

    public void submitAns(int selected) {
        user_ans.set(current_idx, selected);
    }

    public boolean nextQues() {
        if (current_idx< questions.size() - 1) { 
            current_idx++; 
            return true; 
        }
        return false;
    }

    public void finish() {
        finished = true;
        score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).isCorrect(user_ans.get(i)))
                score += questions.get(i).getMarks();
        }
    }

    public Question getCurrentQuestion() { 
        return questions.get(current_idx); 
    }

    public int getSessionId(){
        return session_id;
     }
    public int getUserId(){
        return user_id;
     }
    public int getScore(){
        return score; 
    }
    public int getCurrentIndex(){ 
        return current_idx; 
    }
    public int getTotalQuestions(){
        return questions.size(); 
    }
    public List<Integer> getUserAnswers(){
        return user_ans; 
    }
    public List<Question> getQuestions(){
        return questions; 
    }
    public boolean isFinished(){
        return finished; 
    }
}
