package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import domain.Dialog;
import domain.FileMessage;
import domain.Message;
import domain.Packet;
import domain.User;
import domain.UserMetadata;
import domain.dto.AuthRequest;
import domain.dto.AuthResponse;
import domain.dto.CreateGroupRequest;
import domain.dto.CreateGroupResponse;
import domain.dto.DeleteMessageRequest;
import domain.dto.DeleteMessageResponse;
import domain.dto.DialogContentResponse;
import domain.dto.FetchNewUserResponse;
import domain.dto.FileDownloadRequest;
import domain.dto.FileDownloadResponse;
import domain.dto.FileTransferRequest;
import domain.dto.FileTransferResponse;
import domain.dto.FileUploadSession;
import domain.dto.NetworkConfig;
import domain.dto.RegisterResponse;
import domain.dto.SearchUserRequest;
import domain.dto.SearchUserResponse;
import domain.dto.SendMessageRequest;
import domain.dto.SendMessageResponse;
import domain.dto.UserDialogResponse;
import domain.dto.UserUpdateResponse;
import repository.DialogRepository;
import repository.MessageRepository;
import repository.RepositoryManager;
import repository.UserRepository;
import util.Mapper;
import util.ObjectMapperFactory;

public class TCPServer {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.create();
    private static final Map<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();

    public static void main(String arg[]) {
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);

        RepositoryManager.getInstance();
        ExecutorService pool = Executors.newCachedThreadPool();
        NetworkConfig networkConfig = loadConfig();

