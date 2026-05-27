package repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import constant.GenderEnum;
import domain.Dialog;
import domain.FileMessage;
import domain.Message;
import domain.TextMessage;
import domain.User;
import domain.UserMetadata;
import util.Mapper;

public class RepositoryManager {

	private static RepositoryManager repositoryManager;
	private static UserRepository userRepository;
	private static DialogRepository dialogRepository;
	private static MessageRepository messageRepository;

	private RepositoryManager() {
		userRepository = UserRepository.getInstance();
		dialogRepository = DialogRepository.getInstance();
		messageRepository = MessageRepository.getInstance();

		userRepository.importData("resource/users.txt");
		messageRepository.importData("resource/messages/");
		dialogRepository.importData("resource/dialogs.txt");
		// fakeData();
		// store();
	}

	public static RepositoryManager getInstance() {
		if (repositoryManager == null) {
			return new RepositoryManager();
		}

		return repositoryManager;
	}

	public static void store() {
		dialogRepository.exportData("resource/dialogs.txt");
		messageRepository.exportData("resource/messages/");
		userRepository.exportData("resource/users.txt");
	}

	public static void exportUsers() {
		userRepository.exportData("resource/users.txt");
	}

	public static void exportDialogs() {
		dialogRepository.exportData("resource/dialogs.txt");
	}

	public static void exportMessages(String dialogId) {
		messageRepository.exportMessages("resource/messages/", dialogId);
	}

