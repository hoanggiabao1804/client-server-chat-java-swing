package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.SwingUtilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import component.AppFrame;
import component.auth.LoginForm;
import component.menu.Sidebar;
import component.menu.UserDialog;
import domain.Packet;
import domain.User;
import domain.dto.AuthResponse;
import domain.dto.DialogContentResponse;
import domain.dto.FileDownloadAck;
import domain.dto.FileDownloadResponse;
import domain.dto.FileDownloadSessionClient;
import domain.dto.FileTransferResponse;
import domain.dto.SendMessageResponse;
import domain.dto.UserDialogResponse;
import util.LocalStorage;
import util.ObjectMapperFactory;
import util.PacketService;

public class TCPClient {
    private static final ObjectMapper objectMapper = ObjectMapperFactory.create();
    private static final BlockingQueue<Packet> sendQueue = new LinkedBlockingQueue<>();
    private static final Map<String, FileDownloadSessionClient> downloadSessions = new ConcurrentHashMap<>();

    public static void main(String arg[]) {
        objectMapper.disable(SerializationFeature.INDENT_OUTPUT);

        try (Socket socket = new Socket("localhost", 30036)) {
            PacketService.setSocket(socket);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));

            Thread uiThread = new Thread(() -> {
                AppFrame app = AppFrame.getInstance();
                app.run();
            });

