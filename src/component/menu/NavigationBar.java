package component.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import component.AppContext;
import component.AppFrame;
import domain.User;
import util.Authentication;

public class NavigationBar implements AppContext {
    private final Container parent;
    private Dimension size;

    // Data source
    private User userLogin;

    // Root container
    private JPanel navbarContainer;

    // Title
    private JPanel titleRootContainer;
    private JLabel titleLabel;

    // User menu
    private JPanel menuRootContainer;
    private JButton profileButton;
    private ImageIcon profileIcon;
    private JButton logoutButton;
    private ImageIcon logoutIcon;

    public NavigationBar(Container parent, Dimension size) {
        this.parent = parent;
        this.size = size;
        Color bgColor = new Color(0, 129, 138);
        // Color headerColor = new Color(219, 237, 243);

        userLogin = Authentication.getInstance().getUserLogin();

        // Root initialization
        navbarContainer = new JPanel();

        // Title initialization
        titleRootContainer = new JPanel();
        titleLabel = new JLabel();

        // User menu initialization
        menuRootContainer = new JPanel();
        profileButton = new JButton();
        profileIcon = new ImageIcon("assets/user-round.png");
        logoutButton = new JButton();
        logoutIcon = new ImageIcon("assets/log-out.png");

        GridBagConstraints gbc = new GridBagConstraints();
        // gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        GridBagConstraints gbc1 = new GridBagConstraints();
        // gbc1.insets = new Insets(2, 0, 2, 0);
        gbc1.anchor = GridBagConstraints.WEST;
        gbc1.gridwidth = GridBagConstraints.REMAINDER;

        // Root section
        navbarContainer.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 15));
        navbarContainer.setPreferredSize(size);
        navbarContainer.setBackground(bgColor);
        navbarContainer.add(titleRootContainer);
        navbarContainer.add(menuRootContainer);

        // Title section
        titleRootContainer.setLayout(new BorderLayout());
        titleRootContainer.setPreferredSize(new Dimension(size.width / 2 - 10, 50));
        titleRootContainer.setBackground(bgColor);
        titleRootContainer.add(titleLabel);

        titleLabel.setText("Menu Quản Lý Thư Viện");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 30));
        titleLabel.setForeground(Color.white);
        titleLabel.setBorder(new EmptyBorder(0, 30, 0, 0));

        // User menu section
        menuRootContainer.setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 0));
        menuRootContainer.setPreferredSize(new Dimension(size.width / 2 - 10, 50));
        menuRootContainer.setBackground(bgColor);
        menuRootContainer.add(profileButton);
        menuRootContainer.add(logoutButton);

        profileButton.setPreferredSize(new Dimension(200, 50));
        // profileButton.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        // profileButton.setBackground(headerColor);
        profileButton.setText("");
        profileButton.setFont(new Font("Consolas", Font.BOLD, 20));
        profileButton.setToolTipText("");
        profileButton.setIcon(profileIcon);
        profileButton.setFocusable(false);
        profileButton.addActionListener(l -> {
            AppFrame appFrame = AppFrame.getInstance();
            AppContext mainMenuContext = appFrame.getContextPools().getContext("mainMenu");
            AppContext profileContext = appFrame.getContextPools().getContext("userProfile");
            appFrame.remove(mainMenuContext.getRootComponent());
            appFrame.setMinimumSize(profileContext.getSize());
            appFrame.setSize(profileContext.getSize());

            appFrame.add(profileContext.getRootComponent());
            appFrame.repaint();
        });

        logoutButton.setPreferredSize(new Dimension(50, 50));
        // logoutButton.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        // logoutButton.setBackground(headerColor);
        logoutButton.setIcon(logoutIcon);
        logoutButton.setFocusable(false);
        // logoutButton.setBorder(null);
        logoutButton.addActionListener(l -> {
            // this.switchContext("loginForm");S
            AppFrame appFrame = AppFrame.getInstance();
            AppContext mainMenuContext = appFrame.getContextPools().getContext("mainMenu");
            AppContext loginContext = appFrame.getContextPools().getContext("loginForm");
            appFrame.remove(mainMenuContext.getRootComponent());
            appFrame.setMinimumSize(loginContext.getSize());
            appFrame.setSize(loginContext.getSize());

            appFrame.add(loginContext.getRootComponent());
            appFrame.repaint();
            // AppFrame.getInstance().dispose();
        });
    }

    public void loadUser() {
        userLogin = Authentication.getInstance().getUserLogin();
        this.profileButton.setText(userLogin.getName());
        this.profileButton.setToolTipText(userLogin.getName());
    }

    @Override
    public void draw() {
        this.parent.setSize(this.size);
        this.parent.add(this.navbarContainer);
    }

    @Override
    public void switchContext(String newContext) {
        this.parent.remove(this.navbarContainer);
        AppFrame appFrame = AppFrame.getInstance();
        AppContext context = appFrame.getContextPools().getContext(newContext);
        this.parent.setMinimumSize(context.getSize());
        this.parent.setSize(context.getSize());
        this.parent.add(context.getRootComponent());
        // this.parent.setLayout(null);
        this.parent.repaint();
    }

    @Override
    public Component getRootComponent() {
        return this.navbarContainer;
    }

    @Override
    public Dimension getSize() {
        return this.size;
    }

}
