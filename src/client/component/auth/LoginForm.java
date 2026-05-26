package component.auth;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import component.AppContext;
import component.AppFrame;
import component.menu.MainMenu;
import domain.dto.AuthResponse;
import util.FieldValidator;
import util.PacketService;

public class LoginForm implements AppContext {

    private Container parent;
    private final Dimension size = new Dimension(500, 900);

    private String username;
    private String password;

    // Font & Color
    private Font welcomeLabelFont;
    private Font titleFont;
    private Font labelFont;
    private Font buttonFont;
    private Font textFont;
    private Font errorFont;
    private Font redirectLabelFont;
    private Color panelColor;
    private Color submitColor;

    // Root section
    private JPanel loginForm;

    // Header section
    private JPanel headerContainer;
    private JLabel welcomeLabel;
    private JLabel titleLabel;

    // Body section
    private JPanel bodyContainer;

    // Username section
    private JPanel usernameRootContainer;
    private JPanel usernameTextContainer;
    private JLabel usernameLabel;
    private JLabel userLogo;
    private ImageIcon userIcon;
    private JTextField usernameTextField;
    private JLabel usernameErrorLabel;

    // Password section
    private JPanel passwordRootContainer;
    private JPanel passwordTextContainer;
    private JLabel passwordLabel;
    private JLabel passwordLogo;
    private ImageIcon passwordIcon;
    private JPanel passwordBarPanel;
    private JPasswordField passwordTextField;
    private JButton passwordVisibilityButton;
    private ImageIcon showIcon;
    private ImageIcon hideIcon;
    private JLabel passwordErrorLabel;
    private boolean isPasswordVisible;

    // Footer section
    private JPanel footerContainer;

    // Submit section
    private JPanel submitContainer;
    private JButton submitButton;

    // Register redirection section
    private JPanel registerRedirectionContainer;
    private JLabel registerLabel;
    private JButton registerRedirectionButton;

    private CountDownLatch countDownLatch;

    public LoginForm(Container parent) {
        this.parent = parent;

        username = "";
        password = "";
        countDownLatch = new CountDownLatch(1);

        // Font & Color
        welcomeLabelFont = new Font("Consolas", Font.BOLD, 30);
        titleFont = new Font("Consolas", Font.BOLD, 40);
        labelFont = new Font("Consolas", Font.BOLD, 25);
        buttonFont = new Font("Consolas", Font.BOLD, 20);
        textFont = new Font("Consolas", Font.PLAIN, 15);
        errorFont = new Font("Consolas", Font.ITALIC, 15);
        redirectLabelFont = new Font("Consolas", Font.PLAIN, 20);
        panelColor = new Color(245, 245, 245);
        submitColor = new Color(255, 153, 0);

        // Root container
        loginForm = new JPanel();

        // Header initialization
        headerContainer = new JPanel();
        welcomeLabel = new JLabel();
        titleLabel = new JLabel();

        // Body initialization
        bodyContainer = new JPanel();

        // Username initialization
        usernameRootContainer = new JPanel();
        usernameTextContainer = new JPanel();
        usernameLabel = new JLabel();
        userLogo = new JLabel();
        userIcon = new ImageIcon("assets/user-round.png");
        usernameTextField = new JTextField();
        usernameErrorLabel = new JLabel();

        // Password initialization
        passwordRootContainer = new JPanel();
        passwordTextContainer = new JPanel();
        passwordLabel = new JLabel();
        passwordLogo = new JLabel();
        passwordIcon = new ImageIcon("assets/key-round.png");
        passwordBarPanel = new JPanel();
        passwordTextField = new JPasswordField();
        passwordVisibilityButton = new JButton();
        showIcon = new ImageIcon("assets/eye.png");
        hideIcon = new ImageIcon("assets/eye-off.png");
        passwordErrorLabel = new JLabel();
        isPasswordVisible = false;

        // Footer initialization
        footerContainer = new JPanel();

        // Submit initialization
        submitContainer = new JPanel();
        submitButton = new JButton();

        // Register redirection initialization
        registerRedirectionContainer = new JPanel();
        registerLabel = new JLabel();
        registerRedirectionButton = new JButton();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.insets = new Insets(2, 0, 2, 0);
        gbc1.anchor = GridBagConstraints.WEST;
        gbc1.gridwidth = GridBagConstraints.REMAINDER;

        // Root section
        loginForm.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        loginForm.setLayout(new GridBagLayout());
        loginForm.setPreferredSize(size);
        loginForm.setBackground(Color.white);

        loginForm.add(headerContainer, gbc);
        loginForm.add(bodyContainer, gbc);
        loginForm.add(footerContainer, gbc);

        // Header section
        headerContainer.setLayout(new GridBagLayout());
        headerContainer.setPreferredSize(new Dimension(size.width - 10, 200));
        headerContainer.setBackground(Color.white);
        headerContainer.add(welcomeLabel, gbc1);
        headerContainer.add(titleLabel, gbc1);

        welcomeLabel.setText(
                "<html><body style='text-align:center'>Chào mừng đến với ứng dụng chat</body></html>");
        welcomeLabel.setPreferredSize(new Dimension(size.width - 10, 100));
        welcomeLabel.setFont(welcomeLabelFont);
        welcomeLabel.setForeground(Color.black);

        titleLabel.setText("Đăng nhập");
        titleLabel.setPreferredSize(new Dimension(size.width - 10, 60));
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.black);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Body section
        bodyContainer.setLayout(new GridBagLayout());
        // bodyContainer.setPreferredSize(new Dimension(size.width - 10, 250));
        bodyContainer.setBackground(Color.white);
        bodyContainer.add(usernameRootContainer, gbc);
        bodyContainer.add(passwordRootContainer, gbc);

