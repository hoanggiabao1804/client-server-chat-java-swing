package domain.dto;

public class FileTransferRequest {
    private String dialogId;
    private String messageId;
    private String fileName;
    private long fileSize;
    private int chunkIndex;
    private boolean lastChunk;
    private byte[] data;

    public FileTransferRequest() {
    }

    public FileTransferRequest(String dialogId, String messageId, String fileName, long fileSize, int chunkIndex,
            boolean lastChunk, byte[] data) {
        this.dialogId = dialogId;
        this.messageId = messageId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.chunkIndex = chunkIndex;
        this.lastChunk = lastChunk;
        this.data = data;
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

    public int getChunkIndex() {
        return this.chunkIndex;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public boolean isLastChunk() {
        return this.lastChunk;
    }

    public void setLastChunk(boolean lastChunk) {
        this.lastChunk = lastChunk;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
