package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
import component.auth.RegisterForm;
import component.menu.Sidebar;
import component.menu.UserDialog;
import domain.Packet;
import domain.dto.AuthResponse;
import domain.dto.CreateGroupResponse;
import domain.dto.DeleteMessageResponse;
import domain.dto.DialogContentResponse;
import domain.dto.FetchNewUserResponse;
import domain.dto.FileDownloadAck;
import domain.dto.FileDownloadResponse;
import domain.dto.FileDownloadSessionClient;
import domain.dto.FileTransferResponse;
import domain.dto.NetworkConfig;
import domain.dto.RegisterResponse;
import domain.dto.SearchUserResponse;
import domain.dto.SendMessageResponse;
import domain.dto.UserDialogResponse;
import domain.dto.UserUpdateResponse;
import util.LocalStorage;
import util.ObjectMapperFactory;
import util.PacketService;

public class TCPClient {
	private static final ObjectMapper objectMapper = ObjectMapperFactory.create();
	private static final BlockingQueue<Packet> sendQueue = new LinkedBlockingQueue<>();
	private static final Map<String, FileDownloadSessionClient> downloadSessions = new ConcurrentHashMap<>();
	private static final NetworkConfig networkConfig = loadConfig();

	public static void main(String arg[]) {
		objectMapper.disable(SerializationFeature.INDENT_OUTPUT);

		try (Socket socket = new Socket(networkConfig.getIpAddress(), networkConfig.getPort())) {
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

	private static NetworkConfig loadConfig() {
		try {
			File file = new File("client-config/config.json");

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

	public static void handleServerPacket(Packet packet) {

		switch (packet.getCommand()) {
			case "auth":
				AuthResponse authResponse = objectMapper.convertValue(packet.getData(), AuthResponse.class);

				if (authResponse.getStatus().equals("success")) {
					LocalStorage.setUserLogin(authResponse.getUserLogin());
				}

				LoginForm loginForm = (LoginForm) AppFrame.getInstance().getContextPools().getContext("loginForm");

				SwingUtilities.invokeLater(() -> {
					loginForm.getResponse(authResponse);
				});
				break;
			case "registry":
				RegisterResponse registerResponse = objectMapper.convertValue(packet.getData(), RegisterResponse.class);

				RegisterForm registerForm = (RegisterForm) AppFrame.getInstance().getContextPools()
						.getContext("registerForm");

				SwingUtilities.invokeLater(() -> {
					registerForm.getResponse(registerResponse);
				});
				break;
			case "dialogs/get":
				UserDialogResponse userDialogResponse = objectMapper.convertValue(packet.getData(),
						UserDialogResponse.class);

				LocalStorage.setUserDialogs(userDialogResponse.getUserDialogs());

				Sidebar sidebar = (Sidebar) AppFrame.getInstance().getContextPools().getContext("sidebar");

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

				Sidebar sidebar1 = (Sidebar) AppFrame.getInstance().getContextPools().getContext("sidebar");

				SwingUtilities.invokeLater(() -> {
					sidebar1.getResponse(dialogContentResponse);
				});

				SwingUtilities.invokeLater(() -> {
					userDialog.getResponse(dialogContentResponse);
				});
				break;
			case "dialogs/send":
				SendMessageResponse sendMessageResponse = objectMapper.convertValue(packet.getData(),
						SendMessageResponse.class);

				if (sendMessageResponse.getStatus().equals("success")) {
					LocalStorage.addMessage(sendMessageResponse.getDialogId(),
							sendMessageResponse.getMessagePersisted());
				}

				UserDialog userDialog1 = (UserDialog) AppFrame.getInstance().getContextPools()
						.getContext(sendMessageResponse.getDialogId());

				Sidebar sidebar2 = (Sidebar) AppFrame.getInstance().getContextPools().getContext("sidebar");

				SwingUtilities.invokeLater(() -> {
					sidebar2.getResponse(sendMessageResponse);
				});

				SwingUtilities.invokeLater(() -> {
					userDialog1.getResponse(sendMessageResponse);
				});
				break;
			case "dialogs/delete":
				DeleteMessageResponse deleteMessageResponse = objectMapper.convertValue(packet.getData(),
						DeleteMessageResponse.class);

				if (deleteMessageResponse.getStatus().equals("success")) {
					LocalStorage.removeMessage(deleteMessageResponse.getDialogId(),
							deleteMessageResponse.getMessageDeleted().getId());
				}

				UserDialog userDialog2 = (UserDialog) AppFrame.getInstance().getContextPools()
						.getContext(deleteMessageResponse.getDialogId());

				Sidebar sidebar3 = (Sidebar) AppFrame.getInstance().getContextPools().getContext("sidebar");

				SwingUtilities.invokeLater(() -> {
					sidebar3.getResponse(deleteMessageResponse);
				});

				SwingUtilities.invokeLater(() -> {
					userDialog2.getResponse(deleteMessageResponse);
				});

				break;
			case "dialogs/upload":
				FileTransferResponse fileTransferResponse = objectMapper.convertValue(packet.getData(),
						FileTransferResponse.class);

				UserDialog userDialog3 = (UserDialog) AppFrame.getInstance().getContextPools()
						.getContext(fileTransferResponse.getDialogId());

				SwingUtilities.invokeLater(() -> {
					userDialog3.getResponse(fileTransferResponse);
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

						UserDialog userDialog4 = (UserDialog) AppFrame.getInstance().getContextPools()
								.getContext(fileDownloadResponse.getDialogId());

						SwingUtilities.invokeLater(() -> {
							userDialog4.getResponse(new FileDownloadAck(
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
			case "dialogs/group/create":
				CreateGroupResponse createGroupResponse = objectMapper.convertValue(packet.getData(),
						CreateGroupResponse.class);

				if (createGroupResponse.getStatus().equals("success")) {
					LocalStorage.addDialog(createGroupResponse.getDialog());
				}

				Sidebar sidebar4 = (Sidebar) AppFrame.getInstance().getContextPools().getContext("sidebar");

				SwingUtilities.invokeLater(() -> {
					sidebar4.getResponse(createGroupResponse);
				});

				break;
			case "users/fetch":
				SearchUserResponse searchUserResponse = objectMapper.convertValue(packet.getData(),
						SearchUserResponse.class);

				if (searchUserResponse.getStatus().equals("success")) {
					searchUserResponse.getFoundUsers().forEach(LocalStorage::addUser);
				}

				Sidebar sidebar5 = (Sidebar) AppFrame.getInstance().getContextPools().getContext("sidebar");

				SwingUtilities.invokeLater(() -> {
					sidebar5.getResponse(searchUserResponse);
				});

				break;
			case "users/search":
				SearchUserResponse searchUserResponse1 = objectMapper.convertValue(packet.getData(),
						SearchUserResponse.class);

				Sidebar sidebar6 = (Sidebar) AppFrame.getInstance().getContextPools().getContext("sidebar");

				SwingUtilities.invokeLater(() -> {
					sidebar6.getResponse(searchUserResponse1);
				});
				break;
			case "users/fetch-new":
				FetchNewUserResponse fetchNewUserResponse = objectMapper.convertValue(packet.getData(),
						FetchNewUserResponse.class);

				if (fetchNewUserResponse.getStatus().equals("success")) {
					LocalStorage.addUser(fetchNewUserResponse.getUser());
				}
				break;

			case "users/update-info":
				UserUpdateResponse userUpdateResponse = objectMapper.convertValue(packet.getData(),
						UserUpdateResponse.class);

				if (userUpdateResponse.getStatus().equals("success")) {
					LocalStorage.setUserLogin(userUpdateResponse.getUser());
					LocalStorage.addUser(userUpdateResponse.getUserMetadata());
				}

				break;

			case "users/fetch-update":
				UserUpdateResponse userUpdateResponse1 = objectMapper.convertValue(packet.getData(),
						UserUpdateResponse.class);

				if (userUpdateResponse1.getStatus().equals("success")) {
					LocalStorage.addUser(userUpdateResponse1.getUserMetadata());
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
