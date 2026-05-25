package domain.dto;

import domain.Dialog;

public class CreateGroupResponse {
    private Dialog dialog;
    private String message;
    private String status;

    public CreateGroupResponse() {
    }

    public CreateGroupResponse(Dialog dialog, String message, String status) {
        this.dialog = dialog;
        this.message = message;
        this.status = status;
    }

    public Dialog getDialog() {
        return this.dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
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
