package domain.dto;

public class RegisterResponse {
    private String userId;
    private String message;
    private String status;

    public RegisterResponse() {
    }

    public RegisterResponse(String userId, String message, String status) {
        this.userId = userId;
        this.message = message;
        this.status = status;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
