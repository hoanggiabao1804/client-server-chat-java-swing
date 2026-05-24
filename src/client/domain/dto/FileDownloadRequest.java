package domain.dto;

public class FileDownloadRequest {
    private String dialogId;
    private String messageId;
    private String fileName;
    private String localFilePath;

    public FileDownloadRequest() {
    }

    public FileDownloadRequest(String dialogId, String messageId, String fileName, String localFilePath) {
        this.dialogId = dialogId;
        this.messageId = messageId;
        this.fileName = fileName;
        this.localFilePath = localFilePath;
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

    public String getLocalFilePath() {
        return this.localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }
}
