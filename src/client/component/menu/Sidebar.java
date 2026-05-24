package component.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import component.AppContext;
import component.AppFrame;
// import server.repository.DialogRepository;
import domain.Dialog;
import domain.Message;
import domain.User;
import domain.dto.UserDialogResponse;
import util.LocalStorage;
import util.PacketService;

public class Sidebar implements AppContext {

    private final Container parent;
    private final Dimension size;
    private final Map<String, Dialog> storage = new HashMap<>();
    private final Locale locale = Locale.of("vi");

    private User userLogin;

    // Font & color
    private final Font headerFont;
    private final Font tabFont;
    private final Font textFont;
    private final Color sidebarColor;

    // Containers section
    private JPanel sidebarContainer;
    private JPanel headerContainer;
    private JPanel bodyContainer;

    // Header section
    private JLabel headerLabel;
    private JPanel searchContainer;
    private JTextField searchTextField;
    private JButton searchButton;
    private ImageIcon searchIcon;

    // Body section
    private JScrollPane scrollPane;
    private List<JPanel> dialogTabList;
    private Dialog selectedDialog;
    private JPanel selectedDialogTab;

    private CountDownLatch countDownLatch;

    public Sidebar(Container parent, Dimension size) {
        this.parent = parent;
        this.size = size;
        this.countDownLatch = new CountDownLatch(1);

        // Font & Color
        this.headerFont = new Font("Consolas", Font.BOLD, 30);
        this.tabFont = new Font("Consolas", Font.BOLD, 20);
        this.textFont = new Font("Consolas", Font.PLAIN, 15);
        this.sidebarColor = new Color(0, 129, 138);

        // Container initialization
        sidebarContainer = new JPanel();
        headerContainer = new JPanel();
        bodyContainer = new JPanel();

        // Header initialization
        headerLabel = new JLabel();
        searchContainer = new JPanel();
        searchTextField = new JTextField();
        searchButton = new JButton();
        searchIcon = new ImageIcon("assets/search.png");

        // Body initialization
        scrollPane = new JScrollPane(bodyContainer);
        dialogTabList = null;
        // dialogTabList = this.storage.values().stream().map(item -> {
        // return createDialogTabPanel(item);
        // }).collect(Collectors.toList());
        selectedDialog = null;
        selectedDialogTab = null;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Root container section
        sidebarContainer.setLayout(new GridBagLayout());
        sidebarContainer.setPreferredSize(size);
        sidebarContainer.setBackground(sidebarColor);
        sidebarContainer.add(headerContainer, gbc);
        sidebarContainer.add(scrollPane, gbc);

        // Header section
        headerContainer.setLayout(new GridBagLayout());
        headerContainer.setPreferredSize(new Dimension(size.width, 120));
        headerContainer.setBackground(Color.white);
        headerContainer.add(headerLabel, gbc);
        headerContainer.add(searchContainer, gbc);

        headerLabel.setText("Đoạn chat");
        headerLabel.setFont(this.headerFont);
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        searchContainer.setLayout(new FlowLayout());
        searchContainer.setPreferredSize(new Dimension(size.width - 20, 45));
        searchContainer.setBackground(Color.white);
        searchContainer.add(searchTextField);
        searchContainer.add(searchButton);

        searchTextField.setPreferredSize(new Dimension(size.width - 90, 35));
        searchTextField.setFont(this.textFont);
        searchTextField.setBackground(Color.white);
        searchTextField.setForeground(Color.black);
        searchTextField.addActionListener(e -> {
            String keyword = searchTextField.getText().strip();

            this.search(keyword.toLowerCase(locale));
        });

        searchButton.setIcon(searchIcon);
        searchButton.setFocusable(false);
        searchButton.addActionListener(l -> {
            String keyword = searchTextField.getText().strip();

            this.search(keyword.toLowerCase(locale));
            this.bodyContainer.revalidate();
            this.bodyContainer.repaint();
        });

        // Body section
        bodyContainer.setLayout(new BoxLayout(bodyContainer, BoxLayout.Y_AXIS));
        // bodyContainer.setPreferredSize(new Dimension(size.width - 20,
        // dialogTabList.size() * 100));
        bodyContainer.setBackground(Color.white);

        // dialogTabList.forEach(item -> bodyContainer.add(item));

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setLayout(new ScrollPaneLayout());
        scrollPane.setPreferredSize(new Dimension(size.width - 10, size.height - 130));
        scrollPane.setBorder(new EmptyBorder(5, 0, 5, 0));
        scrollPane.setBackground(Color.lightGray);

    }

