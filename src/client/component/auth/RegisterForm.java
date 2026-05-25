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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import component.PopupWindow;
import component.picker.DatePicker;
import constant.GenderEnum;
import domain.User;
import domain.dto.RegisterResponse;
import util.FieldValidator;
import util.PacketService;

public class RegisterForm implements AppContext {

    private Container parent;
    private final Dimension size = new Dimension(500, 960);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final Map<String, GenderEnum> genderMap1 = new HashMap<>(
            Map.of("Nam", GenderEnum.MALE, "Nữ", GenderEnum.FEMALE, "Khác", GenderEnum.OTHER));
    private User user;

    // Font & Color
    private Font welcomeLabelFont;
    private Font titleFont;
    private Font labelFont;
    private Font buttonFont;
    private Font textFont;
    private Font errorFont;
    private Color panelColor;
    private Color submitColor;
    private Color calendarButtonColor;

    // Root section
    private JPanel registerForm;

    // Header section
    private JPanel headerContainer;
    private JLabel welcomeLabel;
    private JLabel titleLabel;

    // Body section
    private JPanel bodyContainer;

    // Name input section
    private JPanel nameRootContainer;
    private JPanel nameTextContainer;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel nameErrorLabel;

    // Username input section
    private JPanel usernameRootContainer;
    private JPanel usernameTextContainer;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    private JLabel usernameErrorLabel;

    // Password input section
    private JPanel passwordRootContainer;
    private JPanel passwordTextContainer;
    private JLabel passwordLabel;
    private JPasswordField passwordTextField;
    private JLabel passwordErrorLabel;
    private ImageIcon showIcon;
    private ImageIcon hideIcon;
    private JButton passwordVisibilityButton;
    private boolean isPasswordVisible;

    // Email input section
    private JPanel emailRootContainer;
    private JPanel emailTextContainer;
    private JLabel emailLabel;
    private JTextField emailTextField;
    private JLabel emailErrorLabel;

    // Dob input section
    private JPanel dobRootContainer;
    private JPanel dobInputContainer;
    private JLabel dobLabel;
    private JTextField dobTextField;
    private JLabel dobErrorLabel;
    private JButton dobBrowsingButton;
    private ImageIcon calendarIcon;
    private LocalDate selectedDate;

    // Gender input section
    private JPanel genderRootContainer;
    private JLabel genderLabel;
    private JComboBox<String> genderComboBox;

    // Footer section
    private JPanel footerContainer;

    // Sign up section
    private JPanel submitContainer;
    private JButton submitButton;

    // Login redirection section
    private JPanel loginRedirectionContainer;
    private JLabel loginLabel;
    private JButton loginRedirectionButton;

    // Dob picker
    private Dimension windowSize;
    private PopupWindow dobPickerPopupWindow;
    private DatePicker dobPicker;

    private CountDownLatch countDownLatch;

