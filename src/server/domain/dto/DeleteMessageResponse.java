package domain.dto;

import domain.Message;

public class DeleteMessageResponse {
    private String dialogId;
    private Message messageDeleted;
    private String message;
    private String status;

    public DeleteMessageResponse() {
    }

    public DeleteMessageResponse(String dialogId, Message messageDeleted, String message, String status) {
        this.dialogId = dialogId;
        this.messageDeleted = messageDeleted;
        this.message = message;
        this.status = status;
    }

    public String getDialogId() {
        return this.dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public Message getMessageDeleted() {
        return this.messageDeleted;
    }

    public void setMessageDeleted(Message messageDeleted) {
        this.messageDeleted = messageDeleted;
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
