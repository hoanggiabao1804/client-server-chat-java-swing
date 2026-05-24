package util;

import java.util.List;

import domain.Dialog;
import domain.Message;
import domain.User;

public class LocalStorage {
    private static User userLogin;
    private static List<Dialog> userDialogs;

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
}
