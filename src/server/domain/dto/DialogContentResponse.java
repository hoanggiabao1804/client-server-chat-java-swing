package domain.dto;

import java.util.List;

import domain.Message;

public class DialogContentResponse {
    private String dialogId;
    private List<Message> messageList;
    private String message;
    private String status;

    public DialogContentResponse() {
    }

    public DialogContentResponse(String dialogId, List<Message> messageList, String message, String status) {
        this.dialogId = dialogId;
        this.messageList = messageList;
        this.message = message;
        this.status = status;
    }

    public String getDialogId() {
        return this.dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public List<Message> getMessageList() {
        return this.messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
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
