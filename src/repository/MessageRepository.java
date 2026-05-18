package repository;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.FileMessage;
import domain.Message;
import util.ObjectMapperFactory;

public class MessageRepository implements Repository {

    private static MessageRepository messageRepository = null;
    private final ObjectMapper objectMapper;

    private static final Map<String, Message> storage = new HashMap<>();
    private static final Map<String, List<Message>> dialogIdIndexedStorage = new HashMap<>();

    private MessageRepository() {
        objectMapper = ObjectMapperFactory.create();
    }

    public static MessageRepository getInstance() {
        if (messageRepository == null) {
            messageRepository = new MessageRepository();
        }

        return messageRepository;
    }

    public void importData(String path) {
        try {
            List<String> fileNames = Files.list(Paths.get(path)).filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString()).filter(name -> name.endsWith(".json"))
                    .collect(Collectors.toList());

            for (String fileName : fileNames) {
                File file = new File(path + fileName);

                List<Message> messages = objectMapper.readValue(file, new TypeReference<List<Message>>() {
                });

                storage.putAll(messages.stream().collect(Collectors.toMap(Message::getId, message -> message)));

                if (!messages.isEmpty()) {
                    dialogIdIndexedStorage.put(messages.get(0).getDialogId(), messages);
                }
            }

            System.out.println(">>> Imported total " + fileNames.size() + " message(s) from directory '" + path + "'.");
        } catch (Exception ex) {
            System.out.println(">>> ERROR: Failed to import messages from directory '" + path + "'.");
            ex.printStackTrace();
        }
    }

    public void exportData(String path) {
        try {
            for (Map.Entry<String, List<Message>> entry : dialogIdIndexedStorage.entrySet()) {
                String dialogId = entry.getKey();
                List<Message> messages = entry.getValue();

                String filePath = path + dialogId + ".json";
                objectMapper.writeValue(new File(filePath), messages);
            }
            System.out.println(
                    ">>> Exported total " + dialogIdIndexedStorage.size() + " message(s) to directory '" + path + "'.");
        } catch (Exception ex) {
            System.out.println(">>> ERROR: Failed to export messages to directory '" + path + "'.");
            ex.printStackTrace();
        }
    }

    public List<Message> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Message findById(String id) {
        return storage.get(id);
    }

    public List<Message> findByDialogId(String dialogId) {
        return dialogIdIndexedStorage.getOrDefault(dialogId, List.of());
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public Message save(Message message) {
        String id = message.getId();

        if (storage.containsKey(id)) {
            Message messageToRemove = storage.get(id);
            storage.remove(id, messageToRemove);
        }

        storage.put(id, message);

        String dialogId = message.getDialogId();

        List<Message> messagesInDialog = dialogIdIndexedStorage.getOrDefault(dialogId, new ArrayList<>());
        messagesInDialog.add(message);
        dialogIdIndexedStorage.put(dialogId, messagesInDialog);

        return message;
    }

    public void deleteById(String id) {
        if (storage.containsKey(id)) {
            Message messageToRemove = storage.get(id);
            storage.remove(id, messageToRemove);

            String dialogId = messageToRemove.getDialogId();

            List<Message> messagesInDialog = dialogIdIndexedStorage.getOrDefault(dialogId, new ArrayList<>());
            messagesInDialog.remove(messageToRemove);
            dialogIdIndexedStorage.put(dialogId, messagesInDialog);

            if (messageToRemove instanceof FileMessage) {
                FileMessage fileMessageToRemove = (FileMessage) messageToRemove;
                File fileToRemove = new File(fileMessageToRemove.getFilePath());

                if (fileToRemove.exists()) {
                    if (fileToRemove.delete()) {
                        System.out.println(">>> Deleted file '" + fileToRemove.getPath() + "'.");
                    } else {
                        System.out.println(">>> ERROR: Failed to delete file '" + fileToRemove.getPath() + "'.");
                    }
                }
            }
        }
    }
}