	public static void fakeData() {
		User user1 = new User(
				UUID.randomUUID().toString(),
				"admin",
				"admin",
				"admin",
				"admin@example.com",
				LocalDate.of(2000, 1, 1),
				GenderEnum.MALE);

		User user2 = new User(
				UUID.randomUUID().toString(),
				"Hoàng Bảo",
				"bao123",
				"bao123",
				"bao123@gmail.com",
				LocalDate.of(2005, 1, 2),
				GenderEnum.MALE);

		User user3 = new User(
				UUID.randomUUID().toString(),
				"Nguyễn Văn An",
				"an123",
				"an123",
				"an123@gmail.com",
				LocalDate.of(2005, 5, 4),
				GenderEnum.MALE);

		UserMetadata user1Metadata = Mapper.userToUserMetadata(user1);
		UserMetadata user2Metadata = Mapper.userToUserMetadata(user2);
		UserMetadata user3Metadata = Mapper.userToUserMetadata(user3);

		Dialog dialog1 = new Dialog(
				UUID.randomUUID().toString(),
				"",
				List.of(user1Metadata.getId()),
				new ArrayList<>(),
				"private",
				user1.getId());

		File bucket1 = new File("resource/buckets/" + dialog1.getId());
		String bucket1MessageId = UUID.randomUUID().toString();
		long bucket1FileSize = 0L;
		if (!bucket1.exists()) {
			bucket1.mkdirs();

			File file1 = new File(bucket1.getPath() + "/" + bucket1MessageId + "-" + "file.txt");

			try {
				file1.createNewFile();

				BufferedWriter writer = new BufferedWriter(new FileWriter(file1));
				writer.write("This is a sample file for testing.");
				writer.close();

			} catch (Exception ex) {
				System.out.println(">>> ERROR: Failed to create file '" + file1.getPath() + "'.");
				ex.printStackTrace();
			}

			bucket1FileSize = file1.length();
		}

		System.out.println("File1 size: " + bucket1FileSize + "bytes");

		List<Message> messagesInDialog1 = List.of(
				new TextMessage(UUID.randomUUID().toString(), dialog1.getId(), "Hello, how are you?",
						user1.getId(),
						user1.getId(),
						LocalDateTime.of(2026, 05, 15, 12, 31, 12), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog1.getId(),
						"This is a text message.",
						user1.getId(),
						user1.getId(),
						LocalDateTime.of(2026, 05, 15, 12, 35, 54), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog1.getId(), "Uia, uia, uia.",
						user1.getId(),
						user1.getId(),
						LocalDateTime.of(2026, 05, 15, 16, 42, 22), "sent"),
				new FileMessage(bucket1MessageId, dialog1.getId(), "file.txt", bucket1FileSize,
						user1.getId(),
						user1.getId(),
						"resource/buckets/" + dialog1.getId() + "/" + bucket1MessageId + "-"
								+ "file.txt",
						LocalDateTime.of(2026, 05, 15, 17, 12, 45), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog1.getId(),
						"Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						user1.getId(),
						user1.getId(),
						LocalDateTime.of(2026, 05, 15, 23, 19, 10), "sent"));

		dialog1.setMessages(messagesInDialog1);

		Dialog dialog2 = new Dialog(
				UUID.randomUUID().toString(),
				"",
				List.of(user1Metadata.getId(), user2Metadata.getId()),
				new ArrayList<>(),
				"direct",
				user1.getId());

		File bucket2 = new File("resource/buckets/" + dialog2.getId());
		String bucket2MessageId = UUID.randomUUID().toString();
		long bucket2FileSize = 0L;
		String bucket3MessageId = UUID.randomUUID().toString();
		long bucket3FileSize = 0L;
		if (!bucket2.exists()) {
			bucket2.mkdirs();

			System.out.println(">>> Debug: dialog2.getId() = " + dialog2.getId());
			System.out.println(">>> Debug: bucket2.getPath() = " + bucket2.getPath());

			File file2 = new File(bucket2.getPath() + "/" + bucket2MessageId + "-" + "file.txt");
			File file3 = new File(bucket2.getPath() + "/" + bucket3MessageId + "-" + "helloworld.txt");

			try {
				file2.createNewFile();
				file3.createNewFile();

				BufferedWriter writer = new BufferedWriter(new FileWriter(file2));
				writer.write("This is a sample file for testing.");
				writer.close();

				writer = new BufferedWriter(new FileWriter(file3));
				writer.write("Hello, world!");
				writer.close();

			} catch (Exception ex) {
				System.out.println(">>> ERROR: Failed to create file '" + file2.getPath() + "'.");
				ex.printStackTrace();
			}

			bucket2FileSize = file2.length();
			bucket3FileSize = file3.length();
		}

		List<Message> messagesInDialog2 = List.of(
				new TextMessage(UUID.randomUUID().toString(), dialog2.getId(), "Hello, how are you?",
						user1.getId(),
						user2.getId(),
						LocalDateTime.of(2026, 05, 15, 12, 31, 12), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog2.getId(),
						"Im fine, thank you! This is a text message.",
						user2.getId(),
						user1.getId(),
						LocalDateTime.of(2026, 05, 15, 12, 35, 54), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog2.getId(), "Uia, uia, uia.",
						user1.getId(),
						user2.getId(),
						LocalDateTime.of(2026, 05, 15, 16, 42, 22), "sent"),
				new FileMessage(bucket2MessageId, dialog2.getId(), "file.txt", bucket2FileSize,
						user2.getId(),
						user1.getId(),
						"resource/buckets/" + dialog2.getId() + "/" + bucket2MessageId + "-"
								+ "file.txt",
						LocalDateTime.of(2026, 05, 15, 17, 12, 45), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog2.getId(),
						"Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						user1.getId(),
						user2.getId(),
						LocalDateTime.of(2026, 05, 15, 23, 19, 10), "sent"),
				new FileMessage(bucket3MessageId, dialog2.getId(), "helloworld.txt", bucket3FileSize,
						user1.getId(),
						user2.getId(),
						"resource/buckets/" + dialog2.getId() + "/" + bucket3MessageId + "-"
								+ "helloworld.txt",
						LocalDateTime.of(2026, 05, 15, 17, 12, 45), "sent"));

		dialog2.setMessages(messagesInDialog2);

		Dialog dialog3 = new Dialog(
				UUID.randomUUID().toString(),
				"",
				List.of(user2Metadata.getId()),
				new ArrayList<>(),
				"private",
				user2.getId());

		Dialog dialog4 = new Dialog(
				UUID.randomUUID().toString(),
				"",
				List.of(user3Metadata.getId()),
				new ArrayList<>(),
				"private",
				user3.getId());

		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(user3);

		messagesInDialog1.forEach(message -> MessageRepository.getInstance().save(message));
		messagesInDialog2.forEach(message -> MessageRepository.getInstance().save(message));

		dialogRepository.save(dialog1);
		dialogRepository.save(dialog2);
		dialogRepository.save(dialog3);
		dialogRepository.save(dialog4);
	}
}
