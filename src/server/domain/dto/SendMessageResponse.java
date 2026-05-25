package domain.dto;

import domain.Message;

public class SendMessageResponse {
    private String dialogId;
    private Message messagePersisted;
    private String message;
    private String status;

    public SendMessageResponse() {
    }

    public SendMessageResponse(String dialogId, Message messagePersisted, String message, String status) {
        this.dialogId = dialogId;
        this.messagePersisted = messagePersisted;
        this.message = message;
        this.status = status;
    }

    public String getDialogId() {
        return this.dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public Message getMessagePersisted() {
        return this.messagePersisted;
    }

    public void setMessagePersisted(Message messagePersisted) {
        this.messagePersisted = messagePersisted;
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
