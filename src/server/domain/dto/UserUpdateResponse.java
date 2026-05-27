package domain.dto;

import domain.User;
import domain.UserMetadata;

public class UserUpdateResponse {
    private User user;
    private UserMetadata userMetadata;
    private String message;
    private String status;

    public UserUpdateResponse() {
    }

    public UserUpdateResponse(User user, UserMetadata userMetadata, String message, String status) {
        this.user = user;
        this.userMetadata = userMetadata;
        this.message = message;
        this.status = status;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserMetadata getUserMetadata() {
        return this.userMetadata;
    }

    public void setUserMetadata(UserMetadata userMetadata) {
        this.userMetadata = userMetadata;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
