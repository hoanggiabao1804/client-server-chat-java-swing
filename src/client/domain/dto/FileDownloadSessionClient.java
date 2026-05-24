package domain.dto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDownloadSessionClient {
    private final FileOutputStream outputStream;
    private long receivedBytes;

    public FileDownloadSessionClient(File file) throws IOException {
        this.outputStream = new FileOutputStream(file, false);
        this.receivedBytes = 0L;
    }

    public void write(byte[] data) throws IOException {
        outputStream.write(data);
        receivedBytes += data.length;
    }

    public void close() throws IOException {
        outputStream.flush();
        outputStream.close();
    }

    public long getReceivedBytes() {
        return receivedBytes;
    }
}