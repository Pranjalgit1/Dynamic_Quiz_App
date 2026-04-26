package quiz.model;

public class User {

    public enum Role { STUDENT, ADMIN } //student and administrator
    private int userId;
    private String username;
    private String password;
    private String email;
    private Role role;

    public User(int uId, String uname, String pass,
                String mail, Role r) {
        userId   = uId;
        username = uname;
        password = pass;
        email    = mail;
        role     = r;
    }

    public boolean isAdmin(){
         return role == Role.ADMIN;
    }

    public int getUserid(){ 
        return userId; 
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public String getEmail(){ 
        return email; 
    }
    public Role getRole(){
        return role;
    }
}
