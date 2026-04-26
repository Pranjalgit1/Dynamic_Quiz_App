package quiz.model;

public class Question {
    private int ques_id;
    private String ques_text;
    private String[] options;       
    private int correct_option_idx;  // mcq indexes : 0=A, 1=B, 2=C, 3=D
    private String diff;
    private String category;
    private String explanation;

    public Question(int Id, String text, String[] opt,int corroptidx , String d, String cat, String exp) {
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
        if ("easy".equalsIgnoreCase(diff)) return 1;
        if ("medium".equalsIgnoreCase(diff)) return 2;
        if ("hard".equalsIgnoreCase(diff)) return 3;
        return 1;
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
    public String getDifficulty(){
        return diff; 
    }
    public String getCategory(){
        return category; 
    }
    public String getExplanation(){
        return explanation; 
    }
}
