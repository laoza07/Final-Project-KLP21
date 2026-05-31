package areda.model;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private UserRole role;
    private boolean isAccepted;

    public UserAccount(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.isAccepted = false;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}