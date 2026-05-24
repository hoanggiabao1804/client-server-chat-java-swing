package domain.dto;

import domain.Message;

public class UpdateDialogResponse {
    private String dialogId;
    private Message newMessage;
    private String message;
    private String status;

    public UpdateDialogResponse() {
    }

    public UpdateDialogResponse(String dialogId, Message newMessage, String message, String status) {
        this.dialogId = dialogId;
        this.newMessage = newMessage;
        this.message = message;
        this.status = status;
    }

    public String getDialogId() {
        return this.dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public Message getNewMessage() {
        return this.newMessage;
    }

    public void setMessageList(Message newMessage) {
        this.newMessage = newMessage;
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
