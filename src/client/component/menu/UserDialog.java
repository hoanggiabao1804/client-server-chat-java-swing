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
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import component.AppContext;
import component.AppFrame;
import component.PopupWindow;
import domain.Dialog;
import domain.FileMessage;
import domain.IconMessage;
import domain.Message;
import domain.TextMessage;
import domain.User;
import domain.UserMetadata;
import domain.dto.DeleteMessageRequest;
import domain.dto.DeleteMessageResponse;
import domain.dto.DialogContentResponse;
import domain.dto.FileDownloadAck;
import domain.dto.FileDownloadRequest;
import domain.dto.FileTransferResponse;
import domain.dto.SendMessageRequest;
import domain.dto.SendMessageResponse;
import util.LocalStorage;
import util.PacketService;

public class UserDialog implements AppContext {
    private Container parent;
    private Dimension size;

    private User userLogin;
    private Dialog dialog;
    private final JFileChooser fileChooser;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd 'Tháng' MM, yyyy");
    private final long MAX_FILE_SIZE = 20971520L;
    private final Map<String, JPanel> messageBubbleMap = new ConcurrentHashMap<>();

    // Font & Color
    private Font headerFont;
    private Font textFont;
    private Color messageColor;

    // Containers section
    private JPanel rootContainer;
    private JPanel headerContainer;
    private JPanel bodyContainer;
    private JPanel footerContainer;

    // Header section
    private JPanel headerUserInfoContainer;
    private RoundButton userAvatar;
    private ImageIcon userAvatarIcon;
    private JLabel usernameLabel;

    // Body section
    private JScrollPane bodyScrollPane;
    private List<JPanel> messageBubbleList;
    // private JPanel selectedMessageBubble;

    // Footer section
    private JButton fileUploadButton;
    private JTextField messageInputField;
    private JButton sendButton;
    private ImageIcon fileUploadIcon;
    private ImageIcon sendIcon;
    private ImageIcon likeIcon;

    private Dimension informationWindowSize;
    private PopupWindow informationWindow;
    private InformationDialog informationDialog;

    private final Map<String, CountDownLatch> pendingObjects = new ConcurrentHashMap<>();

