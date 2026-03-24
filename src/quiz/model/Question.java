package quiz.model;

public class Question {
    public enum Difficulty { EASY, MEDIUM, HARD } //level
    public enum Category { OOP, COLLECTIONS, MULTITHREADING, EXCEPTION_HANDLING , FILE_IO , JDBC , JAVA_BASICS  }//concepts covered by a question

    private int ques_id;
    private String ques_text;
    private String[] options;       
    private int correct_option_idx;  // mcq indexes : 0=A, 1=B, 2=C, 3=D
    private Difficulty diff;
    private Category category;
    private String explanation;

    public Question(int Id, String text, String[] opt,int corroptidx , Difficulty d,Category cat, String exp) {
        ques_id = Id;
        ques_text = text;
        options = opt;
        correct_option_idx = corroptidx;
        diff = d;
        category = cat;
        explanation = exp;
    }

    public boolean isCorrect(int selected) {
        if(selected == correct_option_idx) 
            return true;
        return false;
    }

    public int getMarks() {
        switch (diff) {
            case EASY:   return 1;
            case MEDIUM: return 2;
            case HARD:   return 3;
            default:     return 1;
        }
    }


    public int getquesId(){
        return ques_id; 
    }
    public String getquesText(){
        return ques_text; 
    }
    public String[] getOptions(){ 
        return options; 
    }
    public int getcorrectOptionidx(){
        return correct_option_idx; 
    }
    public Difficulty getDifficulty(){
        return diff; 
    }
    public Category getCategory(){
        return category; 
    }
    public String getExplanation(){
        return explanation; 
    }
}
