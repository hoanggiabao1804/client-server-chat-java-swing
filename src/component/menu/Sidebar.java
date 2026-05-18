package component.menu;

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
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.EmptyBorder;

import component.AppContext;
import component.AppFrame;
import domain.User;
import repository.UserRepository;

public class Sidebar implements AppContext {

    private final Container parent;
    private final Dimension size;
    private final Map<String, User> userPool = new HashMap<>();
    private final Locale locale = Locale.of("vi");

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
    private User selectedUser;
    private JPanel selectedDialogTab;

    public Sidebar(Container parent, Dimension size) {
        this.parent = parent;
        this.size = size;

        // Font & Color
        this.headerFont = new Font("Consolas", Font.BOLD, 30);
        this.tabFont = new Font("Consolas", Font.BOLD, 20);
        this.textFont = new Font("Consolas", Font.PLAIN, 15);
        this.sidebarColor = new Color(0, 129, 138);

        List<User> userList = UserRepository.getInstance().findAll().stream().map(item -> (User) item)
                .collect(Collectors.toList());

        userList.forEach(item -> {
            this.userPool.put(item.getId().toString(), item);
        });
    }

    public void init() {

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
        dialogTabList = this.userPool.values().stream().map(item -> {
            return createDialogTabPanel(item);
        }).collect(Collectors.toList());
        selectedUser = null;
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
            this.bodyContainer.revalidate();
            this.bodyContainer.repaint();
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
        bodyContainer.setLayout(new FlowLayout(FlowLayout.LEADING));
        bodyContainer.setPreferredSize(new Dimension(size.width - 20, dialogTabList.size() * 100));
        bodyContainer.setBackground(Color.white);

        dialogTabList.forEach(item -> bodyContainer.add(item));

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setLayout(new ScrollPaneLayout());
        scrollPane.setPreferredSize(new Dimension(size.width - 10, size.height - 130));
        scrollPane.setBorder(new EmptyBorder(5, 0, 5, 0));
        scrollPane.setBackground(Color.lightGray);
    }

    private JPanel createDialogTabPanel(User user) {
        JPanel rootPanel = new JPanel();
        RoundButton avatarButton = new RoundButton();
        ImageIcon avatarIcon = new ImageIcon("assets/user-round.png");
        JPanel contentPanel = new JPanel();
        JLabel nameLabel = new JLabel();
        JLabel lastMessageLabel = new JLabel();

        // Root section
        rootPanel.setLayout(new FlowLayout());
        rootPanel.setBackground(Color.white);
        rootPanel.add(avatarButton);
        rootPanel.add(contentPanel);
        rootPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (selectedUser != null) {
                    if (selectedUser != user) {
                        selectedDialogTab.setBackground(Color.white);
                        selectedDialogTab = rootPanel;
                        selectedDialogTab.setBackground(Color.blue);

                        selectedUser = user;
                    }
                } else {
                    selectedUser = user;
                    selectedDialogTab = rootPanel;
                    selectedDialogTab.setBackground(Color.blue);
                }

                MainMenu mainMenu = (MainMenu) AppFrame.getInstance().getContextPools().getContext("mainMenu");
                mainMenu.loadDialogDetail(user);
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

        // Content section
        avatarButton.setIcon(avatarIcon);
        avatarButton.setPreferredSize(new Dimension(50, 50));
        avatarButton.setFocusable(false);

        contentPanel.setLayout(new FlowLayout());
        contentPanel.setPreferredSize(new Dimension(this.size.width - 80, 100));
        contentPanel.setBackground(Color.white);
        contentPanel.add(nameLabel);
        contentPanel.add(lastMessageLabel);

        Dimension labelSize = new Dimension(this.size.width - 80, 30);

        nameLabel.setPreferredSize(labelSize);
        nameLabel.setText(user.getName());
        nameLabel.setFont(this.tabFont);

        lastMessageLabel.setPreferredSize(labelSize);
        lastMessageLabel.setText("Đây là tin nhắn cuối cùng trong đoạn chat");
        lastMessageLabel.setFont(this.textFont);

        return rootPanel;
    }

    public void revalidateData(User user) {
        this.selectedUser = user;

        dialogTabList = this.userPool.values().stream().map(item -> {
            return createDialogTabPanel(item);
        }).collect(Collectors.toList());

        bodyContainer.removeAll();
        bodyContainer.setPreferredSize(new Dimension(size.width - 20, dialogTabList.size() * 100));
        dialogTabList.forEach(item -> bodyContainer.add(item));
    }

    public void setUserPool(List<User> userPool) {
        this.userPool.clear();
        userPool.forEach(item -> {
            this.userPool.put(item.getId().toString(), item);
        });

        dialogTabList = this.userPool.values().stream().map(item -> {
            return createDialogTabPanel(item);
        }).collect(Collectors.toList());

        bodyContainer.removeAll();
        bodyContainer.setPreferredSize(new Dimension(size.width - 20, dialogTabList.size() * 100));
        dialogTabList.forEach(item -> bodyContainer.add(item));
    }

    private void search(String keyword) {
        List<User> searchedUsers = this.userPool.values().stream().filter(item -> {
            if (keyword.isBlank()) {
                return true;
            }

            if (item.getName().toLowerCase(locale).contains(keyword)) {
                return true;
            }

            return false;
        }).collect(Collectors.toList());

        dialogTabList = searchedUsers.stream().map(item -> {
            return createDialogTabPanel(item);
        }).collect(Collectors.toList());

        if (dialogTabList.size() > 0) {
            for (int i = 0; i < 10; ++i) {
                dialogTabList.add(createDialogTabPanel(searchedUsers.get(0)));
            }
        }

        bodyContainer.removeAll();
        bodyContainer.setPreferredSize(new Dimension(size.width - 20, dialogTabList.size() * 115));
        dialogTabList.forEach(item -> bodyContainer.add(item));
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

        // Background
        g2.setColor(this.getBackground());

        // Round corners
        g2.fillOval(0, 0, getWidth(), getHeight());

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(Color.BLACK);
        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

        g2.dispose();
    }
}