    public RegisterForm(Container parent) {
        this.parent = parent;
        this.user = new User();

        // Font & Color
        welcomeLabelFont = new Font("Consolas", Font.BOLD, 30);
        titleFont = new Font("Consolas", Font.BOLD, 40);
        labelFont = new Font("Consolas", Font.BOLD, 25);
        buttonFont = new Font("Consolas", Font.BOLD, 20);
        textFont = new Font("Consolas", Font.PLAIN, 20);
        errorFont = new Font("Consolas", Font.ITALIC, 15);
        panelColor = new Color(245, 245, 245);
        submitColor = new Color(255, 153, 0);
        calendarButtonColor = new Color(219, 237, 243);

        // Root container initialization
        registerForm = new JPanel();

        // Header initialization
        headerContainer = new JPanel();
        welcomeLabel = new JLabel();
        titleLabel = new JLabel();

        // Body initialization
        bodyContainer = new JPanel();

        // Name initialization
        nameRootContainer = new JPanel();
        nameTextContainer = new JPanel();
        nameLabel = new JLabel();
        nameTextField = new JTextField();
        nameErrorLabel = new JLabel();

        // Username initialization
        usernameRootContainer = new JPanel();
        usernameTextContainer = new JPanel();
        usernameLabel = new JLabel();
        usernameTextField = new JTextField();
        usernameErrorLabel = new JLabel();

        // Password initialization
        passwordRootContainer = new JPanel();
        passwordTextContainer = new JPanel();
        passwordLabel = new JLabel();
        passwordTextField = new JPasswordField();
        passwordVisibilityButton = new JButton();
        showIcon = new ImageIcon("assets/eye.png");
        hideIcon = new ImageIcon("assets/eye-off.png");
        passwordErrorLabel = new JLabel();
        isPasswordVisible = false;

        // Email initialization
        emailRootContainer = new JPanel();
        emailTextContainer = new JPanel();
        emailLabel = new JLabel();
        emailTextField = new JTextField();
        emailErrorLabel = new JLabel();

        // Date of birth initialization
        dobRootContainer = new JPanel();
        dobInputContainer = new JPanel();
        dobLabel = new JLabel();
        dobTextField = new JTextField();
        dobErrorLabel = new JLabel();
        dobBrowsingButton = new JButton();
        calendarIcon = new ImageIcon("assets/calendar-days.png");
        selectedDate = null;

        // Gender initialization
        genderRootContainer = new JPanel();
        genderLabel = new JLabel();
        String[] genders = { "Nam", "Nữ", "Khác" };
        genderComboBox = new JComboBox<>(genders);

        // Footer initialization
        footerContainer = new JPanel();

        // Submit initialization
        submitContainer = new JPanel();
        submitButton = new JButton();

        // Login redirection initialization
        loginRedirectionContainer = new JPanel();
        loginLabel = new JLabel();
        loginRedirectionButton = new JButton();

        // Select date of birth
        windowSize = new Dimension(500, 520);
        dobPickerPopupWindow = new PopupWindow(windowSize, "Calendar");

        dobPicker = new DatePicker(dobPickerPopupWindow.getRootComponent());

        dobPicker.setSubmitAction(l -> {
            selectedDate = dobPicker.submit();
            if (selectedDate != null) {
                dobTextField.setText(selectedDate.format(dateTimeFormatter));
                dobPickerPopupWindow.close();
            }
        });

        dobPicker.setCancelAction(l -> {
            dobPickerPopupWindow.close();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        GridBagConstraints gbc1 = new GridBagConstraints();
        // gbc1.insets = new Insets(2, 0, 2, 0);
        gbc1.anchor = GridBagConstraints.WEST;
        gbc1.gridwidth = GridBagConstraints.REMAINDER;

        // Root section
        registerForm.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        registerForm.setLayout(new GridBagLayout());
        registerForm.setPreferredSize(this.size);
        registerForm.setBackground(Color.white);
        registerForm.add(headerContainer, gbc);
        registerForm.add(bodyContainer, gbc);
        registerForm.add(footerContainer, gbc);

        // Header section
        headerContainer.setLayout(new GridBagLayout());
        headerContainer.setPreferredSize(new Dimension(size.width - 10, 60));
        headerContainer.setBackground(Color.white);
        // headerContainer.add(welcomeLabel, gbc1);
        headerContainer.add(titleLabel, gbc1);

        welcomeLabel.setText(
                "<html><body style='text-align:center'>Chào mừng đến với ứng dụng quản lý thư viện</body></html>");
        welcomeLabel.setPreferredSize(new Dimension(size.width - 10, 100));
        welcomeLabel.setFont(welcomeLabelFont);
        welcomeLabel.setForeground(Color.black);

        titleLabel.setText("Đăng ký");
        titleLabel.setPreferredSize(new Dimension(size.width - 10, 60));
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(Color.black);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Body section
        bodyContainer.setLayout(new GridBagLayout());
        // bodyContainer.setPreferredSize(new Dimension(size.width - 10, size.height -
        // 300));
        bodyContainer.setBackground(Color.white);
        bodyContainer.add(nameRootContainer, gbc);
        bodyContainer.add(usernameRootContainer, gbc);
        bodyContainer.add(passwordRootContainer, gbc);
        bodyContainer.add(emailRootContainer, gbc);
        bodyContainer.add(dobRootContainer, gbc);
        bodyContainer.add(genderRootContainer, gbc);

        // Name section
        nameRootContainer.setLayout(new GridBagLayout());
        nameRootContainer.setBackground(panelColor);
        nameRootContainer.add(nameLabel, gbc1);
        nameRootContainer.add(nameTextContainer, gbc1);
        nameRootContainer.add(nameErrorLabel, gbc1);

        nameTextContainer.setLayout(new BorderLayout());
        nameTextContainer.setPreferredSize(new Dimension(size.width - 50, 40));
        nameTextContainer.setBackground(panelColor);
        nameTextContainer.setBorder(new EmptyBorder(0, 5, 10, 5));
        nameTextContainer.add(nameTextField);

        nameLabel.setText("Họ và tên");
        nameLabel.setFont(labelFont);
        nameLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        nameTextField.setFont(textFont);
        nameTextField.setBackground(Color.white);
        nameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                if (nameErrorLabel.isVisible()) {
                    nameErrorLabel.setVisible(false);
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (nameErrorLabel.isVisible()) {
                    nameErrorLabel.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (nameErrorLabel.isVisible()) {
                    nameErrorLabel.setVisible(false);
                }
            }
        });

        nameErrorLabel.setText("");
        nameErrorLabel.setFont(errorFont);
        nameErrorLabel.setForeground(Color.red);
        nameErrorLabel.setVisible(false);

        // Username section
        usernameRootContainer.setLayout(new GridBagLayout());
        usernameRootContainer.setBackground(panelColor);
        usernameRootContainer.add(usernameLabel, gbc1);
        usernameRootContainer.add(usernameTextContainer, gbc1);
        usernameRootContainer.add(usernameErrorLabel, gbc1);

        usernameTextContainer.setLayout(new BorderLayout());
        usernameTextContainer.setPreferredSize(new Dimension(size.width - 50, 40));
        usernameTextContainer.setBackground(panelColor);
        usernameTextContainer.setBorder(new EmptyBorder(0, 5, 10, 5));
        usernameTextContainer.add(usernameTextField);

        usernameLabel.setText("Username");
        usernameLabel.setFont(labelFont);
        usernameLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

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
        passwordRootContainer.setLayout(new GridBagLayout());
        passwordRootContainer.setBackground(panelColor);
        passwordRootContainer.add(passwordLabel, gbc1);
        passwordRootContainer.add(passwordTextContainer, gbc1);
        passwordRootContainer.add(passwordErrorLabel, gbc1);

        passwordLabel.setText("Mật khẩu");
        passwordLabel.setFont(labelFont);
        passwordLabel.setBorder(new EmptyBorder(5, 5, 0, 5));

        passwordTextContainer.setLayout(new FlowLayout());
        passwordTextContainer.setPreferredSize(new Dimension(size.width - 50, 50));
        passwordTextContainer.setBackground(new Color(245, 245, 245));
        passwordTextContainer.add(passwordTextField);
        passwordTextContainer.add(passwordVisibilityButton);

        passwordTextField.setPreferredSize(new Dimension(size.width - 110, 30));
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

        // Email section
        emailRootContainer.setLayout(new GridBagLayout());
        emailRootContainer.setBackground(panelColor);
        emailRootContainer.add(emailLabel, gbc1);
        emailRootContainer.add(emailTextContainer, gbc1);
        emailRootContainer.add(emailErrorLabel, gbc1);

        emailTextContainer.setLayout(new BorderLayout());
        emailTextContainer.setPreferredSize(new Dimension(size.width - 50, 40));
        emailTextContainer.setBackground(panelColor);
        emailTextContainer.setBorder(new EmptyBorder(0, 5, 10, 5));
        emailTextContainer.add(emailTextField);

        emailLabel.setText("Email (tùy chọn)");
        emailLabel.setFont(labelFont);
        emailLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        emailTextField.setFont(textFont);
        emailTextField.setBackground(Color.white);
        emailTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                if (emailErrorLabel.isVisible()) {
                    emailErrorLabel.setVisible(false);
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (emailErrorLabel.isVisible()) {
                    emailErrorLabel.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (emailErrorLabel.isVisible()) {
                    emailErrorLabel.setVisible(false);
                }
            }
        });

        emailErrorLabel.setText("");
        emailErrorLabel.setFont(errorFont);
        emailErrorLabel.setForeground(Color.red);
        emailErrorLabel.setVisible(false);

        // Date of birth section
        dobRootContainer.setLayout(new GridBagLayout());
        dobRootContainer.setBackground(panelColor);
        dobRootContainer.add(dobLabel, gbc1);
        dobRootContainer.add(dobInputContainer, gbc1);
        dobRootContainer.add(dobErrorLabel, gbc1);

        dobInputContainer.setLayout(new FlowLayout());
        dobInputContainer.setPreferredSize(new Dimension(size.width - 50, 50));
        dobInputContainer.setBackground(panelColor);
        dobInputContainer.add(dobTextField);
        dobInputContainer.add(dobBrowsingButton);

        dobLabel.setText("Ngày sinh");
        dobLabel.setFont(labelFont);
        dobLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        dobTextField.setPreferredSize(new Dimension(size.width - 110, 35));
        dobTextField.setFont(textFont);
        dobTextField.setBackground(Color.white);
        dobTextField.setEditable(false);
        dobTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent e) {
                if (dobErrorLabel.isVisible()) {
                    dobErrorLabel.setVisible(false);
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (dobErrorLabel.isVisible()) {
                    dobErrorLabel.setVisible(false);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (dobErrorLabel.isVisible()) {
                    dobErrorLabel.setVisible(false);
                }
            }
        });

