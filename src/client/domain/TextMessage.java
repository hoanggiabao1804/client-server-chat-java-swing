package domain;

import java.time.LocalDateTime;

public class TextMessage implements Message {
    private String id;
    private String dialogId;
    private String content;
    private String senderId;
    private String receiverId;
    private LocalDateTime timestamp;
    private String tag;

    public TextMessage() {
    }

    public TextMessage(String id, String dialogId, String content, String senderId, String receiverId,
            LocalDateTime timestamp,
            String tag) {
        this.id = id;
        this.dialogId = dialogId;
        this.content = content;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String getType() {
        return "text";
    }
}
