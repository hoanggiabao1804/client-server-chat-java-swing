package domain.dto;

import domain.User;

public class AuthResponse {
    private User userLogin;
    private String loginMessage;
    private String status;

    public AuthResponse() {
    }

    public AuthResponse(User userLogin, String loginMessage, String status) {
        this.userLogin = userLogin;
        this.loginMessage = loginMessage;
        this.status = status;
    }

    public User getUserLogin() {
        return this.userLogin;
    }

    public void setUserLogin(User userLogin) {
        this.userLogin = userLogin;
    }

    public String getLoginMessage() {
        return this.loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