    public UserDialog(Container parent, Dimension size) {
        this.parent = parent;
        this.size = size;
        this.fileChooser = new JFileChooser();
        this.fileChooser.setDialogTitle("Select file to upload");

        // Font & Color
        this.headerFont = new Font("Consolas", Font.BOLD, 20);
        this.textFont = new Font("Consolas", Font.PLAIN, 15);
        this.messageColor = new Color(53, 155, 164);

        // Containers initialization
        rootContainer = new JPanel();
        headerContainer = new JPanel();
        bodyContainer = new JPanel();
        footerContainer = new JPanel();

        // Header initialization
        headerUserInfoContainer = new JPanel();
        userAvatar = new RoundButton();
        userAvatarIcon = new ImageIcon("assets/user-round.png");
        usernameLabel = new JLabel();

        // Body initialization
        bodyScrollPane = new JScrollPane(bodyContainer);

        messageBubbleList = null;
        // selectedMessageBubble = null;

        // Footer initialization
        fileUploadButton = new JButton();
        messageInputField = new JTextField();
        sendButton = new JButton();
        fileUploadIcon = new ImageIcon("assets/file-image.png");
        sendIcon = new ImageIcon("assets/send-horizontal.png");
        likeIcon = new ImageIcon("assets/thumbs-up.png");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Root section
        rootContainer.setLayout(new GridBagLayout());
        rootContainer.setPreferredSize(size);
        rootContainer.setBackground(Color.blue);
        rootContainer.add(headerContainer, gbc);
        rootContainer.add(bodyScrollPane, gbc);
        rootContainer.add(footerContainer, gbc);

        // Header section
        headerContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerContainer.setPreferredSize(new Dimension(size.width - 10, 70));
        headerContainer.setBackground(Color.orange);
        headerContainer.add(headerUserInfoContainer);

        headerUserInfoContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerUserInfoContainer.setPreferredSize(new Dimension(300, 60));
        headerUserInfoContainer.setBackground(Color.orange);
        headerUserInfoContainer.add(userAvatar);
        headerUserInfoContainer.add(usernameLabel);

        informationWindowSize = new Dimension(600, 600);
        informationWindow = new PopupWindow(informationWindowSize, "Thành viên");
        informationDialog = new InformationDialog(informationWindow.getRootComponent());
        informationDialog.setCloseAction(l -> {
            informationWindow.close();
        });

        userAvatar.setIcon(userAvatarIcon);
        userAvatar.setPreferredSize(new Dimension(50, 50));
        userAvatar.setFocusable(false);
        userAvatar.addActionListener(l -> {
            System.out.println("View user profile...");
            informationDialog.loadDialog(dialog);
            informationDialog.draw();
            informationWindow.draw();
        });

        usernameLabel.setFont(headerFont);

        // Body section
        bodyContainer.setLayout(new BoxLayout(bodyContainer, BoxLayout.Y_AXIS));
        bodyContainer.setBackground(Color.white);

        // messageBubbleList.forEach(bodyContainer::add);

        bodyScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        bodyScrollPane.setLayout(new ScrollPaneLayout());
        bodyScrollPane.setPreferredSize(new Dimension(size.width - 10, size.height - 130));
        bodyScrollPane.setBackground(Color.lightGray);

        // Footer section
        footerContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        footerContainer.setPreferredSize(new Dimension(size.width - 10, 50));
        footerContainer.setBackground(Color.orange);
        footerContainer.add(fileUploadButton);
        footerContainer.add(messageInputField);
        footerContainer.add(sendButton);

        fileUploadButton.setIcon(fileUploadIcon);
        fileUploadButton.setPreferredSize(new Dimension(40, 40));
        fileUploadButton.setFocusable(false);
        fileUploadButton.addActionListener(l -> {
            System.out.println("Add new file:");

            int returnVal = fileChooser.showOpenDialog(rootContainer);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                if (selectedFile.length() > MAX_FILE_SIZE) {
                    JOptionPane.showMessageDialog(
                            null,
                            "Chỉ có thể gửi được file với kích thước tối đa 20MB!",
                            "File quá lớn",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Process the selected file

                FileMessage fileMessage = new FileMessage(UUID.randomUUID().toString(),
                        dialog.getId(),
                        selectedFile.getName(), selectedFile.length(), userLogin.getId(), null,
                        null,
                        LocalDateTime.now(),
                        "sent");

                // Required an save action here:

                PacketService
                        .sendMessage(new SendMessageRequest(userLogin.getId(), dialog.getId(), fileMessage));

                PacketService.sendFile(selectedFile, fileMessage);

                // JPanel fileWrapper = createMessageWrapper(fileMessage);
                // bodyContainer.add(fileWrapper);
                // bodyContainer.revalidate();
                // bodyContainer.repaint();
            }

        });

        messageInputField.setPreferredSize(new Dimension(size.width - 120, 40));
        messageInputField.setFont(textFont);
        // messageInputField.addKeyListener(new KeyAdapter() {
        // public void keyTyped(KeyEvent e) {
        // System.out.println("Input: '" + messageInputField.getText() + "'.");
        // }
        // });
        messageInputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateText();
            }

            public void removeUpdate(DocumentEvent e) {
                updateText();
            }

            public void changedUpdate(DocumentEvent e) {
                updateText();
            }

            private void updateText() {
                String fullText = messageInputField.getText();
                System.out.println("Input: '" + fullText + "'.");

                sendButton.setIcon(fullText.isBlank() ? likeIcon : sendIcon);
            }
        });
        messageInputField.addActionListener(l -> {
            System.out.println("Input: '" + messageInputField.getText() + "'.");

            if (!messageInputField.getText().isBlank()) {
                TextMessage textMessage = new TextMessage(UUID.randomUUID().toString(), dialog.getId(),
                        messageInputField.getText().strip(), userLogin.getId(), null, LocalDateTime.now(),
                        "sent");

                CountDownLatch countDownLatch = new CountDownLatch(1);
                pendingObjects.put(textMessage.getId(), countDownLatch);

                PacketService
                        .sendMessage(new SendMessageRequest(userLogin.getId(), dialog.getId(), textMessage));

                new Thread(() -> {
                    try {
                        boolean success = countDownLatch.await(5, TimeUnit.SECONDS);

                        if (!success) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Gửi tin nhắn không thành công",
                                        "Server không phản hồi!",
                                        JOptionPane.ERROR_MESSAGE);
                            });
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

                JPanel textWrapper = createMessageWrapper(textMessage);
                bodyContainer.add(textWrapper);
                bodyContainer.revalidate();
                bodyContainer.repaint();

                messageInputField.setText("");
            }
        });

        sendButton.setIcon(likeIcon);
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setFocusable(false);
        sendButton.addActionListener(l -> {
            System.out.println("Input: '" + messageInputField.getText() + "'.");
            CountDownLatch countDownLatch = new CountDownLatch(1);

            if (!messageInputField.getText().isBlank()) {
                TextMessage textMessage = new TextMessage(UUID.randomUUID().toString(), dialog.getId(),
                        messageInputField.getText().strip(), userLogin.getId(), null, LocalDateTime.now(),
                        "sent");
                pendingObjects.put(textMessage.getId(), countDownLatch);
                PacketService
                        .sendMessage(new SendMessageRequest(userLogin.getId(), dialog.getId(), textMessage));

                JPanel textWrapper = createMessageWrapper(textMessage);
                bodyContainer.add(textWrapper);
                bodyContainer.revalidate();
                bodyContainer.repaint();

                messageInputField.setText("");
            } else {
                IconMessage iconMessage = new IconMessage(UUID.randomUUID().toString(), dialog.getId(), "👍",
                        userLogin.getId(), null, "assets/like.png", LocalDateTime.now(), "sent");
                pendingObjects.put(iconMessage.getId(), countDownLatch);
                PacketService
                        .sendMessage(new SendMessageRequest(userLogin.getId(), dialog.getId(), iconMessage));

                JPanel iconWrapper = createMessageWrapper(iconMessage);
                bodyContainer.add(iconWrapper);
                bodyContainer.revalidate();
                bodyContainer.repaint();
            }

            new Thread(() -> {
                try {
                    boolean success = countDownLatch.await(5, TimeUnit.SECONDS);

                    if (!success) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Gửi tin nhắn không thành công",
                                    "Server không phản hồi!",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private JPanel createMessageWrapper(Message message) {
        boolean isMine = message.getSenderId().equals(userLogin.getId());

        JPanel wrapper = new JPanel(new BorderLayout());
        messageBubbleMap.put(message.getId(), wrapper);
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        Dimension itemSize = null;
        JComponent item = null;

        if (message instanceof TextMessage) {
            // new Color(0, 120, 255)
            item = new MessageItem(message.getContent(), messageColor);
        } else if (message instanceof FileMessage) {
            FileMessage fileMessage = (FileMessage) message;
            ImageIcon fileIcon = new ImageIcon("assets/file.png");
            item = new FileMessageItem(fileMessage.getFileName(),
                    formatFileSize(fileMessage.getFileSize()), fileIcon);

            if (!fileMessage.getTag().equals("sent")) {
                item.setEnabled(false);
            }

            item.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Download file...");

                    JFileChooser chooser = new JFileChooser();

                    chooser.setDialogTitle("Chọn nơi lưu file");
                    chooser.setFileSelectionMode(
                            JFileChooser.DIRECTORIES_ONLY);

                    int returnValue = chooser.showOpenDialog(rootContainer);

                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File folder = chooser.getSelectedFile();

                        File outputFile = getAvailableFile(
                                folder,
                                fileMessage.getFileName());

                        String localFilePath = outputFile.getPath();

                        PacketService.downloadFile(new FileDownloadRequest(dialog.getId(), message.getId(),
                                fileMessage.getFileName(), localFilePath));
                    }
                }
            });
        } else if (message instanceof IconMessage) {
            IconMessage iconMessage = (IconMessage) message;
            item = new IconMessageItem(iconMessage.getIconPath(), 48, null);
        }

        JPanel actionPanel = createMessageActionPanel(wrapper, item, message, isMine);

        setActionButtonsVisible(actionPanel, false);

        actionPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        item.setAlignmentY(Component.CENTER_ALIGNMENT);

        item.setToolTipText(message.getTimestamp().format(dateTimeFormatter));
        itemSize = item.getPreferredSize();

        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));

        if (isMine) {
            rowPanel.add(actionPanel);
            rowPanel.add(Box.createHorizontalStrut(6));
            rowPanel.add(item);

            wrapper.add(rowPanel, BorderLayout.EAST);
        } else {
            JComponent avatar = createSenderAvatar(message);

            avatar.setAlignmentY(Component.BOTTOM_ALIGNMENT);
            item.setAlignmentY(Component.BOTTOM_ALIGNMENT);
            actionPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

            rowPanel.add(avatar);
            rowPanel.add(Box.createHorizontalStrut(6));
            rowPanel.add(item);
            rowPanel.add(Box.createHorizontalStrut(6));
            rowPanel.add(actionPanel);

            wrapper.add(rowPanel, BorderLayout.WEST);
        }

        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setActionButtonsVisible(actionPanel, true);
                wrapper.revalidate();
                wrapper.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Point p = SwingUtilities.convertPoint(
                        e.getComponent(),
                        e.getPoint(),
                        wrapper);

                if (!wrapper.contains(p)) {
                    setActionButtonsVisible(actionPanel, false);
                    wrapper.revalidate();
                    wrapper.repaint();
                }
            }
        };

        wrapper.addMouseListener(hoverAdapter);
        rowPanel.addMouseListener(hoverAdapter);
        actionPanel.addMouseListener(hoverAdapter);

        Dimension rowSize = rowPanel.getPreferredSize();

        wrapper.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                rowSize.height + 10));

        wrapper.setPreferredSize(new Dimension(
                bodyContainer.getPreferredSize().width,
                rowSize.height + 10));

        return wrapper;
    }

    private JPanel createMessageActionPanel(JPanel wrapper, JComponent originMessageItem, Message originMessage,
            boolean isMine) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(60, 32));
        panel.setMinimumSize(new Dimension(60, 32));
        panel.setMaximumSize(new Dimension(60, 32));

        JButton deleteButton = new JButton();
        ImageIcon deleteIcon = new ImageIcon("assets/trash.png");
        JButton hideButton = new JButton();
        ImageIcon hideIcon = new ImageIcon("assets/eye-off.png");
        ImageIcon showIcon = new ImageIcon("assets/eye.png");

        deleteButton.setIcon(deleteIcon);
        deleteButton.setToolTipText("Xóa tin nhắn");
        deleteButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                deleteButton.setContentAreaFilled(true);
                deleteButton.setBackground(Color.lightGray);
            }

            public void mouseExited(MouseEvent e) {
                deleteButton.setContentAreaFilled(false);
            }
        });
        deleteButton.addActionListener(l -> {
            if (JOptionPane.showConfirmDialog(null,
                    "Bạn có chắc muốn xóa tin nhắn đã chọn (tin nhắn sẽ không thể khôi phục)?", "Xóa tin nhắn?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                System.out.println("Deleted message.");
                CountDownLatch countDownLatch = new CountDownLatch(1);
                pendingObjects.put(originMessage.getId(), countDownLatch);
                PacketService.deleteMessage(
                        new DeleteMessageRequest(userLogin.getId(), dialog.getId(), originMessage));

                // bodyContainer.remove(wrapper);
                wrapper.setVisible(false);
                bodyContainer.revalidate();
                bodyContainer.repaint();

                new Thread(() -> {
                    try {
                        boolean success = countDownLatch.await(5, TimeUnit.SECONDS);

                        if (!success) {
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(
                                        null,
                                        "Gửi tin nhắn không thành công",
                                        "Server không phản hồi!",
                                        JOptionPane.ERROR_MESSAGE);
                            });
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            // Delete in database;
        });

        MessageItem hiddenMessageItem = new MessageItem("Đã ẩn tin nhắn", Color.lightGray);
        hideButton.setIcon(hideIcon);
        hideButton.setToolTipText("Ẩn tin nhắn");
        hideButton.addActionListener(l -> {
            JPanel rowPanel = (JPanel) wrapper.getComponent(0);

            final int messageItemIndex = 2;

            if (hideButton.getIcon() == hideIcon) {
                hideButton.setIcon(showIcon);
                hideButton.setToolTipText("Hiện tin nhắn");

                hiddenMessageItem.setAlignmentY(
                        isMine
                                ? Component.CENTER_ALIGNMENT
                                : Component.BOTTOM_ALIGNMENT);

                hiddenMessageItem.setToolTipText(
                        originMessage.getTimestamp().format(dateTimeFormatter));

                // // Gắn hover listener giống message cũ
                // for (MouseAdapter adapter : new MouseAdapter[] {}) {
                // // không cần đoạn này nếu bạn gắn listener vào wrapper + actionPanel
                // }

                rowPanel.remove(messageItemIndex);
                rowPanel.add(hiddenMessageItem, messageItemIndex);

                updateWrapperHeight(wrapper, rowPanel);

            } else {
                hideButton.setIcon(hideIcon);
                hideButton.setToolTipText("Ẩn tin nhắn");

                originMessageItem.setAlignmentY(
                        isMine
                                ? Component.CENTER_ALIGNMENT
                                : Component.BOTTOM_ALIGNMENT);

                rowPanel.remove(messageItemIndex);
                rowPanel.add(originMessageItem, messageItemIndex);

                updateWrapperHeight(wrapper, rowPanel);
            }

            rowPanel.revalidate();
            rowPanel.repaint();

            wrapper.revalidate();
            wrapper.repaint();

            bodyContainer.revalidate();
            bodyContainer.repaint();
        });
        hideButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                hideButton.setContentAreaFilled(true);
                hideButton.setBackground(Color.lightGray);
            }

            public void mouseExited(MouseEvent e) {
                hideButton.setContentAreaFilled(false);
            }
        });

        for (JButton button : List.of(deleteButton, hideButton)) {
            button.setFocusable(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);

            button.setPreferredSize(new Dimension(28, 28));
            button.setMinimumSize(new Dimension(28, 28));
            button.setMaximumSize(new Dimension(28, 28));
        }

        if (isMine) {
            panel.add(deleteButton);
        }
        panel.add(hideButton);

        return panel;
    }

    private JComponent createSenderAvatar(Message message) {
        RoundButton avatar = new RoundButton();

        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setMinimumSize(new Dimension(32, 32));
        avatar.setMaximumSize(new Dimension(32, 32));
        avatar.setFocusable(false);

        ImageIcon avatarIcon = new ImageIcon("assets/user-round.png");
        avatar.setIcon(avatarIcon);

        String senderName = getSenderDisplayName(message.getSenderId());
        avatar.setToolTipText(senderName);

        return avatar;
    }

    private void setActionButtonsVisible(JPanel actionPanel, boolean visible) {
        for (Component c : actionPanel.getComponents()) {
            c.setVisible(visible);
        }

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private String getSenderDisplayName(String senderId) {
        UserMetadata user = LocalStorage.getUserById(senderId);

        if (user != null) {
            return user.getName();
        }

        return senderId;
    }

    private void updateWrapperHeight(JPanel wrapper, JPanel rowPanel) {
        Dimension rowSize = rowPanel.getPreferredSize();

        wrapper.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                rowSize.height + 10));

        wrapper.setPreferredSize(new Dimension(
                bodyContainer.getPreferredSize().width,
                rowSize.height + 10));
    }

    public void loadUser() {
        userLogin = LocalStorage.getUserLogin();
    }

    public void loadDialog(Dialog dialog) {
        this.dialog = dialog;

        if (dialog.getType().equals("private")) {
            userAvatarIcon = new ImageIcon("assets/user-lock.png");
        } else if (dialog.getType().equals("direct")) {
            userAvatarIcon = new ImageIcon("assets/user-round.png");
        } else {
            userAvatarIcon = new ImageIcon("assets/users-round.png");
        }

        userAvatar.setIcon(userAvatarIcon);

        if (dialog.getMessages() == null) {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            pendingObjects.put(dialog.getId(), countDownLatch);

            PacketService.loadDialogContent(dialog.getId());

            new Thread(() -> {
                try {
                    boolean success = countDownLatch.await(5, TimeUnit.SECONDS);

                    if (!success) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "Tải dữ liệu hội thoại không thành công.",
                                    "Server không phản hồi!",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            messageBubbleList = dialog.getMessages().stream()
                    .map(msg -> createMessageWrapper(msg))
                    .collect(Collectors.toList());

            String dialogName = dialog.getName();
            if (dialog.getType().equals("direct")) {
                for (String userId : dialog.getParticipants()) {
                    if (!userId.equals(userLogin.getId())) {
                        dialogName = LocalStorage.getUserById(userId).getName();
                        break;
                    }
                }
            } else if (dialog.getType().equals("private")) {
                dialogName = LocalStorage.getUserById(dialog.getParticipants().get(0)).getName();
            }

            usernameLabel.setText(dialogName);

            bodyContainer.removeAll();
            messageBubbleList.forEach(bodyContainer::add);
            bodyContainer.revalidate();
            bodyContainer.repaint();
        }
    }

    public synchronized void getResponse(DialogContentResponse dialogContentResponse) {

        String dialogId = dialogContentResponse.getDialogId();
        CountDownLatch countDownLatch = pendingObjects.remove(dialogId);

        if (countDownLatch != null) {
            countDownLatch.countDown();
        }

        if (dialogContentResponse.getStatus().equals("success")) {
            messageBubbleList = dialog.getMessages().stream()
                    .map(msg -> createMessageWrapper(msg))
                    .collect(Collectors.toList());

            String dialogName = dialog.getName();
            if (dialog.getType().equals("direct")) {
                for (String userId : dialog.getParticipants()) {
                    if (!userId.equals(userLogin.getId())) {
                        dialogName = LocalStorage.getUserById(userId).getName();
                        break;
                    }
                }
            } else if (dialog.getType().equals("private")) {
                dialogName = LocalStorage.getUserById(dialog.getParticipants().get(0)).getName();
            }

            usernameLabel.setText(dialogName);

            bodyContainer.removeAll();
            messageBubbleList.forEach(bodyContainer::add);
            bodyContainer.revalidate();
            bodyContainer.repaint();
        } else {
            JOptionPane.showMessageDialog(null, dialogContentResponse.getMessage(),
                    "Lấy nội dung đoạn hội thoạt thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public synchronized void getResponse(SendMessageResponse sendMessageResponse) {
        String messageId = sendMessageResponse.getMessagePersisted().getId();
        CountDownLatch countDownLatch = pendingObjects.remove(messageId);

        if (countDownLatch != null) {
            countDownLatch.countDown();
        }

        JPanel wrapper = null;
        if (!messageBubbleMap.containsKey(messageId)) {
            wrapper = createMessageWrapper(sendMessageResponse.getMessagePersisted());
            messageBubbleMap.put(messageId, wrapper);
            messageBubbleList.add(wrapper);
            bodyContainer.add(wrapper);
            bodyContainer.revalidate();
            bodyContainer.repaint();
        } else {
            wrapper = (JPanel) messageBubbleMap.get(messageId);
        }

        JPanel rowPanel = (JPanel) wrapper.getComponent(0);
        JComponent item = (JComponent) rowPanel.getComponent(2);

        if (sendMessageResponse.getStatus().equals("success")) {
            // System.out.println("Called ");
        } else {
            item.setBackground(Color.red);
            JOptionPane.showMessageDialog(null, sendMessageResponse.getMessage(),
                    "Gửi tin nhắn thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }

        rowPanel.repaint();
        wrapper.repaint();
        bodyContainer.revalidate();
        bodyContainer.repaint();
    }

    public synchronized void getResponse(FileTransferResponse fileTransferResponse) {
        String messageId = fileTransferResponse.getMessageId();
        CountDownLatch countDownLatch = pendingObjects.remove(messageId);

        if (countDownLatch != null) {
            countDownLatch.countDown();
        }

        if (fileTransferResponse.getStatus().equals("success")) {
            System.out.println("Progress: " + fileTransferResponse.getSentBytes() + "/"
                    + fileTransferResponse.getFileSize() + "bytes");

            if (fileTransferResponse.getSentBytes() >= fileTransferResponse.getFileSize()) {
                JOptionPane.showMessageDialog(null,
                        "File " + fileTransferResponse.getFileName() + " đã được gửi thành công tới đích.",
                        "Gửi file thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, fileTransferResponse.getMessage(),
                    "Gửi file thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public synchronized void getResponse(FileDownloadAck fileDownloadAck) {

        // String messageId = fileDownloadAck.getMessageId();
        // CountDownLatch countDownLatch = pendingObjects.remove(messageId);

        // if (countDownLatch != null) {
        // countDownLatch.countDown();
        // }

        if (fileDownloadAck.getStatus().equals("success")) {
            System.out.println("Progress: " + fileDownloadAck.getSentBytes() + "/"
                    + fileDownloadAck.getFileSize() + "bytes");

            if (fileDownloadAck.getSentBytes() >= fileDownloadAck.getFileSize()) {
                JOptionPane.showMessageDialog(null,
                        "File " + fileDownloadAck.getFileName() + " đã được tải xuống thành công.",
                        "Tải file thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, fileDownloadAck.getMessage(),
                    "Tải file thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public synchronized void getResponse(DeleteMessageResponse deleteMessageResponse) {
        String messageId = deleteMessageResponse.getMessageDeleted().getId();
        CountDownLatch countDownLatch = pendingObjects.remove(messageId);

        if (countDownLatch != null) {
            countDownLatch.countDown();
        }

        if (deleteMessageResponse.getStatus().equals("success")) {
            JPanel messageBubble = messageBubbleMap.remove(messageId);
            messageBubbleList.remove(messageBubble);

            bodyContainer.remove(messageBubble);
            bodyContainer.revalidate();
            bodyContainer.repaint();

        } else {
            JPanel messageBubble = messageBubbleMap.get(messageId);
            if (!messageBubble.isVisible()) {
                messageBubble.setVisible(true);
                bodyContainer.revalidate();
                bodyContainer.repaint();
            }

            JOptionPane.showMessageDialog(null, deleteMessageResponse.getMessage(),
                    "Xóa tin nhắn thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private File getAvailableFile(File folder, String fileName) {
        File file = new File(folder, fileName);

        if (!file.exists()) {
            return file;
        }

        String name = fileName;
        String extension = "";

        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex > 0) {
            name = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }

        int count = 1;

        do {
            file = new File(folder, name + " (" + count + ")" + extension);
            count++;
        } while (file.exists());

        return file;
    }

    // public void revalidateData(List<User> newDialogUsers, List<String>
    // newDialogMessages) {
    // this.dialogMessages = newDialogMessages;
    // messageBubbleList = dialogMessages.stream()
    // .map(msg -> createMessageWrapper(new MessageItem(msg, new Color(0, 120,
    // 255)),
    // new Random().nextBoolean(), false))
    // .toList();
    // bodyContainer.removeAll();
    // messageBubbleList.forEach(bodyContainer::add);
    // bodyContainer.revalidate();
    // bodyContainer.repaint();
    // }

    private String formatFileSize(long bytes) {
        String[] units = { "B", "KB", "MB", "GB" };
        double size = bytes;

        int unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        if (size % 1 == 0) {
            return String.format("%.0f %s", size, units[unitIndex]);
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    @Override
    public void draw() {
        this.parent.setSize(this.size);
        this.parent.add(this.rootContainer);
    }

    @Override
    public void switchContext(String newContext) {
        this.parent.remove(this.rootContainer);

        AppFrame appFrame = AppFrame.getInstance();
        AppContext context = appFrame.getContextPools().getContext(newContext);
        this.parent.setSize(context.getSize());
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
        return this.size;
    }
}
