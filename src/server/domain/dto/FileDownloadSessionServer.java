package domain.dto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileDownloadSessionServer {
    private final FileInputStream inputStream;
    private long sentBytes;

    public FileDownloadSessionServer(File file) throws IOException {
        this.inputStream = new FileInputStream(file);
        this.sentBytes = 0L;
    }

    public void read(int limit) throws IOException {
        byte[] data = inputStream.readNBytes(limit);
        sentBytes += data.length;
    }

    public void close() throws IOException {
        inputStream.close();
    }

    public long getSentBytes() {
        return sentBytes;
    }
}