        // Username section
        usernameRootContainer.setLayout(new FlowLayout());
        usernameRootContainer.setBackground(panelColor);
        usernameRootContainer.add(userLogo);
        usernameRootContainer.add(usernameTextContainer);

        userLogo.setIcon(userIcon);
        userLogo.setHorizontalAlignment(JLabel.CENTER);
        userLogo.setBorder(new EmptyBorder(0, 5, 0, 5));

        usernameTextContainer.setLayout(new GridBagLayout());
        usernameTextContainer.setBackground(panelColor);
        usernameTextContainer.setBorder(new EmptyBorder(0, 0, 10, 0));
        usernameTextContainer.add(usernameLabel, gbc1);
        usernameTextContainer.add(usernameTextField, gbc1);
        usernameTextContainer.add(usernameErrorLabel, gbc1);

        usernameLabel.setText("Tên đăng nhập");
        usernameLabel.setFont(labelFont);
        usernameLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        usernameTextField.setPreferredSize(new Dimension(size.width - 100, 30));
        usernameTextField.setFont(textFont);
        usernameTextField.setBackground(Color.white);
        usernameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                if (usernameErrorLabel.isVisible()) {
                    usernameErrorLabel.setVisible(false);
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (usernameErrorLabel.isVisible()) {
                    usernameErrorLabel.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (usernameErrorLabel.isVisible()) {
                    usernameErrorLabel.setVisible(false);
                }
            }
        });

        usernameErrorLabel.setText("");
        usernameErrorLabel.setFont(errorFont);
        usernameErrorLabel.setForeground(Color.red);
        usernameErrorLabel.setVisible(false);

        // Password section
        passwordRootContainer.setLayout(new FlowLayout());
        passwordRootContainer.setBackground(panelColor);
        passwordRootContainer.add(passwordLogo);
        passwordRootContainer.add(passwordTextContainer);

        passwordLogo.setIcon(passwordIcon);
        passwordLogo.setHorizontalAlignment(JLabel.CENTER);
        passwordLogo.setBorder(new EmptyBorder(0, 0, 0, 5));

        passwordTextContainer.setLayout(new GridBagLayout());
        passwordTextContainer.setBackground(panelColor);
        passwordTextContainer.add(passwordLabel, gbc1);
        passwordTextContainer.add(passwordBarPanel, gbc1);
        passwordTextContainer.add(passwordErrorLabel, gbc1);

        passwordLabel.setText("Mật khẩu");
        passwordLabel.setFont(labelFont);
        passwordLabel.setBorder(new EmptyBorder(0, 5, 0, 0));

        passwordBarPanel.setLayout(new FlowLayout());
        passwordBarPanel.setBackground(panelColor);
        passwordBarPanel.add(passwordTextField);
        passwordBarPanel.add(passwordVisibilityButton);

        passwordTextField.setPreferredSize(new Dimension(size.width - 150, 30));
        passwordTextField.setFont(textFont);
        passwordTextField.setBackground(Color.white);
        passwordTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                if (passwordErrorLabel.isVisible()) {
                    passwordErrorLabel.setVisible(false);
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (passwordErrorLabel.isVisible()) {
                    passwordErrorLabel.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (passwordErrorLabel.isVisible()) {
                    passwordErrorLabel.setVisible(false);
                }
            }
        });

        passwordVisibilityButton.setPreferredSize(new Dimension(40, 40));
        passwordVisibilityButton.setBackground(panelColor);
        passwordVisibilityButton.setFocusable(false);
        passwordVisibilityButton.setIcon(showIcon);
        passwordVisibilityButton.setBorder(null);
        passwordVisibilityButton.setToolTipText("Hiện mật khẩu");
        passwordVisibilityButton.addActionListener(l -> {
            if (!isPasswordVisible) {
                passwordTextField.setEchoChar('\u0000');
                passwordVisibilityButton.setIcon(hideIcon);
                passwordVisibilityButton.setToolTipText("Ẩn mật khẩu");
            } else {
                passwordTextField.setEchoChar('•');
                passwordVisibilityButton.setIcon(showIcon);
                passwordVisibilityButton.setToolTipText("Hiện mật khẩu");
            }
            isPasswordVisible = !isPasswordVisible;
        });

        passwordErrorLabel.setText("");
        passwordErrorLabel.setFont(errorFont);
        passwordErrorLabel.setForeground(Color.red);
        passwordErrorLabel.setVisible(false);

        // Footer section
        footerContainer.setLayout(new GridBagLayout());
        footerContainer.setBackground(Color.white);
        footerContainer.add(submitContainer, gbc);
        footerContainer.add(registerRedirectionContainer, gbc);

        submitContainer.setLayout(new BorderLayout());
        submitContainer.setPreferredSize(new Dimension(size.width - 50, 60));
        submitContainer.add(submitButton);

        submitButton.setText("Đăng nhập");
        submitButton.setFocusable(false);
        submitButton.setFont(labelFont);
        submitButton.setBackground(submitColor);
        submitButton.setForeground(Color.white);
        submitButton.setBorder(null);
        submitButton.addActionListener(l -> {
            boolean hasErrors = false;

            username = usernameTextField.getText();
            password = new String(passwordTextField.getPassword());
            String nameValidateResult = FieldValidator.validateUsername(username);
            String passwordValidateResult = FieldValidator.validatePassword(password);

            if (!nameValidateResult.isEmpty()) {
                if (nameValidateResult.contains("trống")) {
                    usernameErrorLabel.setPreferredSize(null);
                } else {
                    usernameErrorLabel.setPreferredSize(new Dimension(size.width - 100, 40));
                }
                usernameErrorLabel.setText("<html>*" + nameValidateResult + "</html>");
                usernameErrorLabel.setVisible(true);
                hasErrors = true;
            } else {
                usernameErrorLabel.setVisible(false);
            }

            if (!passwordValidateResult.isEmpty()) {
                passwordErrorLabel.setText("*" + passwordValidateResult);
                passwordErrorLabel.setVisible(true);
                hasErrors = true;
            } else {
                passwordErrorLabel.setVisible(false);
            }

            if (!hasErrors) {
                // String authenticatedResult =
                // Authentication.getInstance().authenticate(this.username, this.password);
                // if (authenticatedResult.isEmpty()) {
                // MainMenu mainMenu = (MainMenu)
                // AppFrame.getInstance().getContextPools().getContext("mainMenu");
                // mainMenu.loadUser();
                // this.reset();
                // this.switchContext("mainMenu");
                // } else {
                // JOptionPane.showMessageDialog(null, authenticatedResult, "Đăng nhập không
                // thành công",
                // JOptionPane.ERROR_MESSAGE);
                // }

                loginForm.setEnabled(false);
                countDownLatch = new CountDownLatch(1);
                PacketService.authentication(username, password);
                // loginForm.setEnabled(false);
                // try {
                // boolean success = countDownLatch.await(5, TimeUnit.SECONDS);
                // if (success) {
                // System.out.println("Ok!");
                // } else {
                // System.out.println("Not Ok!");
                // loginForm.setEnabled(true);
                // JOptionPane.showMessageDialog(null, "Server không phản hồi!", "Đăng nhập
                // không thành công",
                // JOptionPane.ERROR_MESSAGE);
                // }

                // } catch (InterruptedException e1) {
                // System.out.println("Errors happened");
                // e1.printStackTrace();
                // }

                new Thread(() -> {
                    try {
                        boolean success = countDownLatch.await(5, TimeUnit.SECONDS);

                        if (!success) {
                            SwingUtilities.invokeLater(() -> {
                                loginForm.setEnabled(true);
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Server không phản hồi!",
                                        "Đăng nhập không thành công",
                                        JOptionPane.ERROR_MESSAGE);
                            });
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        });

        registerRedirectionContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        registerRedirectionContainer.setPreferredSize(new Dimension(size.width - 50, 40));
        registerRedirectionContainer.setBackground(Color.white);
        registerRedirectionContainer.add(registerLabel);
        registerRedirectionContainer.add(registerRedirectionButton);

        registerLabel.setText("<html><body style='text-align:center'>Chưa có tài khoản?</body></html>");
        registerLabel.setFont(redirectLabelFont);
        registerLabel.setBackground(Color.white);
        registerLabel.setOpaque(true);

        registerRedirectionButton.setText("Đăng ký");
        registerRedirectionButton.setFont(buttonFont);
        registerRedirectionButton.setBackground(Color.white);
        registerRedirectionButton.setForeground(Color.blue);
        registerRedirectionButton.setFocusable(false);
        registerRedirectionButton.setBorder(null);
        registerRedirectionButton.addActionListener(l -> {
            this.reset();
            this.switchContext("registerForm");
        });

    }

    public void reset() {
        this.username = "";
        this.password = "";
        this.usernameTextField.setText("");
        this.usernameErrorLabel.setVisible(false);
        this.passwordTextField.setText("");
        this.passwordTextField.setEchoChar('•');
        this.passwordErrorLabel.setVisible(false);
        this.passwordVisibilityButton.setIcon(showIcon);
        this.isPasswordVisible = false;
        this.countDownLatch = new CountDownLatch(1);
    }

    public synchronized void getResponse(AuthResponse authResponse) {
        countDownLatch.countDown();
        loginForm.setEnabled(true);
        if (authResponse.getStatus().equals("success")) {
            MainMenu mainMenu = (MainMenu) AppFrame.getInstance().getContextPools().getContext("mainMenu");
            mainMenu.loadUser();
            this.reset();
            this.switchContext("mainMenu");
        } else {
            JOptionPane.showMessageDialog(null, authResponse.getLoginMessage(), "Đăng nhập không thành công",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void draw() {
        this.parent.setSize(this.getSize());
        this.parent.setMinimumSize(this.getSize());

        this.parent.add(this.loginForm);

        this.parent.revalidate();
        this.parent.repaint();
    }

    @Override
    public void switchContext(String newContext) {
        this.parent.remove(this.loginForm);
        AppFrame appFrame = AppFrame.getInstance();
        AppContext context = appFrame.getContextPools().getContext(newContext);
        this.parent.setMinimumSize(context.getSize());
        this.parent.setSize(context.getSize());
        // this.parent.setLayout(null);
        this.parent.add(context.getRootComponent());
        this.parent.revalidate();
        this.parent.repaint();
    }

    @Override
    public Component getRootComponent() {
        return this.loginForm;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(this.size.width, this.size.height + 35);
    }

}
