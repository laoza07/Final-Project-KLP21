package areda.model;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private String email, password, role;
    private boolean isAccepted;

    public UserAccount(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.isAccepted = false;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }
}