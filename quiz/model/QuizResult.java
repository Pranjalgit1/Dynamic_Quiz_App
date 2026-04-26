package quiz.model;

public class QuizResult {
    private int resultId;
    private int userId;
    private int sessionId;
    private int score;
    private int maxScore;
    private String category;

    public QuizResult(int resId, int uId, int sId,int s, int maxS, String cat) {
        resultId  = resId;
        userId    = uId;
        sessionId = sId;
        score     = s;
        maxScore  = maxS;
        category  = cat;
    }

    public double getPercentage() {
        return (score * 100.0) / maxScore;
    }

    public String getGrade() {
        double p = getPercentage();
        if (p >= 90) return "A+";
        if (p >= 75) return "A";
        if (p >= 60) return "B";
        if (p >= 45) return "C";
        return "F";
    }


    public int getResultId(){
        return resultId; 
    }
    public int getUserId(){
        return userId; 
    }
    public int getSessionId(){
        return sessionId;
    }
    public int getScore(){
        return score;
    }
    public int getMaxScore(){
        return maxScore; 
    }
    public String getCategory(){
        return category; 
    }
}