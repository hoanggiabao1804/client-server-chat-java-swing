package domain.dto;

import domain.UserMetadata;

public class FetchNewUserResponse {
    private UserMetadata user;
    private String message;
    private String status;

    public FetchNewUserResponse() {
    }

    public FetchNewUserResponse(UserMetadata user, String message, String status) {
        this.user = user;
        this.message = message;
        this.status = status;
    }

    public UserMetadata getUser() {
        return this.user;
    }

    public void setUser(UserMetadata user) {
        this.user = user;
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
