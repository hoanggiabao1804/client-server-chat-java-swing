package component.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import domain.dto.UserUpdateResponse;
import util.FieldValidator;
import util.LocalStorage;
import util.PacketService;

public class UserProfile implements AppContext {
    private final Container parent;
    private Dimension size = new Dimension(760, 560);
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final Map<String, GenderEnum> genderMap1 = new HashMap<>(
            Map.of("Nam", GenderEnum.MALE, "Nữ", GenderEnum.FEMALE, "Khác", GenderEnum.OTHER));

    private final Map<GenderEnum, String> genderMap2 = new HashMap<>(
            Map.of(GenderEnum.MALE, "Nam", GenderEnum.FEMALE, "Nữ", GenderEnum.OTHER, "Khác"));

    // Font & Color
    private final Font labelFont;
    private final Font textFont;
    private final Font errorFont;
    private final Color panelColor;
    private final Color headerColor;
    private final Color buttonColor;

    // Data source
    private User userLogin;

    // Containers section
    private JPanel rootContainer;
    private JPanel headerContainer;
    private JPanel bodyContainer;

    // Header section
    private JPanel headerLabelPanel;
    private JButton backButton;
    private ImageIcon backIcon;
    private JLabel headerLabel;
    private JPanel updateProfilePanel;
    private JButton updateProfileButton;
    private JButton saveProfileButton;
    private JButton cancelUpdateButton;
    private boolean isEditable;

    // Body section

    // User ID
    private JPanel userIdRootContainer;
    private JLabel userIdLabel;
    private JLabel userIdTextLabel;

    // Name
    private JPanel nameRootContainer;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private JLabel nameErrorLabel;

    // Username
    private JPanel usernameRootContainer;
    private JLabel usernameLabel;
    private JLabel usernameTextLabel;

    // Password
    private JPanel passwordRootContainer;
    private JPanel passwordInputPanel;
    private JLabel passwordLabel;
    private JPasswordField passwordTextField;
    private JLabel passwordErrorLabel;
    private JButton passwordVisibilityButton;
    private ImageIcon eyeIcon;
    private ImageIcon eyeOffIcon;
    private boolean isPasswordVisible;

    // Email
    private JPanel emailRootContainer;
    private JLabel emailLabel;
    private JTextField emailTextField;
    private JLabel emailErrorLabel;

    // Date of birth
    private JPanel dobRootContainer;
    private JPanel dobPanel;
    private JLabel dobLabel;
    private JTextField dobTextField;
    private JLabel dobErrorLabel;
    private JButton dobBrowsingButton;
    private ImageIcon calendarIcon;
    private LocalDate selectedDob;

    // Gender
    private JPanel genderRootContainer;
    private JPanel genderInputPanel;
    private JLabel genderLabel;
    private final String[] genders = { "Nam", "Nữ", "Khác" };
    private JComboBox<String> genderComboBox;
    private JLabel genderValueLabel;

    // Created at
    private JPanel createdAtRootContainer;
    private JLabel createdAtLabel;
    private JLabel createdAtTextLabel;

    // Date of birth picker
    private Dimension windowSize;
    private PopupWindow dobPickerPopupWindow;
    private DatePicker dobPicker;

    private CountDownLatch countDownLatch;

