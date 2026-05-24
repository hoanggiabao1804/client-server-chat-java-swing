package domain.dto;

public class FileDownloadAck {
    private String dialogId;
    private String messageId;
    private String fileName;
    private long fileSize;
    private long receivedBytes;
    private String message;
    private String status;

    public FileDownloadAck() {
    }

    public FileDownloadAck(String dialogId, String messageId, String fileName, long fileSize, long receivedBytes,
            String message, String status) {
        this.dialogId = dialogId;
        this.messageId = messageId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.receivedBytes = receivedBytes;
        this.message = message;
        this.status = status;
    }

    public String getDialogId() {
        return this.dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFileName() {
        return this.fileName;
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

    public long getSentBytes() {
        return this.receivedBytes;
    }

    public void setSentBytes(long receivedBytes) {
        this.receivedBytes = receivedBytes;
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