            Thread writerThread = new Thread(() -> {
                try {
                    while (true) {
                        Packet packet = sendQueue.take();

                        String json = objectMapper.writeValueAsString(packet);

                        writer.write(json);
                        writer.newLine();
                        writer.flush();

                        if ("quit".equals(packet.getType())) {
                            socket.close();
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Writer stopped");
                }
            });

            Thread readerThread = new Thread(() -> {
                try {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        Packet packet = objectMapper.readValue(line, Packet.class);

                        // System.out.println("Received from server: " + packet.getType());

                        handleServerPacket(packet);
                    }
                } catch (Exception e) {
                    System.out.println("Reader stopped");
                    e.printStackTrace();
                }

                System.out.println("End???");
            });

            uiThread.start();
            writerThread.start();
            readerThread.start();

            uiThread.join();
            writerThread.join();
            readerThread.join();
        } catch (ConnectException ex) {
            System.out.println("Failed to connect to server!");
        } catch (IOException ex) {
            System.out.println("There're some error??");
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            System.out.println("There're some error.");
            ex.printStackTrace();
        } finally {
            for (FileDownloadSessionClient session : downloadSessions.values()) {
                try {
                    session.close();
                } catch (IOException ex) {
                    System.out.println("Failed to close session");
                    ex.printStackTrace();
                }
            }
            downloadSessions.clear();
        }
    }

    public static void handleServerPacket(Packet packet) {

        switch (packet.getCommand()) {
            case "auth":
                LoginForm loginForm = (LoginForm) AppFrame.getInstance().getContextPools().getContext("loginForm");
                AuthResponse authResponse = objectMapper.convertValue(packet.getData(), AuthResponse.class);
                User userLogin = authResponse.getUserLogin();
                // try {
                // System.out.println("Response from server: " +
                // objectMapper.writeValueAsString(authResponse));
                // } catch (Exception ex) {

                // }
                LocalStorage.setUserLogin(userLogin);

                loginForm.getCountDownLatch().countDown();
                // loginForm.getResponse(authResponse);
                SwingUtilities.invokeLater(() -> {
                    loginForm.getResponse(authResponse);
                });

                break;
            case "dialogs/get":
                UserDialogResponse userDialogResponse = objectMapper.convertValue(packet.getData(),
                        UserDialogResponse.class);

                // try {
                // System.out.println("Response from server: " +
                // objectMapper.writeValueAsString(userDialogResponse));
                // } catch (Exception ex) {

                // }

                LocalStorage.setUserDialogs(userDialogResponse.getUserDialogs());

                Sidebar sidebar = (Sidebar) AppFrame.getInstance().getContextPools().getContext("sidebar");
                sidebar.getCountDownLatch().countDown();
                SwingUtilities.invokeLater(() -> {
                    sidebar.getResponse(userDialogResponse);
                });

                break;
            case "dialogs/id":
                DialogContentResponse dialogContentResponse = objectMapper.convertValue(packet.getData(),
                        DialogContentResponse.class);

                try {
                    System.out.println("Response from server: " +
                            objectMapper.writeValueAsString(dialogContentResponse));
                } catch (Exception ex) {

                }

                LocalStorage.setDialogMessageContent(dialogContentResponse.getDialogId(),
                        dialogContentResponse.getMessageList());

                UserDialog userDialog = (UserDialog) AppFrame.getInstance().getContextPools()
                        .getContext(dialogContentResponse.getDialogId());
                userDialog.getCountDownLatch().countDown();
                SwingUtilities.invokeLater(() -> {
                    userDialog.getResponse(dialogContentResponse);
                });
                break;
            case "dialogs/send":
                SendMessageResponse sendMessageResponse = objectMapper.convertValue(packet.getData(),
                        SendMessageResponse.class);

                LocalStorage.addMessage(sendMessageResponse.getDialogId(), sendMessageResponse.getMessagePersisted());

                UserDialog userDialog1 = (UserDialog) AppFrame.getInstance().getContextPools()
                        .getContext(sendMessageResponse.getDialogId());
                userDialog1.getCountDownLatch().countDown();
                SwingUtilities.invokeLater(() -> {
                    userDialog1.getResponse(sendMessageResponse);
                });
                break;

            case "dialogs/upload":
                FileTransferResponse fileTransferResponse = objectMapper.convertValue(packet.getData(),
                        FileTransferResponse.class);

                UserDialog userDialog2 = (UserDialog) AppFrame.getInstance().getContextPools()
                        .getContext(fileTransferResponse.getDialogId());

                userDialog2.getCountDownLatch().countDown();
                SwingUtilities.invokeLater(() -> {
                    userDialog2.getResponse(fileTransferResponse);
                });

                break;
            case "dialogs/download":
                FileDownloadResponse fileDownloadResponse = objectMapper.convertValue(packet.getData(),
                        FileDownloadResponse.class);

                FileDownloadSessionClient fileDownloadSession = downloadSessions
                        .get(fileDownloadResponse.getMessageId());

                try {
                    if (fileDownloadSession == null) {
                        String filePath = fileDownloadResponse.getLocalFilePath();
                        File outputFile = new File(filePath);
                        fileDownloadSession = new FileDownloadSessionClient(outputFile);

                        downloadSessions.put(
                                fileDownloadResponse.getMessageId(),
                                fileDownloadSession);
                    }

                    fileDownloadSession.write(fileDownloadResponse.getData());
                    long receivedBytes = fileDownloadSession.getReceivedBytes();

                    System.out.println("Progress: " + receivedBytes + "/"
                            + fileDownloadResponse.getFileSize() + "bytes");
                    if (fileDownloadResponse.isLastChunk()) {
                        fileDownloadSession.close();
                        downloadSessions.remove(fileDownloadResponse.getMessageId());

                        System.out.println("Download completed");

                        UserDialog userDialog3 = (UserDialog) AppFrame.getInstance().getContextPools()
                                .getContext(fileDownloadResponse.getDialogId());

                        userDialog3.getCountDownLatch().countDown();
                        SwingUtilities.invokeLater(() -> {
                            userDialog3.getResponse(new FileDownloadAck(
                                    fileDownloadResponse.getDialogId(),
                                    fileDownloadResponse.getMessageId(),
                                    fileDownloadResponse.getFileName(),
                                    fileDownloadResponse.getFileSize(),
                                    receivedBytes,
                                    "Chunk downloaded successfully",
                                    "success"));
                        });
                    }

                } catch (IOException ex) {
                    System.out.println("Failed to write file.");

                    ex.printStackTrace();
                }

                break;

            default:
                System.out.println("?");
        }
    }

    public static synchronized void enqueuPacket(Packet packet) {
        sendQueue.add(packet);
    }
}
