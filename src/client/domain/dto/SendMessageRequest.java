package domain.dto;

import domain.Message;

public class SendMessageRequest {
    private String userId;
    private String dialogId;
    private Message message;

    public SendMessageRequest() {
    }

    public SendMessageRequest(String userId, String dialogId, Message message) {
        this.userId = userId;
        this.dialogId = dialogId;
        this.message = message;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDialogId() {
        return this.dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public Message getMessage() {
        return this.message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
