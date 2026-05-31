package component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Menu extends JFrame {
    private static Menu menuInstance = null;
    private static Map<String, AppContext> componentMap = new ConcurrentHashMap<>();

    private Menu() {
        NavigationBar navigationBar = new NavigationBar(this, new Dimension(1480, 60));
        ContentPage contentPage = new ContentPage(this, new Dimension(1480, 900));
        componentMap.put("navigationBar", navigationBar);
        componentMap.put("contentPage", contentPage);
    }

    public static Menu getInstance() {
        if (menuInstance == null) {
            menuInstance = new Menu();
        }

        return menuInstance;
    }

    public void run() {
        this.setTitle("Chat App Server");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(null,
                        "Bạn có chắc muốn thoát ứng dụng?", "Đóng ứng dụng?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    // RepositoryManager.store();
                    System.exit(0);
                }
            }
        });
        this.setResizable(false);

        NavigationBar navigationBar = (NavigationBar) getContext("navigationBar");
        ContentPage contentPage = (ContentPage) getContext("contentPage");
        this.add(navigationBar.getRootComponent(), BorderLayout.NORTH);
        this.add(contentPage.getRootComponent(), BorderLayout.CENTER);

        this.setSize(new Dimension(1480, 960));
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public AppContext getContext(String context) {
        return componentMap.get(context);
    }
}