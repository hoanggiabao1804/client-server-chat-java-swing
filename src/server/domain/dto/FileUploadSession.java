package domain.dto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUploadSession {
    private final FileOutputStream outputStream;
    private long receivedBytes;

    public FileUploadSession(File file) throws IOException {
        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            boolean created = parent.mkdirs();

            if (!created && !parent.exists()) {
                throw new IOException("Cannot create upload directory: " + parent.getPath());
            }
        }

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