        try (ServerSocket serverSocket = new ServerSocket(networkConfig.getPort())) {
            do {
                Socket socket = serverSocket.accept();

                System.out.println("Talking to client with port: " + socket.getPort());

                ClientHandler handler = new ClientHandler(socket);

                pool.execute(handler);
            } while (true);
        } catch (Exception ex) {
            System.out.println("Errors happened!");
            ex.printStackTrace();
        }
    }

    public static boolean isOnline(String userId) {
        return onlineUsers.containsKey(userId);
    }

    public static void putToClientPool(String userId, ClientHandler handler) {
        onlineUsers.put(userId, handler);
    }

    public static ClientHandler getClientHandler(String userId) {
        return onlineUsers.getOrDefault(userId, null);
    }

    public static void broadcast(Packet packet, String senderId) {
        for (String userId : onlineUsers.keySet()) {
            ClientHandler client = onlineUsers.get(userId);
            if (!userId.equals(senderId)) {
                try {
                    String message = objectMapper.writeValueAsString(packet);
                    client.send(message);
                } catch (IOException ex) {
                    onlineUsers.remove(userId, client);
                }
            }
        }
    }

    public static void multicast(Packet packet, List<String> userIds) {
        userIds.forEach(item -> {
            ClientHandler client = onlineUsers.getOrDefault(item, null);
            if (client != null) {
                try {
                    String message = objectMapper.writeValueAsString(packet);
                    client.send(message);
                } catch (IOException ex) {
                    onlineUsers.remove(item, client);
                    ex.printStackTrace();
                }
            }
        });
    }

    private static NetworkConfig loadConfig() {
        try {
            File file = new File("server-config/config.json");

            NetworkConfig config = objectMapper.readValue(file, NetworkConfig.class);

            return config;
        } catch (FileNotFoundException ex) {
            System.out.println(">>> ERROR: File name server-config/config.json not found.");
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println(">>> ERROR: Failed to load server config.");
            ex.printStackTrace();
        }

        return new NetworkConfig("localhost", 30036);
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    private static final ObjectMapper objectMapper = ObjectMapperFactory.create();
    private final Map<String, FileUploadSession> uploadSessions = new ConcurrentHashMap<>();
    // private final Map<String, FileDownloadSessionServer> downloadSessions = new
    // ConcurrentHashMap<>();

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void run() {
        try {
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println("Received: " + line);
                Packet packet = objectMapper.readValue(line, Packet.class);

                System.out.println("Packet type: " + packet.getType());

                handlePacket(packet);
            }
        } catch (IOException ex) {
            System.out.println("Client disconnected");
        } finally {
            close();
            for (FileUploadSession session : uploadSessions.values()) {
                try {
                    session.close();
                } catch (IOException ex) {
                    System.out.println("Failed to close session");
                    ex.printStackTrace();
                }
            }
            uploadSessions.clear();
        }
    }

    private void handlePacket(Packet packet) {
        Packet sentPacket = null;

        switch (packet.getCommand()) {
            case "auth":
                AuthRequest authRequest = objectMapper.convertValue(packet.getData(), AuthRequest.class);
                User foundUser = UserRepository.getInstance().findByUsername(authRequest.getUsername());
                String result = "";

                if (foundUser == null) {
                    result = "Không tìm thấy người dùng.";
                } else if (!foundUser.getPassword().equals(authRequest.getPassword())) {
                    result = "Sai tài khoản hoặc mật khẩu.";
                }

                if (!result.isEmpty()) {
                    System.out.println("Authentication fail with error: " + result);
                    sentPacket = new Packet(
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort(),
                            new AuthResponse(null, result, "failed"),
                            "AuthResponse",
                            "auth");

                } else {
                    TCPServer.putToClientPool(foundUser.getId(), this);
                    System.out.println("Successful to authenticate user.");
                    sentPacket = new Packet(
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort(),
                            new AuthResponse(foundUser, "Authenticate successful.", "success"),
                            "AuthResponse",
                            "auth");
                }
                break;
            case "registry":
                User user = objectMapper.convertValue(packet.getData(), User.class);

                System.out.println("Registering user with username: " + user.getUsername());
                if (UserRepository.getInstance().findByUsername(user.getUsername()) != null) {
                    System.out.println("Registration fail with error: Username is already taken.");
                    sentPacket = new Packet(
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort(),
                            new RegisterResponse(null, "Tên đăng nhập đã tồn tại.", "failed"),
                            "RegisterResponse",
                            "registry");
                } else {
                    user.setId(UUID.randomUUID().toString());
                    User savedUser = UserRepository.getInstance().save(user);
                    DialogRepository.getInstance().save(new Dialog(
                            UUID.randomUUID().toString(),
                            savedUser.getName(),
                            Arrays.asList(savedUser.getId()),
                            new ArrayList<>(),
                            "private",
                            savedUser.getId()));

                    RepositoryManager.exportUsers();
                    RepositoryManager.exportDialogs();

                    System.out.println("Successful to register user.");
                    sentPacket = new Packet(
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort(),
                            new RegisterResponse(savedUser.getId(), "Đăng ký thành công.", "success"),
                            "RegisterResponse",
                            "registry");

                    Packet newUserNotificationPacket = new Packet(
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort(),
                            new FetchNewUserResponse(Mapper.userToUserMetadata(savedUser),
                                    "Người dùng mới đã được tạo.", "success"),
                            "FetchNewUserResponse",
                            "users/fetch-new");

                    TCPServer.broadcast(newUserNotificationPacket, null);
                }

                break;
            case "dialogs/get":
                String userId = (String) packet.getData();

                List<Dialog> userDialogs = DialogRepository.getInstance().findByUserId(userId);

                if (userDialogs == null) {
                    System.out.println("Fetch user dialogs failed!");
                    sentPacket = new Packet(
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort(),
                            new UserDialogResponse(userDialogs, "Failed to fetch dialog list of user '" + userId + "'.",
                                    "failed"),
                            "UserDialogResponse",
                            "dialogs/get");
                } else {
                    System.out.println("UserDialogs size: " + userDialogs.size());
                    sentPacket = new Packet(
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort(),
                            new UserDialogResponse(userDialogs, "Fetch user '" + userId + "' dialogs successful.",
                                    "success"),
                            "UserDialogResponse",
                            "dialogs/get");
                }

                break;
            case "dialogs/id":
                String dialogId = (String) packet.getData();

                List<Message> messages = MessageRepository.getInstance().findByDialogId(dialogId);
                System.out.println("Dialog messages size: " + messages.size());
                sentPacket = new Packet(
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort(),
                        new DialogContentResponse(dialogId, messages,
                                "Fetch message list for dialog '" + dialogId + "' successful.", "success"),
                        "DialogContentResponse", "dialogs/id");
                break;
            case "dialogs/send":
                SendMessageRequest sendMessageRequest = objectMapper.convertValue(packet.getData(),
                        SendMessageRequest.class);

                Message message = sendMessageRequest.getMessage();

                if (message instanceof FileMessage) {
                    ((FileMessage) message).setFilePath("resource/buckets/" + message.getDialogId() + "/"
                            + message.getId() + "-" + message.getContent());
                }

                Message messagePersisted = MessageRepository.getInstance().save(message);

                System.out.println("Message content: " + messagePersisted.getContent());

                sentPacket = new Packet(
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort(),
                        new SendMessageResponse(messagePersisted.getDialogId(), messagePersisted,
                                "Message is sent successful.", "success"),
                        "SendMessageResponse",
                        "dialogs/send");

                if (!(message instanceof FileMessage)) {
                    RepositoryManager.exportMessages(message.getDialogId());
                }

                Dialog dialog = DialogRepository.getInstance().findById(message.getDialogId());

                TCPServer.multicast(sentPacket, dialog.getParticipants());
                return;
            case "dialogs/delete":
                DeleteMessageRequest deleteMessageRequest = objectMapper.convertValue(packet.getData(),
                        DeleteMessageRequest.class);
                Message message1 = deleteMessageRequest.getMessage();

                if (message1 instanceof FileMessage) {
                    String filePath = "resource/buckets/" + message1.getDialogId() + "/"
                            + message1.getId() + "-" + message1.getContent();

                    File fileToRemove = new File(filePath);

                    if (fileToRemove.exists()) {
                        if (fileToRemove.delete()) {
                            System.out.println(">>> Deleted file '" + fileToRemove.getPath() + "'.");
                        } else {
                            System.out.println(">>> ERROR: Failed to delete file '" +
                                    fileToRemove.getPath() + "'.");
                            sentPacket = new Packet(
                                    socket.getInetAddress().getHostAddress(),
                                    socket.getPort(),
                                    new DeleteMessageResponse(message1.getDialogId(), message1,
                                            "Failed to delete message. File not found.", "failed"),
                                    "DeleteMessageResponse",
                                    "dialogs/delete");
                        }
                    }
                }

                MessageRepository.getInstance().deleteById(message1.getId());

                RepositoryManager.exportMessages(message1.getDialogId());

                sentPacket = new Packet(
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort(),
                        new DeleteMessageResponse(message1.getDialogId(), message1,
                                "Message is deleted successful.", "success"),
                        "DeleteMessageResponse",
                        "dialogs/delete");

                Dialog dialog1 = DialogRepository.getInstance().findById(message1.getDialogId());

                TCPServer.multicast(sentPacket, dialog1.getParticipants());
                return;
            case "dialogs/upload":
                FileTransferRequest fileTransferRequest = objectMapper.convertValue(packet.getData(),
                        FileTransferRequest.class);

                FileUploadSession fileUploadSession = uploadSessions.get(fileTransferRequest.getMessageId());

                try {
                    if (fileUploadSession == null) {
                        String filePath = "resource/buckets/" + fileTransferRequest.getDialogId() + "/"
                                + fileTransferRequest.getMessageId() + "-" + fileTransferRequest.getFileName();
                        File outputFile = new File(filePath);
                        fileUploadSession = new FileUploadSession(outputFile);

                        uploadSessions.put(
                                fileTransferRequest.getMessageId(),
                                fileUploadSession);
                    }

                    fileUploadSession.write(fileTransferRequest.getData());

                    if (fileTransferRequest.isLastChunk()) {
                        fileUploadSession.close();
                        uploadSessions.remove(fileTransferRequest.getMessageId());

                        RepositoryManager.exportMessages(fileTransferRequest.getDialogId());
                        System.out.println("Upload completed");
                    }

                    sentPacket = new Packet(
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort(),
                            new FileTransferResponse(fileTransferRequest.getDialogId(),
                                    fileTransferRequest.getMessageId(), fileTransferRequest.getFileName(),
                                    fileTransferRequest.getFileSize(), fileUploadSession.getReceivedBytes(),
                                    "Chunk is persisted.", "success"),
                            "FileTransferResponse",
                            "dialogs/upload");

                } catch (IOException ex) {
                    System.out.println("Failed to write file.");
                    long receivedBytes = fileUploadSession != null
                            ? fileUploadSession.getReceivedBytes()
                            : 0L;

                    if (fileUploadSession != null) {
                        try {
                            fileUploadSession.close();
                        } catch (IOException ignored) {
                        }

                        uploadSessions.remove(fileTransferRequest.getMessageId());
                    }

                    sentPacket = new Packet(
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort(),
                            new FileTransferResponse(
                                    fileTransferRequest.getDialogId(),
                                    fileTransferRequest.getMessageId(),
                                    fileTransferRequest.getFileName(),
                                    fileTransferRequest.getFileSize(),
                                    receivedBytes,
                                    "Chunk is corrupted: " + ex.getMessage(),
                                    "failed"),
                            "FileTransferResponse",
                            "dialogs/upload");

                    ex.printStackTrace();
                }

                break;
            case "dialogs/download":
                FileDownloadRequest fileDownloadRequest = objectMapper.convertValue(packet.getData(),
                        FileDownloadRequest.class);

                new Thread(() -> {
                    String filePath = "resource/buckets/" + fileDownloadRequest.getDialogId() + "/"
                            + fileDownloadRequest.getMessageId() + "-" + fileDownloadRequest.getFileName();
                    File file = new File(filePath);

                    byte[] buffer = new byte[16 * 1024];

                    try (InputStream fileIn = new FileInputStream(file)) {
                        int read = 0;
                        int chunkIndex = 0;
                        long sent = 0;

                        while ((read = fileIn.read(buffer)) != -1) {
                            sent += read;

                            boolean lastChunk = sent >= file.length();

                            byte[] chunkData = Arrays.copyOf(buffer, read);

                            FileDownloadResponse fileDownloadResponse = new FileDownloadResponse(
                                    fileDownloadRequest.getDialogId(), fileDownloadRequest.getMessageId(),
                                    fileDownloadRequest.getFileName(), fileDownloadRequest.getLocalFilePath(),
                                    file.length(), chunkIndex, lastChunk, chunkData);

                            Packet chunkPacket = new Packet(
                                    socket.getInetAddress().getHostAddress(),
                                    socket.getPort(),
                                    fileDownloadResponse,
                                    "FileDownloadResponse",
                                    "dialogs/download");

                            System.out.println("Sent: " + sent + "/" + file.length() + "bytes");

                            send(objectMapper.writeValueAsString(chunkPacket));
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("File not found!");
                        e.printStackTrace();
                    } catch (IOException e) {
                        System.out.println("Failed to process file.");
                        e.printStackTrace();
                    }
                }).start();
                return;
            case "dialogs/group/create":
                CreateGroupRequest createGroupRequest = objectMapper.convertValue(packet.getData(),
                        CreateGroupRequest.class);

                List<String> participants = new ArrayList<>(createGroupRequest.getParticipantIds());

                if (!participants.contains(createGroupRequest.getCreatorId())) {
                    participants.add(createGroupRequest.getCreatorId());
                }

                Dialog group = new Dialog(
                        UUID.randomUUID().toString(),
                        createGroupRequest.getGroupName(),
                        participants,
                        new ArrayList<>(),
                        createGroupRequest.getType(),
                        createGroupRequest.getCreatorId());

                Dialog saved = DialogRepository.getInstance().save(group);

                File bucketDir = new File("resource/buckets/" + saved.getId());

                if (!bucketDir.exists()) {
                    bucketDir.mkdirs();
                }

                RepositoryManager.exportDialogs();

                sentPacket = new Packet(
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort(),
                        new CreateGroupResponse(saved, "Tạo nhóm thành công.", "success"),
                        "CreateGroupResponse",
                        "dialogs/group/create");

                TCPServer.multicast(sentPacket, participants);
                return;
            case "users/fetch":
            case "users/search":
                SearchUserRequest searchUserRequest = objectMapper.convertValue(packet.getData(),
                        SearchUserRequest.class);

                List<User> foundUsers = null;
                String keyword = searchUserRequest.getKeyword();

                if (packet.getCommand().equals("users/fetch")) {
                    foundUsers = UserRepository.getInstance().findAll();
                } else {
                    foundUsers = UserRepository.getInstance().findAll().stream()
                            .filter(item -> item.getName().toLowerCase().contains(keyword))
                            .collect(Collectors.toList());
                }

                if (!searchUserRequest.getStatus().equals("all")) {
                    boolean isOnline = searchUserRequest.getStatus().equals("online");
                    foundUsers = foundUsers.stream().filter(item -> {
                        String id = item.getId();
                        return (isOnline) ? TCPServer.isOnline(id) : !TCPServer.isOnline(id);
                    }).collect(Collectors.toList());
                }

                List<UserMetadata> userMetadatas1 = foundUsers.stream().map(Mapper::userToUserMetadata)
                        .collect(Collectors.toList());

                sentPacket = new Packet(
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort(),
                        new SearchUserResponse(searchUserRequest.getRequesterId(), userMetadatas1,
                                "Searched users successfull.", "success"),
                        "SearchUserResponse",
                        packet.getCommand());

                break;
            case "users/update-info":
                User updatedUser = objectMapper.convertValue(packet.getData(), User.class);
                User oldUser = UserRepository.getInstance().findById(updatedUser.getId());

                if (oldUser == null) {
                    sentPacket = new Packet(
                            socket.getInetAddress().getHostAddress(),
                            socket.getPort(),
                            new UserUpdateResponse(null, null, "Invalid user.", "failed"),
                            "UserUpdateResponse",
                            "users/update-info");

                    return;
                }

                UserMetadata newUserMetadata = Mapper.userToUserMetadata(updatedUser);
                UserMetadata oldUserMetadata = Mapper.userToUserMetadata(oldUser);

                sentPacket = new Packet(
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort(),
                        new UserUpdateResponse(null, newUserMetadata, "User metadata updated.", "success"),
                        "UserUpdateResponse",
                        "users/fetch-update");

                if (!(newUserMetadata.getName().equals(oldUserMetadata.getName())
                        && newUserMetadata.getEmail().equals(oldUserMetadata.getEmail())
                        && newUserMetadata.getDob().isEqual(oldUserMetadata.getDob())
                        && newUserMetadata.getGender().equals(oldUserMetadata.getGender()))) {
                    TCPServer.broadcast(sentPacket, updatedUser.getId());
                }

                UserRepository.getInstance().save(updatedUser);

                sentPacket = new Packet(
                        socket.getInetAddress().getHostAddress(),
                        socket.getPort(),
                        new UserUpdateResponse(updatedUser, newUserMetadata, "User updated.", "success"),
                        "UserUpdateResponse",
                        "users/update-info");

                RepositoryManager.exportUsers();

                break;
            default:
                System.out.println("?");

        }

        try {
            send(objectMapper.writeValueAsString(sentPacket));
        } catch (Exception ex) {
            System.out.println("Failed to send packet!");
            ex.printStackTrace();
        }
    }

    public synchronized void send(String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }

    private void close() {
        try {
            socket.close();
            System.out.println("Client with port: " + socket.getPort() + " has disconnected.");
        } catch (IOException ex) {
            System.out.println("Failed to close the client connection.");
        }
    }
}