    private JPanel createDialogTabPanel(Dialog dialog) {
        JPanel rootPanel = new JPanel();
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        RoundButton avatarButton = new RoundButton();
        ImageIcon avatarIcon = new ImageIcon("assets/user-round.png");
        JPanel contentPanel = new JPanel();
        JLabel nameLabel = new JLabel();
        JLabel lastMessageLabel = new JLabel();

        int tabHeight = 80;
        int tabWidth = this.size.width - 20;

        // Root section
        rootPanel.setLayout(new BorderLayout(10, 0));
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rootPanel.setBackground(Color.white);
        rootPanel.setPreferredSize(new Dimension(tabWidth, tabHeight));
        rootPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, tabHeight));
        rootPanel.setMinimumSize(new Dimension(tabWidth, tabHeight));
        rootPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (selectedDialog != null) {
                    if (selectedDialog != dialog) {
                        selectedDialogTab.setBackground(Color.white);
                        selectedDialogTab = rootPanel;
                        selectedDialogTab.setBackground(Color.blue);

                        selectedDialog = dialog;
                    }
                } else {
                    selectedDialog = dialog;
                    selectedDialogTab = rootPanel;
                    selectedDialogTab.setBackground(Color.blue);
                }

                MainMenu mainMenu = (MainMenu) AppFrame.getInstance().getContextPools().getContext("mainMenu");
                mainMenu.loadDialogDetail(dialog);
            }

            public void mouseEntered(MouseEvent e) {
                if (selectedDialogTab != rootPanel) {
                    rootPanel.setBackground(Color.lightGray);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (selectedDialogTab != rootPanel) {
                    rootPanel.setBackground(Color.white);
                }
            }
        });

        avatarPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(60, 50));

        // Content section
        avatarButton.setIcon(avatarIcon);
        avatarButton.setPreferredSize(new Dimension(50, 50));
        avatarButton.setMinimumSize(new Dimension(50, 50));
        avatarButton.setMaximumSize(new Dimension(50, 50));
        avatarButton.setFocusable(false);

        avatarPanel.add(avatarButton);

        // contentPanel.setLayout(new FlowLayout());
        // contentPanel.setPreferredSize(new Dimension(this.size.width - 80, 100));
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Dimension labelSize = new Dimension(this.size.width - 80, 30);

        // nameLabel.setPreferredSize(labelSize);
        nameLabel.setText(dialog.getName());
        nameLabel.setFont(this.tabFont);

        // lastMessageLabel.setPreferredSize(labelSize);
        Message lastMsg = dialog.getMessages() == null || dialog.getMessages().isEmpty() ? null
                : dialog.getMessages().getLast();

        lastMessageLabel.setText(lastMsg != null
                ? lastMsg.getSenderId().equals(userLogin.getId().toString()) ? "Bạn: " + lastMsg.getContent()
                        : lastMsg.getContent()
                : "");
        lastMessageLabel.setFont(this.textFont);

        contentPanel.add(nameLabel);
        contentPanel.add(lastMessageLabel);

        rootPanel.add(avatarPanel, BorderLayout.WEST);
        rootPanel.add(contentPanel, BorderLayout.CENTER);

        return rootPanel;
    }

    public void revalidateData(Dialog dialog) {
        this.selectedDialog = dialog;

        dialogTabList = this.storage.values().stream().map(item -> {
            return createDialogTabPanel(item);
        }).collect(Collectors.toList());

        bodyContainer.removeAll();
        dialogTabList.forEach(item -> bodyContainer.add(item));
    }

    public void setDialogPool(List<Dialog> dialogPool) {
        this.storage.clear();
        dialogPool.forEach(item -> {
            this.storage.put(item.getId(), item);
        });

        dialogTabList = this.storage.values().stream().map(item -> {
            return createDialogTabPanel(item);
        }).collect(Collectors.toList());

        bodyContainer.removeAll();
        dialogTabList.forEach(item -> bodyContainer.add(item));
    }

    private void search(String keyword) {
        List<Dialog> searchedDialogs = this.storage.values().stream().filter(item -> {
            if (keyword.isBlank()) {
                return true;
            }

            if (item.getName().toLowerCase(locale).contains(keyword)) {
                return true;
            }

            return false;
        }).collect(Collectors.toList());

        dialogTabList = searchedDialogs.stream().map(item -> {
            return createDialogTabPanel(item);
        }).collect(Collectors.toList());

        // if (dialogTabList.size() > 0) {
        // for (int i = 0; i < 10; ++i) {
        // dialogTabList.add(createDialogTabPanel(searchedDialogs.get(0)));
        // }
        // }

        bodyContainer.removeAll();
        dialogTabList.forEach(item -> bodyContainer.add(item));
        this.bodyContainer.revalidate();
        this.bodyContainer.repaint();
    }

    public void reset() {
        storage.clear();
        userLogin = null;
        searchTextField.setText("");
        dialogTabList.clear();
        selectedDialog = null;
        selectedDialogTab = null;
        countDownLatch = new CountDownLatch(1);
    }

    public void loadUser() {
        userLogin = LocalStorage.getUserLogin();

        countDownLatch = new CountDownLatch(1);
        PacketService.loadUserDialogs(userLogin.getId().toString());

        new Thread(() -> {
            try {
                boolean success = countDownLatch.await(5, TimeUnit.SECONDS);

                if (!success) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                                null,
                                "Server không phản hồi!",
                                "Tải danh sách không thành công",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void getResponse(UserDialogResponse userDialogResponse) {

        if (userDialogResponse.getStatus().equals("success")) {
            List<Dialog> dialogList = LocalStorage.getUserDialogs();

            dialogList.forEach(item -> {
                this.storage.put(item.getId(), item);
            });

            dialogTabList = this.storage.values().stream().map(item -> {
                return createDialogTabPanel(item);
            }).collect(Collectors.toList());

            bodyContainer.removeAll();
            dialogTabList.forEach(item -> bodyContainer.add(item));
            this.bodyContainer.revalidate();
            this.bodyContainer.repaint();
        } else {
            JOptionPane.showMessageDialog(null, userDialogResponse.getMessage(),
                    "Lấy danh sách đoạn hội thoạt thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public CountDownLatch getCountDownLatch() {
        return this.countDownLatch;
    }

    @Override
    public void draw() {
        this.parent.setSize(this.size);
        this.parent.add(this.sidebarContainer);
    }

    @Override
    public void switchContext(String newContext) {
        this.parent.remove(this.sidebarContainer);

        AppFrame appFrame = AppFrame.getInstance();
        AppContext context = appFrame.getContextPools().getContext(newContext);
        this.parent.setSize(context.getSize());
        this.parent.add(context.getRootComponent());
        // this.parent.setLayout(null);
        this.parent.repaint();
    }

    @Override
    public Component getRootComponent() {
        return this.sidebarContainer;
    }

    @Override
    public Dimension getSize() {
        return this.size;
    }
}

// Custom button
class RoundButton extends JButton {

    public RoundButton() {
        super();

        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }

    public RoundButton(String label) {
        super(label);

        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Smooth edges
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // // Background
        // g2.setColor(this.getBackground());

        // // Round corners
        // g2.fillOval(0, 0, getWidth(), getHeight());

        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2.setColor(getBackground());
        g2.fillOval(x, y, size, size);

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // g2.setColor(Color.BLACK);
        // g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, size - 1, size - 1);

        g2.dispose();
    }
}