    public UserProfile(Container parent) {
        this.parent = parent;
        // this.size = size;

        // Font & Color
        this.labelFont = new Font("Consolas", Font.BOLD, 20);
        this.textFont = new Font("Consolas", Font.PLAIN, 15);
        this.errorFont = new Font("Consolas", Font.ITALIC, 15);
        this.panelColor = new Color(245, 245, 245);
        this.headerColor = new Color(0, 129, 138);
        this.buttonColor = new Color(219, 237, 243);

        // Containers initialization
        rootContainer = new JPanel();
        headerContainer = new JPanel();
        bodyContainer = new JPanel();

        // Header initialization
        headerLabelPanel = new JPanel();
        backButton = new JButton();
        backIcon = new ImageIcon("assets/arrow-left.png");
        headerLabel = new JLabel();
        updateProfilePanel = new JPanel();
        updateProfileButton = new JButton();
        saveProfileButton = new JButton();
        cancelUpdateButton = new JButton();
        isEditable = false;

        // Body initialization
        // User ID
        userIdRootContainer = new JPanel();
        userIdLabel = new JLabel();
        userIdTextLabel = new JLabel();

        // Name initialization
        nameRootContainer = new JPanel();
        nameLabel = new JLabel();
        nameTextField = new JTextField();
        nameErrorLabel = new JLabel();

        // Username initialization
        usernameRootContainer = new JPanel();
        usernameLabel = new JLabel();
        usernameTextLabel = new JLabel();

        // Password initialization
        passwordRootContainer = new JPanel();
        passwordInputPanel = new JPanel();
        passwordLabel = new JLabel();
        passwordTextField = new JPasswordField();
        passwordErrorLabel = new JLabel();
        passwordVisibilityButton = new JButton();
        eyeIcon = new ImageIcon("assets/eye.png");
        eyeOffIcon = new ImageIcon("assets/eye-off.png");
        isPasswordVisible = false;

        // Email
        emailRootContainer = new JPanel();
        emailLabel = new JLabel();
        emailTextField = new JTextField();
        emailErrorLabel = new JLabel();

        // Date of birth
        dobRootContainer = new JPanel();
        dobPanel = new JPanel();
        dobLabel = new JLabel();
        dobTextField = new JTextField();
        dobErrorLabel = new JLabel();
        dobBrowsingButton = new JButton();
        calendarIcon = new ImageIcon("assets/calendar-days.png");
        selectedDob = this.userLogin != null ? this.userLogin.getDob() : null;

        // Gender
        genderRootContainer = new JPanel();
        genderInputPanel = new JPanel();
        genderLabel = new JLabel();
        genderComboBox = new JComboBox<>(genders);
        genderValueLabel = new JLabel();

        // Created at
        createdAtRootContainer = new JPanel();
        createdAtLabel = new JLabel();
        createdAtTextLabel = new JLabel();

        // Dob picker
        windowSize = new Dimension(500, 520);
        dobPickerPopupWindow = new PopupWindow(windowSize, "Ngày sinh");

        dobPicker = new DatePicker(dobPickerPopupWindow.getRootComponent());

        dobPicker.setSubmitAction(l -> {
            selectedDob = dobPicker.submit();
            if (selectedDob != null) {
                dobTextField.setText(selectedDob.format(dateTimeFormatter));
                dobPickerPopupWindow.close();
            }
        });

        dobPicker.setCancelAction(l -> {
            dobPickerPopupWindow.close();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        // gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        GridBagConstraints gbc1 = new GridBagConstraints();
        // gbc1.insets = new Insets(2, 0, 2, 0);
        gbc1.anchor = GridBagConstraints.WEST;
        gbc1.gridwidth = GridBagConstraints.REMAINDER;

        // Root section
        rootContainer.setLayout(new FlowLayout());
        rootContainer.setPreferredSize(size);
        rootContainer.setBackground(Color.gray);
        rootContainer.add(headerContainer);
        rootContainer.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        rootContainer.add(bodyContainer);

        // Header section
        headerContainer.setLayout(new FlowLayout(FlowLayout.LEADING));
        headerContainer.setPreferredSize(new Dimension(size.width - 10, 60));
        headerContainer.setBackground(headerColor);
        headerContainer.add(headerLabelPanel);
        headerContainer.add(updateProfilePanel);

        headerLabelPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 10));
        headerLabelPanel.setPreferredSize(new Dimension((int) (size.width * 0.6) - 15, 60));
        headerLabelPanel.setBackground(headerColor);
        headerLabelPanel.add(backButton);
        headerLabelPanel.add(headerLabel);

        backButton.setText("Quay lại");
        backButton.setIcon(backIcon);
        backButton.setFont(textFont);
        backButton.setFocusable(false);
        backButton.addActionListener(l -> {
            if (isEditable) {
                int optionValue = JOptionPane.showConfirmDialog(null,
                        "Thay đổi chưa được lưu. Bạn có chắc muốn thoát", "Chưa lưu thay đổi",
                        JOptionPane.YES_NO_OPTION);
                if (optionValue != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            this.reset();
            this.switchContext("mainMenu");
        });

        headerLabel.setText("Thông tin tài khoản");
        headerLabel.setFont(new Font("Consolas", Font.BOLD, 25));
        headerLabel.setForeground(Color.white);
        headerLabel.setBorder(new EmptyBorder(0, 10, 0, 0));

        updateProfilePanel.setLayout(new FlowLayout(FlowLayout.TRAILING, 10, 10));
        updateProfilePanel.setPreferredSize(new Dimension((int) (size.width * 0.4) - 15, 60));
        updateProfilePanel.setBackground(headerColor);
        updateProfilePanel.add(saveProfileButton);
        updateProfilePanel.add(cancelUpdateButton);
        updateProfilePanel.add(updateProfileButton);

        saveProfileButton.setText("Lưu");
        saveProfileButton.setFont(textFont);
        saveProfileButton.setFocusable(false);
        saveProfileButton.setEnabled(false);
        saveProfileButton.addActionListener(l -> {
            this.save();
        });

        cancelUpdateButton.setText("Hủy");
        cancelUpdateButton.setFont(textFont);
        cancelUpdateButton.setFocusable(false);
        cancelUpdateButton.setEnabled(false);
        cancelUpdateButton.addActionListener(l -> {
            this.reset();
        });

        updateProfileButton.setText("Sửa");
        updateProfileButton.setFont(textFont);
        updateProfileButton.setFocusable(false);
        updateProfileButton.addActionListener(l -> {
            this.changeEditingState(isEditable = true);
        });

        // Body section
        bodyContainer.setLayout(new FlowLayout(FlowLayout.LEADING));
        bodyContainer.setPreferredSize(new Dimension(size.width - 10, size.height - 80));
        bodyContainer.setBackground(panelColor);
        bodyContainer.add(userIdRootContainer);
        bodyContainer.add(nameRootContainer);
        bodyContainer.add(usernameRootContainer);
        bodyContainer.add(passwordRootContainer);
        bodyContainer.add(emailRootContainer);
        bodyContainer.add(dobRootContainer);
        bodyContainer.add(genderRootContainer);
        bodyContainer.add(createdAtRootContainer);

        // User ID section
        userIdRootContainer.setLayout(new GridBagLayout());
        userIdRootContainer.setPreferredSize(new Dimension(size.width / 2 - 10, 115));
        userIdRootContainer.setBackground(panelColor);
        userIdRootContainer.add(userIdLabel, gbc1);
        userIdRootContainer.add(userIdTextLabel, gbc1);

        userIdLabel.setText("Mã người dùng");
        userIdLabel.setFont(labelFont);
        userIdLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        userIdTextLabel.setText("");
        userIdTextLabel.setFont(new Font("Consolas", Font.BOLD, 15));

        // Name section
        nameRootContainer.setLayout(new GridBagLayout());
        nameRootContainer.setPreferredSize(new Dimension(size.width / 2 - 10, 115));
        nameRootContainer.setBackground(panelColor);
        nameRootContainer.add(nameLabel, gbc1);
        nameRootContainer.add(nameTextField, gbc1);
        nameRootContainer.add(nameErrorLabel, gbc1);

        nameLabel.setText("Họ và tên");
        nameLabel.setFont(labelFont);
        nameLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        nameTextField.setPreferredSize(new Dimension(size.width / 2 - 80, 30));
        nameTextField.setText("");
        nameTextField.setFont(textFont);
        nameTextField.setBackground(panelColor);
        nameTextField.setEditable(false);
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
        nameErrorLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        nameErrorLabel.setVisible(false);

        // Username section
        usernameRootContainer.setLayout(new GridLayout(2, 1, 0, -60));
        usernameRootContainer.setPreferredSize(new Dimension(size.width / 2 - 10, 115));
        usernameRootContainer.setBackground(panelColor);
        usernameRootContainer.add(usernameLabel);
        usernameRootContainer.add(usernameTextLabel);

        usernameLabel.setText("Tên đăng nhập");
        usernameLabel.setFont(labelFont);
        usernameLabel.setBorder(new EmptyBorder(0, 35, 0, 0));

        usernameTextLabel.setText("");
        usernameTextLabel.setFont(new Font("Consolas", Font.BOLD, 15));
        usernameTextLabel.setBorder(new EmptyBorder(0, 35, 0, 0));

        // Password section
        passwordRootContainer.setLayout(new GridBagLayout());
        passwordRootContainer.setPreferredSize(new Dimension(size.width / 2 - 10, 115));
        passwordRootContainer.setBackground(panelColor);
        passwordRootContainer.add(passwordLabel, gbc1);
        passwordRootContainer.add(passwordInputPanel, gbc1);
        passwordRootContainer.add(passwordErrorLabel, gbc1);

        passwordInputPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 0));
        passwordInputPanel.setPreferredSize(new Dimension(size.width / 2 - 70, 30));
        passwordInputPanel.setBackground(panelColor);
        passwordInputPanel.add(passwordTextField);
        passwordInputPanel.add(passwordVisibilityButton);

        passwordLabel.setText("Mật khẩu");
        passwordLabel.setFont(labelFont);
        passwordLabel.setBorder(new EmptyBorder(0, 5, 5, 0));

        passwordTextField.setText("");
        passwordTextField.setPreferredSize(new Dimension(size.width / 2 - 110, 30));
        passwordTextField.setFont(textFont);
        passwordTextField.setBackground(panelColor);
        passwordTextField.setEditable(false);
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

        passwordVisibilityButton.setIcon(eyeIcon);
        passwordVisibilityButton.setBackground(panelColor);
        passwordVisibilityButton.setFocusable(false);
        passwordVisibilityButton.setBorder(null);
        passwordVisibilityButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isPasswordVisible) {
                    passwordTextField.setEchoChar('\u0000');
                    passwordVisibilityButton.setIcon(eyeOffIcon);
                    passwordVisibilityButton.setToolTipText("Hide");
                } else {
                    passwordTextField.setEchoChar('•');
                    passwordVisibilityButton.setIcon(eyeIcon);
                    passwordVisibilityButton.setToolTipText("Show");
                }
                isPasswordVisible = !isPasswordVisible;
            }
        });

        passwordErrorLabel.setText("");
        passwordErrorLabel.setFont(errorFont);
        passwordErrorLabel.setForeground(Color.red);
        passwordErrorLabel.setBorder(new EmptyBorder(5, 5, 0, 0));
        passwordErrorLabel.setVisible(false);

        // Email section
        emailRootContainer.setLayout(new GridBagLayout());
        emailRootContainer.setPreferredSize(new Dimension(size.width / 2 - 10, 115));
        emailRootContainer.setBackground(panelColor);
        emailRootContainer.add(emailLabel, gbc1);
        emailRootContainer.add(emailTextField, gbc1);
        emailRootContainer.add(emailErrorLabel, gbc1);

        emailLabel.setText("Email");
        emailLabel.setFont(labelFont);
        emailLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        emailTextField.setPreferredSize(new Dimension(size.width / 2 - 80, 30));
        emailTextField.setText("");
        emailTextField.setFont(textFont);
        emailTextField.setBackground(panelColor);
        emailTextField.setEditable(false);
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
        emailErrorLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        emailErrorLabel.setVisible(false);

        // Dob section
        dobRootContainer.setLayout(new GridBagLayout());
        dobRootContainer.setPreferredSize(new Dimension(size.width / 2 - 10, 115));
        dobRootContainer.setBackground(panelColor);
        dobRootContainer.add(dobLabel, gbc1);
        dobRootContainer.add(dobPanel, gbc1);
        dobRootContainer.add(dobErrorLabel, gbc1);

        dobPanel.setLayout(new FlowLayout());
        // dobPanel.setPreferredSize(new Dimension(size.width / 2 - 10, 115));
        dobPanel.setBackground(panelColor);
        dobPanel.add(dobTextField);
        dobPanel.add(dobBrowsingButton);

        dobLabel.setText("Ngày sinh");
        dobLabel.setFont(labelFont);
        dobLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        dobTextField.setPreferredSize(new Dimension(size.width / 2 - 120, 30));
        dobTextField.setText("");
        dobTextField.setFont(textFont);
        dobTextField.setBackground(panelColor);
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
        dobBrowsingButton.setBackground(buttonColor);
        dobBrowsingButton.setFocusable(false);
        dobBrowsingButton.setEnabled(false);
        dobBrowsingButton.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        dobBrowsingButton.addActionListener(l -> {
            dobPicker.setDateRangeEnabled(null, LocalDate.now()); // Dob cannot greater than current day
            dobPicker.revalidate(selectedDob);
            dobPicker.draw();
            dobPickerPopupWindow.draw();
        });

        dobErrorLabel.setText("");
        dobErrorLabel.setFont(errorFont);
        dobErrorLabel.setForeground(Color.red);
        dobErrorLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        dobErrorLabel.setVisible(false);

        // Gender section
        genderRootContainer.setLayout(new FlowLayout(FlowLayout.LEADING, 30, 40));
        genderRootContainer.setPreferredSize(new Dimension(size.width / 2 - 10, 115));
        genderRootContainer.setBackground(panelColor);
        genderRootContainer.add(genderLabel);
        genderRootContainer.add(genderValueLabel);

        genderLabel.setText("Giới tính");
        genderLabel.setFont(labelFont);

        genderInputPanel.setLayout(new BorderLayout());
        genderInputPanel.setPreferredSize(new Dimension(size.width / 4 - 60, 30));
        genderInputPanel.add(genderComboBox);

        genderValueLabel.setText("");
        genderValueLabel.setFont(labelFont);

        genderComboBox.setFont(textFont);
        genderComboBox.setEditable(false);

        // Created At section
        createdAtRootContainer.setLayout(new GridBagLayout());
        createdAtRootContainer.setPreferredSize(new Dimension(size.width / 2 - 10, 115));
        createdAtRootContainer.setBackground(panelColor);
        createdAtRootContainer.add(createdAtLabel, gbc1);
        createdAtRootContainer.add(createdAtTextLabel, gbc1);

        createdAtLabel.setText("Ngày tạo");
        createdAtLabel.setPreferredSize(new Dimension(size.width / 2 - 80, 30));
        createdAtLabel.setHorizontalTextPosition(JLabel.LEADING);
        createdAtLabel.setFont(labelFont);
        createdAtLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        createdAtTextLabel.setText("");
        createdAtTextLabel.setFont(new Font("Consolas", Font.BOLD, 15));
    }

    public void loadUser() {
        this.userLogin = LocalStorage.getUserLogin();

        this.userIdTextLabel.setText(userLogin.getId());
        this.nameTextField.setText(userLogin.getName());
        this.usernameTextLabel.setText(userLogin.getUsername());
        this.passwordTextField.setText(userLogin.getPassword());
        this.emailTextField.setText(userLogin.getEmail());
        this.dobTextField.setText(userLogin.getDob().format(dateTimeFormatter));
        this.genderComboBox.setSelectedItem(genderMap2.get(userLogin.getGender()));
        this.genderValueLabel.setText(genderMap2.get(userLogin.getGender()));
        this.createdAtTextLabel.setText(userLogin.getCreatedAt().format(dateTimeFormatter));

        selectedDob = userLogin.getDob();
    }

    public void reset() {
        this.saveProfileButton.setEnabled(false);
        this.cancelUpdateButton.setEnabled(false);
        this.updateProfileButton.setEnabled(true);

        this.nameTextField.setText(this.userLogin.getName());
        this.nameTextField.setEditable(false);
        this.nameErrorLabel.setVisible(false);

        this.passwordTextField.setText(this.userLogin.getPassword());
        this.passwordTextField.setEchoChar('•');
        this.passwordTextField.setEditable(false);
        this.passwordVisibilityButton.setIcon(eyeIcon);
        this.isPasswordVisible = false;
        this.passwordErrorLabel.setVisible(false);

        this.emailTextField.setText(this.userLogin.getEmail());
        this.emailTextField.setEditable(false);
        this.emailErrorLabel.setVisible(false);

        this.dobTextField.setText(this.userLogin.getDob().format(dateTimeFormatter));
        this.dobBrowsingButton.setEnabled(false);
        this.dobErrorLabel.setVisible(false);

        this.genderComboBox.setSelectedItem(genderMap2.get(this.userLogin.getGender()));
        this.genderRootContainer.remove(genderInputPanel);
        this.genderRootContainer.add(genderValueLabel);
        this.genderRootContainer.revalidate();
        this.genderRootContainer.repaint();

        this.isEditable = false;
    }

    private void changeEditingState(boolean isEnableEditing) {
        this.saveProfileButton.setEnabled(isEnableEditing);
        this.cancelUpdateButton.setEnabled(isEnableEditing);
        this.updateProfileButton.setEnabled(!isEnableEditing);

        this.nameTextField.setEditable(isEnableEditing);

        this.passwordTextField.setEditable(isEnableEditing);

        this.emailTextField.setEditable(isEnableEditing);

        this.dobBrowsingButton.setEnabled(isEnableEditing);
        // this.genderComboBox.setSelectedItem(genderMap2.get(this.user.getGender()));

        if (isEnableEditing) {
            this.genderRootContainer.remove(genderValueLabel);
            this.genderRootContainer.add(genderInputPanel);
        } else {
            this.genderRootContainer.remove(genderInputPanel);
            this.genderRootContainer.add(genderValueLabel);
        }
        this.genderRootContainer.revalidate();
        this.genderRootContainer.repaint();

        this.isEditable = isEnableEditing;
    }

    private void save() {
        boolean hasErrors = false;
        String password = (new String(this.passwordTextField.getPassword())).strip();
        String nameValidateResult = FieldValidator.validateName(this.nameTextField.getText().strip());
        String passwordValidateResult = FieldValidator.validatePassword(password);
        String emailValidateResult = FieldValidator.validateEmail(emailTextField.getText().strip());

        if (!nameValidateResult.isEmpty()) {
            nameErrorLabel.setText("*" + nameValidateResult);
            nameErrorLabel.setVisible(true);
            hasErrors = true;
        } else {
            nameErrorLabel.setVisible(false);
        }

        if (!passwordValidateResult.isEmpty()) {
            passwordErrorLabel.setText("*" + passwordValidateResult);
            passwordErrorLabel.setVisible(true);
            hasErrors = true;
        } else {
            passwordErrorLabel.setVisible(false);
        }

        if (!emailTextField.getText().isBlank() && !emailValidateResult.isEmpty()) {
            emailErrorLabel.setText("*" + emailValidateResult);
            emailErrorLabel.setVisible(true);
            hasErrors = true;
        } else {
            emailErrorLabel.setVisible(false);
        }

        if (selectedDob == null) {
            dobErrorLabel.setText("*Ngày sinh không được để trống.");
            dobErrorLabel.setVisible(true);
            hasErrors = true;
        } else {
            dobErrorLabel.setVisible(false);
        }

        if (!hasErrors) {
            User updatedUser = new User(
                    userLogin.getId(),
                    this.nameTextField.getText().strip(),
                    this.userLogin.getUsername(),
                    password,
                    this.emailTextField.getText().strip(),
                    this.selectedDob,
                    genderMap1.get(this.genderComboBox.getSelectedItem()));

            countDownLatch = new CountDownLatch(1);

            PacketService.updateUserInformation(updatedUser);

            new Thread(() -> {
                try {
                    boolean success = countDownLatch.await(5, TimeUnit.SECONDS);

                    if (!success) {
                        AppFrame appFrame = AppFrame.getInstance();

                        appFrame.reset();

                        AppContext loginContext = appFrame.getContextPools().getContext("loginForm");

                        appFrame.setMinimumSize(loginContext.getSize());
                        appFrame.setSize(loginContext.getSize());

                        loginContext.draw();

                        appFrame.getContentPane().revalidate();
                        appFrame.getContentPane().repaint();

                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Cập nhật thông tin người dùng không thành công",
                                    "Server không phản hồi!",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            // MainMenu mainMenu = (MainMenu)
            // AppFrame.getInstance().getContextPools().getContext("mainMenu");
            // mainMenu.loadUser();

            this.changeEditingState(isEditable = false);
        }
    }

    public synchronized void getResponse(UserUpdateResponse userUpdateResponse) {
        countDownLatch.countDown();

        if (userUpdateResponse.getStatus().equals("success")) {
            System.out.println("Update info successfully.");
        } else {
            JOptionPane.showMessageDialog(null, userUpdateResponse.getMessage(),
                    "Cập nhật thông tin người dùng thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void draw() {
        this.parent.setSize(this.getSize());
        this.parent.setMinimumSize(this.getSize());
        this.parent.add(this.rootContainer);
    }

    @Override
    public void switchContext(String newContext) {
        this.parent.remove(this.rootContainer);
        AppFrame appFrame = AppFrame.getInstance();
        AppContext context = appFrame.getContextPools().getContext(newContext);
        this.parent.setSize(context.getSize());
        this.parent.setMinimumSize(context.getSize());
        this.parent.add(context.getRootComponent());
        this.parent.revalidate();
        this.parent.repaint();
    }

    @Override
    public Component getRootComponent() {
        return this.rootContainer;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(this.size.width, this.size.height + 35);
    }

}