        dobBrowsingButton.setPreferredSize(new Dimension(35, 35));
        dobBrowsingButton.setIcon(calendarIcon);
        dobBrowsingButton.setBackground(calendarButtonColor);
        dobBrowsingButton.setFocusable(false);
        dobBrowsingButton.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        dobBrowsingButton.addActionListener(l -> {
            dobPicker.setDateRangeEnabled(null, LocalDate.now()); // Dob cannot greater than current day
            dobPicker.revalidate(selectedDate);
            dobPicker.draw();
            dobPickerPopupWindow.draw();
        });

        dobErrorLabel.setText("");
        dobErrorLabel.setFont(errorFont);
        dobErrorLabel.setForeground(Color.red);
        dobErrorLabel.setVisible(false);

        // Gender section
        genderRootContainer.setLayout(new FlowLayout(FlowLayout.LEADING));
        genderRootContainer.setPreferredSize(new Dimension(size.width - 50, 60));
        genderRootContainer.setBackground(panelColor);
        genderRootContainer.add(genderLabel, gbc1);
        genderRootContainer.add(genderComboBox, gbc1);

        genderLabel.setText("Giới tính");
        genderLabel.setFont(labelFont);
        genderLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        genderComboBox.setFont(textFont);

        // Footer section
        footerContainer.setLayout(new GridBagLayout());
        footerContainer.setBackground(Color.white);
        footerContainer.add(submitContainer, gbc);
        footerContainer.add(loginRedirectionContainer, gbc);

