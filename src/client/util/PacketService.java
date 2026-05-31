package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import domain.FileMessage;
import domain.Packet;
import domain.User;
import domain.dto.AuthRequest;
import domain.dto.CreateGroupRequest;
import domain.dto.DeleteMessageRequest;
import domain.dto.FileDownloadRequest;
import domain.dto.FileTransferRequest;
import domain.dto.NetworkConfig;
import domain.dto.SearchUserRequest;
import domain.dto.SendMessageRequest;
import main.TCPClient;

public class PacketService {
    private static NetworkConfig networkConfig;

    public NetworkConfig getNetworkConfig() {
        return PacketService.networkConfig;
    }

    public static void setNetWorkConfig(NetworkConfig networkConfig) {
        PacketService.networkConfig = networkConfig;
    }

    public static synchronized void authentication(String username, String password) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                new AuthRequest(username, password),
                "AuthRequest",
                "auth");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void registration(User user) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                user,
                "User",
                "registry");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void loadUserDialogs(String userId) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                userId,
                "String",
                "dialogs/get");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void loadDialogContent(String dialogId) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                dialogId,
                "String",
                "dialogs/id");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void sendMessage(SendMessageRequest sendMessageRequest) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                sendMessageRequest,
                "SendMessageRequest",
                "dialogs/send");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void deleteMessage(DeleteMessageRequest deleteMessageRequest) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                deleteMessageRequest,
                "DeleteMessageRequest",
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
                        networkConfig.getIpAddress(),
                        networkConfig.getPort(),
                        fileTransferRequest,
                        "FileTransferRequest",
                        "dialogs/upload");

                TCPClient.enqueuPacket(packet);
            }
        } catch (FileNotFoundException e) {
            System.out.println(">>> ERROR: File not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(">>> ERROR: Failed to process file.");
            e.printStackTrace();
        }
    }

    public static synchronized void downloadFile(FileDownloadRequest fileDownloadRequest) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                fileDownloadRequest,
                "FileDownloadRequest",
                "dialogs/download");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void createGroup(CreateGroupRequest createGroupRequest) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                createGroupRequest,
                "CreateGroupRequest",
                "dialogs/group/create");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void fetchUsers(SearchUserRequest searchUserRequest) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                searchUserRequest,
                "SearchUserRequest",
                "users/fetch");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void searchUsers(SearchUserRequest searchUserRequest) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                searchUserRequest,
                "SearchUserRequest",
                "users/search");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void updateUserInformation(User user) {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                user,
                "User",
                "users/update-info");

        TCPClient.enqueuPacket(packet);
    }

    public static synchronized void closeConnection() {
        Packet packet = new Packet(
                networkConfig.getIpAddress(),
                networkConfig.getPort(),
                LocalStorage.getUserLogin().getId(),
                "String",
                "connection/close");

        TCPClient.enqueuPacket(packet);
    }
}