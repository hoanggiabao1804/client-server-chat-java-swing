package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

import domain.FileMessage;
import domain.Packet;
import domain.dto.AuthRequest;
import domain.dto.FileDownloadRequest;
import domain.dto.FileTransferRequest;
import domain.dto.SendMessageRequest;
import main.TCPClient;

public class PacketService {
    private static Socket socket;

    public Socket getSocket() {
        return PacketService.socket;
    }

    public static void setSocket(Socket socket) {
        PacketService.socket = socket;
    }

    public static synchronized void authentication(String username, String password) {
        Packet packet = new Packet(
                socket.getInetAddress().getHostAddress(),
                socket.getPort(),
                new AuthRequest(username, password),
                "AuthRequest",
                "auth");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void loadUserDialogs(String userId) {
        Packet packet = new Packet(
                socket.getInetAddress().getHostAddress(),
                socket.getPort(),
                userId,
                "String",
                "dialogs/get");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void loadDialogContent(String dialogId) {
        Packet packet = new Packet(
                socket.getInetAddress().getHostAddress(),
                socket.getPort(),
                dialogId,
                "String",
                "dialogs/id");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void sendMessage(SendMessageRequest sendMessageRequest) {
        Packet packet = new Packet(
                socket.getInetAddress().getHostAddress(),
                socket.getPort(),
                sendMessageRequest,
                "SendMessageRequest",
                "dialogs/send");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void deleteMessage(String messageId) {
        Packet packet = new Packet(
                socket.getInetAddress().getHostAddress(),
                socket.getPort(),
                messageId,
                "String",
                "dialogs/delete");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void sendFile(File file, FileMessage fileMessage) {
        byte[] buffer = new byte[16 * 1024];
        int read = 0;
        int chunkIndex = 0;
        long sent = 0;

        try (InputStream fileIn = new FileInputStream(file)) {
            while ((read = fileIn.read(buffer)) != -1) {
                sent += read;

                boolean lastChunk = sent >= file.length();

                byte[] chunkData = Arrays.copyOf(buffer, read);

                FileTransferRequest fileTransferRequest = new FileTransferRequest(fileMessage.getDialogId(),
                        fileMessage.getId(), file.getName(), file.length(), chunkIndex++, lastChunk, chunkData);

                Packet packet = new Packet(
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort(),
                        fileTransferRequest,
                        "FileTransferRequest",
                        "dialogs/upload");

                TCPClient.enqueuPacket(packet);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed to process file.");
            e.printStackTrace();
        }
    }

    public static synchronized void downloadFile(FileDownloadRequest fileDownloadRequest) {
        Packet packet = new Packet(
                socket.getInetAddress().getHostAddress(),
                socket.getPort(),
                fileDownloadRequest,
                "FileDownloadRequest",
                "dialogs/download");

        TCPClient.enqueuPacket(packet);
    }
}