package domain.dto;

import java.util.List;

import domain.Dialog;

public class UserDialogResponse {
    private List<Dialog> userDialogs;
    private String message;
    private String status;

    public UserDialogResponse() {
    }

    public UserDialogResponse(List<Dialog> userDialogs, String message, String status) {
        this.userDialogs = userDialogs;
        this.message = message;
        this.status = status;
    }

    public List<Dialog> getUserDialogs() {
        return this.userDialogs;
    }

    public void setUserDialogs(List<Dialog> userDialogs) {
        this.userDialogs = userDialogs;
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
