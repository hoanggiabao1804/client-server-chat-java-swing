package util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import domain.Dialog;
import domain.Message;
import domain.User;
import domain.UserMetadata;

public class LocalStorage {
    private static User userLogin;
    private static List<Dialog> userDialogs;
    private static Map<String, UserMetadata> users = new ConcurrentHashMap<>();

    public synchronized static void reset() {
        LocalStorage.userLogin = null;
        LocalStorage.userDialogs = null;
        LocalStorage.users = new ConcurrentHashMap<>();
    }

    public synchronized static User getUserLogin() {
        return LocalStorage.userLogin;
    }

    public synchronized static void setUserLogin(User userLogin) {
        LocalStorage.userLogin = userLogin;
    }

    public synchronized static List<Dialog> getUserDialogs() {
        return LocalStorage.userDialogs;
    }

    public synchronized static void setUserDialogs(List<Dialog> userDialogs) {
        LocalStorage.userDialogs = userDialogs;
    }

    public synchronized static Dialog getDialog(String dialogId) {
        for (Dialog dialog : userDialogs) {
            if (dialog.getId().equals(dialogId)) {
                return dialog;
            }
        }

        return null;
    }

    public synchronized static void setDialogMessageContent(String dialogId, List<Message> messages) {
        for (Dialog dialog : userDialogs) {
            if (dialog.getId().equals(dialogId)) {
                dialog.setMessages(messages);
            }
        }
    }

    public synchronized static void addMessage(String dialogId, Message message) {
        for (Dialog dialog : userDialogs) {
            if (dialog.getId().equals(dialogId)) {
                dialog.getMessages().add(message);
            }
        }
    }

    public synchronized static void removeMessage(String dialogId, String messageId) {
        for (Dialog dialog : userDialogs) {
            if (dialog.getId().equals(dialogId)) {
                dialog.getMessages().removeIf(message -> message.getId().equals(messageId));
            }
        }
    }

    public synchronized static void setUsers(Map<String, UserMetadata> users) {
        LocalStorage.users = users;
    }

    public synchronized static Map<String, UserMetadata> getUsers() {
        return LocalStorage.users;
    }

    public synchronized static UserMetadata getUserById(String id) {
        return LocalStorage.users.getOrDefault(id, null);
    }

    public synchronized static void addUser(UserMetadata userMetadata) {
        LocalStorage.users.put(userMetadata.getId(), userMetadata);
    }

    public synchronized static void addDialog(Dialog dialog) {
        LocalStorage.userDialogs.addFirst(dialog);
    }

    public synchronized static Dialog findDirectDialog(String userA, String userB) {
        return userDialogs.stream().filter(item -> "direct".equals(item.getType()))
                .filter(item -> item.getParticipants().size() == 2)
                .filter(item -> item.getParticipants().stream().anyMatch(part -> part.getId().equals(userA)))
                .filter(item -> item.getParticipants().stream().anyMatch(part -> part.getId().equals(userB)))
                .findFirst().orElse(null);
    }
}
