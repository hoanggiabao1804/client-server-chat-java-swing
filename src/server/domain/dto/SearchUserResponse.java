package domain.dto;

import java.util.List;

import domain.UserMetadata;

public class SearchUserResponse {
    private String requesterId;
    private List<UserMetadata> foundUsers;
    private String message;
    private String status;

    public SearchUserResponse() {
    }

    public SearchUserResponse(String requesterId, List<UserMetadata> foundUsers, String message, String status) {
        this.requesterId = requesterId;
        this.foundUsers = foundUsers;
        this.message = message;
        this.status = status;
    }

    public String getRequesterId() {
        return this.requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public List<UserMetadata> getFoundUsers() {
        return this.foundUsers;
    }

    public void setFoundUsers(List<UserMetadata> foundUsers) {
        this.foundUsers = foundUsers;
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
