package app;

import component.AppFrame;
import repository.RepositoryManager;

public class ChatApplication {
    public static void main(String[] args) {
        System.out.println("Server Client Chat Application!");
        RepositoryManager.getInstance();
        AppFrame app = AppFrame.getInstance();
        app.run();
    }
}
