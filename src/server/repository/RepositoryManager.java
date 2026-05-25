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
import domain.UserMetadata;

public class RepositoryManager {

	private static RepositoryManager repositoryManager;
	private static UserRepository userRepository;
	private static DialogRepository dialogRepository;
	private static MessageRepository messageRepository;
	// private static BookRepository bookRepository;
	// private static ReaderRepository readerRepository;
	// private static CallSlipRepository callSlipRepository;
	// private static ReturnSlipRepository returnSlipRepository;

	private RepositoryManager() {
		userRepository = UserRepository.getInstance();
		dialogRepository = DialogRepository.getInstance();
		messageRepository = MessageRepository.getInstance();
		// bookRepository = BookRepository.getInstance();
		// readerRepository = ReaderRepository.getInstance();
		// callSlipRepository = CallSlipRepository.getInstance();
		// returnSlipRepository = ReturnSlipRepository.getInstance();

		userRepository.importData("resource/users.txt");
		messageRepository.importData("resource/messages/");
		dialogRepository.importData("resource/dialogs.txt");
		// fakeData();
		// store();
		// bookRepository.importData("resource/books.txt");
		// readerRepository.importData("resource/readers.txt");
		// GenreRepository.importData("resource/genres.txt");
		// callSlipRepository.importData("resource/call_slips.txt");
		// returnSlipRepository.importData("resource/return_slips.txt");
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
		// userRepository.exportData("resource/librarians.txt");
		// bookRepository.exportData("resource/books.txt");
		// readerRepository.exportData("resource/readers.txt");
		// GenreRepository.exportData("resource/genres.txt");
		// callSlipRepository.exportData("resource/call_slips.txt");
		// returnSlipRepository.exportData("resource/return_slips.txt");
	}

	public static void fakeData() {

		UserMetadata user1Metadata = new UserMetadata("019d7869-8821-7da1-9f04-53ff53d972dd", "admin",
				"admin@example.com", LocalDate.of(2000, 1, 1), GenderEnum.MALE);
		UserMetadata user2Metadata = new UserMetadata("727dfee5-0591-447d-bef0-5ee0f56089f5", "Hoàng Bảo",
				"bao123@gmail.com", LocalDate.of(2000, 1, 2), GenderEnum.MALE);

		Dialog dialog1 = new Dialog(
				UUID.randomUUID().toString(),
				"admin",
				List.of(user1Metadata),
				new ArrayList<>(),
				"single",
				"019d7869-8821-7da1-9f04-53ff53d972dd");

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

		System.out.println("FIle1 size: " + bucket1FileSize + "bytes");

		List<Message> messagesInDialog1 = List.of(
				new TextMessage(UUID.randomUUID().toString(), dialog1.getId(), "Hello, how are you?",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						LocalDateTime.of(2026, 05, 15, 12, 31, 12), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog1.getId(),
						"This is a text message.",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						LocalDateTime.of(2026, 05, 15, 12, 35, 54), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog1.getId(), "Uia, uia, uia.",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						LocalDateTime.of(2026, 05, 15, 16, 42, 22), "sent"),
				new FileMessage(bucket1MessageId, dialog1.getId(), "file.txt", bucket1FileSize,
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"resource/buckets/" + dialog1.getId() + "/" + bucket1MessageId + "-"
								+ "file.txt",
						LocalDateTime.of(2026, 05, 15, 17, 12, 45), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog1.getId(),
						"Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						LocalDateTime.of(2026, 05, 15, 23, 19, 10), "sent"));

		dialog1.setMessages(messagesInDialog1);

		Dialog dialog2 = new Dialog(
				UUID.randomUUID().toString(),
				"Hoàng Bảo",
				List.of(user1Metadata, user2Metadata),
				new ArrayList<>(),
				"direct",
				"019d7869-8821-7da1-9f04-53ff53d972dd");

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
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"727dfee5-0591-447d-bef0-5ee0f56089f5",
						LocalDateTime.of(2026, 05, 15, 12, 31, 12), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog2.getId(),
						"Im fine, thank you! This is a text message.",
						"727dfee5-0591-447d-bef0-5ee0f56089f5",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						LocalDateTime.of(2026, 05, 15, 12, 35, 54), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog2.getId(), "Uia, uia, uia.",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"727dfee5-0591-447d-bef0-5ee0f56089f5",
						LocalDateTime.of(2026, 05, 15, 16, 42, 22), "sent"),
				new FileMessage(bucket2MessageId, dialog2.getId(), "file.txt", bucket2FileSize,
						"727dfee5-0591-447d-bef0-5ee0f56089f5",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"resource/buckets/" + dialog2.getId() + "/" + bucket2MessageId + "-"
								+ "file.txt",
						LocalDateTime.of(2026, 05, 15, 17, 12, 45), "sent"),
				new TextMessage(UUID.randomUUID().toString(), dialog2.getId(),
						"Aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"727dfee5-0591-447d-bef0-5ee0f56089f5",
						LocalDateTime.of(2026, 05, 15, 23, 19, 10), "sent"),
				new FileMessage(bucket3MessageId, dialog2.getId(), "helloworld.txt", bucket3FileSize,
						"019d7869-8821-7da1-9f04-53ff53d972dd",
						"727dfee5-0591-447d-bef0-5ee0f56089f5",
						"resource/buckets/" + dialog2.getId() + "/" + bucket3MessageId + "-"
								+ "helloworld.txt",
						LocalDateTime.of(2026, 05, 15, 17, 12, 45), "sent"));

		dialog2.setMessages(messagesInDialog2);

		messagesInDialog1.forEach(message -> MessageRepository.getInstance().save(message));
		messagesInDialog2.forEach(message -> MessageRepository.getInstance().save(message));

		dialogRepository.save(dialog1);
		dialogRepository.save(dialog2);
	}
}
