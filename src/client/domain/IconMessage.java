package domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class IconMessage implements Message {
    private String id;
    private String dialogId;
    private String iconName;
    private String senderId;
    private String receiverId;
    private String iconPath;
    private LocalDateTime timestamp;
    private String tag;

    public IconMessage() {
    }

    public IconMessage(String id, String dialogId, String iconName, String senderId, String receiverId, String iconPath,
            LocalDateTime timestamp, String tag) {
        this.id = id;
        this.dialogId = dialogId;
        this.iconName = iconName;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.iconPath = iconPath;
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

    public void setIconName(String iconName) {
        this.iconName = iconName;
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

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
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

    @JsonIgnore
    @Override
    public String getContent() {
        return iconName;
    }

    @Override
    public String getType() {
        return "icon";
    }
}
