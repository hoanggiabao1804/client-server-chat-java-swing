package repository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.Dialog;
import domain.Message;
import domain.UserMetadata;
import util.ObjectMapperFactory;

public class DialogRepository implements Repository {
    private static DialogRepository dialogRepository = null;
    private final ObjectMapper objectMapper;

    private static final Map<String, Dialog> storage = new HashMap<>();
    private static final Map<String, List<Dialog>> userIdIndexedStorage = new HashMap<>();

    private DialogRepository() {
        objectMapper = ObjectMapperFactory.create();
    }

    public static DialogRepository getInstance() {
        if (dialogRepository == null) {
            dialogRepository = new DialogRepository();
        }

        return dialogRepository;
    }

    public void importData(String path) {
        try {
            File file = new File(path);

            List<Dialog> dialogs = objectMapper.readValue(file, new TypeReference<List<Dialog>>() {
            });
            storage.putAll(dialogs.stream().collect(Collectors.toMap(Dialog::getId, dialog -> {
                List<Message> messages = MessageRepository.getInstance().findByDialogId(dialog.getId());
                dialog.setMessages(messages);
                return dialog;
            })));

            // Index dialogs by user ID
            for (Dialog dialog : dialogs) {
                for (UserMetadata userMetadata : dialog.getParticipants()) {
                    userIdIndexedStorage.computeIfAbsent(userMetadata.getId(), k -> new ArrayList<>()).add(dialog);
                }
            }

            System.out.println(">>> Imported total " + dialogs.size() + " dialog(s) from directory '" + path + "'.");
        } catch (Exception ex) {
            System.out.println(">>> ERROR: Failed to import dialogs from directory '" + path + "'.");
            ex.printStackTrace();
        }
    }

    public void exportData(String path) {

        List<Dialog> dialogs = new ArrayList<>(storage.values());

        try {
            // for (Dialog dialog : dialogs) {
            // String filePath = path + dialog.getId() + ".json";
            // File file = new File(filePath);
            // objectMapper.writeValue(file, dialog);
            // }
            File file = new File(path);
            objectMapper.writeValue(file, dialogs);
            System.out.println(">>> Exported total " + storage.size() + " dialog(s) to directory '" + path + "'.");
        } catch (Exception ex) {
            System.out.println(">>> ERROR: Failed to export dialogs to directory '" + path + "'.");
            ex.printStackTrace();
        }
    }

    public List<Dialog> findAll() {
        return new ArrayList<>(storage.values());
    }

    public Dialog findById(String id) {
        return storage.get(id);
    }

    public List<Dialog> findByUserId(String userId) {
        return userIdIndexedStorage.getOrDefault(userId, null);
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    public Dialog save(Dialog dialog) {
        String id = dialog.getId();

        if (storage.containsKey(id)) {
            Dialog currentDialog = storage.get(id);

            currentDialog.setName(dialog.getName());
            currentDialog.setParticipants(dialog.getParticipants());
            currentDialog.setMessages(dialog.getMessages());

            return currentDialog;
        }

        storage.put(id, dialog);
        for (UserMetadata userMetadata : dialog.getParticipants()) {
            userIdIndexedStorage.computeIfAbsent(userMetadata.getId(), k -> new ArrayList<>()).add(dialog);
        }

        return dialog;
    }

    public void deleteById(String id) {
        if (storage.containsKey(id)) {
            Dialog dialogToRemove = storage.get(id);
            storage.remove(id, dialogToRemove);

            for (UserMetadata userMetadata : dialogToRemove.getParticipants()) {
                userIdIndexedStorage.computeIfPresent(userMetadata.getId(), (k, v) -> {
                    v.remove(dialogToRemove);
                    return v;
                });
            }
        }
    }

    public Dialog findDirectDialog(String userA, String userB) {
        return storage.values().stream().filter(item -> "direct".equals(item.getType()))
                .filter(item -> item.getParticipants().size() == 2)
                .filter(item -> item.getParticipants().stream().anyMatch(part -> part.getId().equals(userA)))
                .filter(item -> item.getParticipants().stream().anyMatch(part -> part.getId().equals(userB)))
                .findFirst().orElse(null);
    }
}
