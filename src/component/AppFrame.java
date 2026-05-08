package component;

import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import repository.RepositoryManager;

public class AppFrame extends JFrame {

    private static AppFrame instance;
    private AppContextPools contextPools;

    private AppFrame() {
        contextPools = new AppContextPools(this);
    }

    public void run() {
        this.setTitle("Library Management App");
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(null,
                        "Bạn có chắc muốn thoát ứng dụng?", "Đóng ứng dụng?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    RepositoryManager.store();
                    System.exit(0);
                }
            }
        });
        this.setResizable(false);

        contextPools.getContext("loginForm").draw();

        this.setVisible(true);
    }

    public AppContextPools getContextPools() {
        return contextPools;
    }

    public static AppFrame getInstance() {
        if (instance == null) {
            instance = new AppFrame();
        }

        return instance;
    }
}