        submitContainer.setLayout(new BorderLayout());
        submitContainer.setPreferredSize(new Dimension(size.width - 50, 60));
        submitContainer.add(submitButton);

        submitButton.setText("Đăng ký");
        submitButton.setFocusable(false);
        submitButton.setFont(labelFont);
        submitButton.setBackground(submitColor);
        submitButton.setForeground(Color.white);
        submitButton.setBorder(null);
        submitButton.addActionListener(l -> {

            boolean hasError = false;

            String password = new String(passwordTextField.getPassword());
            String nameValidateResult = FieldValidator.validateName(nameTextField.getText());
            String usernameValidateResult = FieldValidator.validateUsername(usernameTextField.getText());
            String passwordValidateResult = FieldValidator.validatePassword(password.strip());
            String emailValidateResult = FieldValidator.validateEmail(emailTextField.getText());

            if (!nameValidateResult.isEmpty()) {
                nameErrorLabel.setText("*" + nameValidateResult);
                nameErrorLabel.setVisible(true);
                hasError = true;
            } else {
                nameErrorLabel.setVisible(false);
            }

            if (!usernameValidateResult.isEmpty()) {
                if (usernameValidateResult.contains("trống")) {
                    usernameErrorLabel.setPreferredSize(null);
                } else {
                    usernameErrorLabel.setPreferredSize(new Dimension(size.width - 100, 40));
                }
                usernameErrorLabel.setText("<html>*" + usernameValidateResult + "</html>");
                usernameErrorLabel.setVisible(true);
                hasError = true;
            } else {
                usernameErrorLabel.setVisible(false);
            }

            if (!passwordValidateResult.isEmpty()) {
                passwordErrorLabel.setText("*" + passwordValidateResult);
                passwordErrorLabel.setVisible(true);
                hasError = true;
            } else {
                passwordErrorLabel.setVisible(false);
            }

            if (!emailTextField.getText().isBlank() && !emailValidateResult.isEmpty()) {
                emailErrorLabel.setText("*" + emailValidateResult);
                emailErrorLabel.setVisible(true);
                hasError = true;
            } else {
                emailErrorLabel.setVisible(false);
            }

            if (selectedDate == null) {
                dobErrorLabel.setText("*Ngày sinh không được để trống.");
                dobErrorLabel.setVisible(true);
                hasError = true;
            } else {
                dobErrorLabel.setVisible(false);
            }

            if (!hasError) {
                user.setName(nameTextField.getText().strip());
                user.setUsername(usernameTextField.getText().strip());
                user.setPassword(password.strip());
                user.setEmail(emailTextField.getText().strip());
                user.setDob(selectedDate);
                user.setGender(genderMap1.get(genderComboBox.getSelectedItem()));
                user.setCreatedAt(LocalDate.now());

                registerForm.setEnabled(false);

                countDownLatch = new CountDownLatch(1);

                PacketService.registration(user);

                new Thread(() -> {
                    try {
                        boolean success = countDownLatch.await(5, TimeUnit.SECONDS);

                        if (!success) {
                            SwingUtilities.invokeLater(() -> {
                                registerForm.setEnabled(true);
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Server không phản hồi!",
                                        "Đăng ký không thành công",
                                        JOptionPane.ERROR_MESSAGE);
                            });
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

                // String registerResult = register();

                // if (registerResult.isEmpty()) {
                // Authentication.getInstance().authenticate(usernameTextField.getText().strip(),
                // password.strip());
                // this.reset();
                // this.switchContext("loginForm");
                // } else {
                // JOptionPane.showMessageDialog(null, registerResult, "Đăng ký không thành
                // công",
                // JOptionPane.ERROR_MESSAGE);
                // }
            }
        });

        loginRedirectionContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        loginRedirectionContainer.setPreferredSize(new Dimension(size.width - 50, 40));
        loginRedirectionContainer.setBackground(Color.white);
        loginRedirectionContainer.add(loginLabel);
        loginRedirectionContainer.add(loginRedirectionButton);

        loginLabel.setText("<html><body style='text-align:center'>Đã có tài khoản?</body></html>");
        loginLabel.setFont(textFont);
        loginLabel.setBackground(Color.white);
        loginLabel.setOpaque(true);

        loginRedirectionButton.setText("Đăng nhập");
        loginRedirectionButton.setFont(buttonFont);
        loginRedirectionButton.setBackground(Color.white);
        loginRedirectionButton.setForeground(Color.blue);
        loginRedirectionButton.setFocusable(false);
        loginRedirectionButton.setBorder(null);
        loginRedirectionButton.addActionListener(l -> {
            this.reset();
            this.switchContext("loginForm");
        });
    }

    public synchronized void getResponse(RegisterResponse registerResponse) {
        countDownLatch.countDown();
        registerForm.setEnabled(true);
        if (registerResponse.getStatus().equals("success")) {
            this.reset();
            this.switchContext("loginForm");
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    registerResponse.getMessage(),
                    "Đăng ký không thành công",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public void reset() {
        this.user = new User();
        this.nameTextField.setText("");
        this.nameErrorLabel.setVisible(false);
        this.usernameTextField.setText("");
        this.usernameErrorLabel.setVisible(false);
        this.passwordTextField.setText("");
        this.passwordTextField.setEchoChar('•');
        this.passwordErrorLabel.setVisible(false);
        this.passwordVisibilityButton.setIcon(showIcon);
        this.isPasswordVisible = false;
        this.emailTextField.setText("");
        this.emailErrorLabel.setVisible(false);
        this.dobTextField.setText("");
        this.dobErrorLabel.setVisible(false);
        this.genderComboBox.setSelectedIndex(0);
    }

    @Override
    public void draw() {
        this.parent.setSize(this.getSize());
        this.parent.setMinimumSize(this.getSize());
        this.parent.add(this.registerForm);
    }

    @Override
    public void switchContext(String newContext) {
        this.parent.remove(this.registerForm);

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
        return this.registerForm;
    }

    @Override
    public Dimension getSize() {
        return new Dimension((int) this.size.getWidth(), (int) this.size.getHeight() + 35);
    }
}
