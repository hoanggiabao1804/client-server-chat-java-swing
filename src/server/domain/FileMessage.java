package domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FileMessage implements Message {
    private String id;
    private String dialogId;
    private String fileName;
    private long fileSize;
    private String senderId;
    private String receiverId;
    private String filePath;
    private LocalDateTime timestamp;
    private String tag;

    public FileMessage() {
    }

    public FileMessage(String id, String dialogId, String fileName, long fileSize, String senderId, String receiverId,
            String filePath,
            LocalDateTime timestamp, String tag) {
        this.id = id;
        this.dialogId = dialogId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.filePath = filePath;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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
        return fileName;
    }

    @Override
    public String getType() {
        return "file";
    }
}
