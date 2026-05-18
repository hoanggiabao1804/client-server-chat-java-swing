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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;

import component.AppContext;
import component.AppFrame;
import domain.User;

public class UserDialog implements AppContext {
    private Container parent;
    private Dimension size;

    private final List<User> dialogUsers;
    private final JFileChooser fileChooser;

    // Font & Color
    private Font headerFont;
    private Font textFont;

    // Containers section
    private JPanel rootContainer;
    private JPanel headerContainer;
    private JPanel bodyContainer;
    private JPanel footerContainer;

    // Heade section
    private JPanel headerUserInfoContainer;
    private RoundButton userAvatar;
    private ImageIcon userAvatarIcon;
    private JLabel usernameLabel;

    // Body section
    private JScrollPane bodyScrollPane;
    private List<JPanel> messageBubbleList;
    private JPanel selectedMessageBubble;

    // Footer section
    private JButton fileUploadButton;
    private JTextField messageInputField;
    private JButton sendButton;
    private ImageIcon fileUploadIcon;
    private ImageIcon sendIcon;
    private ImageIcon likeIcon;

    private List<String> dialogMessages = List.of(
            "Hello, how are you?",
            "I'm good, thanks! How about you?",
            "Doing well, just working on a project.",
            "That's great to hear! What kind of project is it?",
            "It's a chat application using Java Swing.",
            "Wow, that sounds interesting! Good luck with it!",
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            "?");

    public UserDialog(Container parent, Dimension size, List<User> dialogUsers) {
        this.parent = parent;
        this.size = size;
        this.dialogUsers = dialogUsers;
        this.fileChooser = new JFileChooser();
        this.fileChooser.setDialogTitle("Select file to upload");

        // Font & Color
        this.headerFont = new Font("Consolas", Font.BOLD, 20);
        this.textFont = new Font("Consolas", Font.PLAIN, 15);

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
        messageBubbleList = dialogMessages.stream()
                .map(msg -> createMessageWrapper(new MessageItem(msg, new Color(0, 120, 255)),
                        new Random().nextBoolean(), false))
                .toList();
        selectedMessageBubble = null;

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
        headerUserInfoContainer.setPreferredSize(new Dimension(500, 60));
        headerUserInfoContainer.setBackground(Color.white);
        headerUserInfoContainer.add(userAvatar);
        headerUserInfoContainer.add(usernameLabel);

        userAvatar.setIcon(userAvatarIcon);
        userAvatar.setPreferredSize(new Dimension(50, 50));
        userAvatar.setFocusable(false);

        usernameLabel.setText(dialogUsers.get(0).getUsername());
        usernameLabel.setFont(headerFont);

        // Body section
        bodyContainer.setLayout(new BoxLayout(bodyContainer, BoxLayout.Y_AXIS));
        bodyContainer.setBackground(Color.white);

        messageBubbleList.forEach(bodyContainer::add);

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

                // Process the selected file
                FileMessageItem fileMsg = new FileMessageItem(
                        selectedFile.getName(),
                        formatFileSize(selectedFile.length()),
                        new ImageIcon("assets/file.png"));

                JPanel fileWrapper = createMessageWrapper(fileMsg, false, true);
                bodyContainer.add(fileWrapper);
                bodyContainer.revalidate();
                bodyContainer.repaint();
            }

        });

        messageInputField.setPreferredSize(new Dimension(size.width - 120, 40));
        messageInputField.setFont(textFont);
        messageInputField.addActionListener(l -> {
            System.out.println("Input: '" + messageInputField.getText() + "'.");
        });

        sendButton.setIcon(sendIcon);
        sendButton.setPreferredSize(new Dimension(40, 40));
        sendButton.setFocusable(false);
    }

    private JPanel createMessageWrapper(Object item, boolean isMine, boolean isFile) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // wrapper.setBackground(Color.green);

        wrapper.add(isFile ? (FileMessageItem) item : (MessageItem) item,
                isMine ? BorderLayout.EAST : BorderLayout.WEST);

        Dimension itemSize = (isFile ? (FileMessageItem) item : (MessageItem) item).getPreferredSize();

        wrapper.setMaximumSize(new Dimension(
                Integer.MAX_VALUE,
                itemSize.height + 10));

        wrapper.setPreferredSize(new Dimension(
                bodyContainer.getPreferredSize().width,
                itemSize.height + 10));

        if (isFile) {
            ((FileMessageItem) item).addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Open file:");
                }
            });
        }

        return wrapper;
    }

    public void revalidateData(List<User> newDialogUsers, List<String> newDialogMessages) {
        this.dialogMessages = newDialogMessages;
        this.dialogUsers.clear();
        this.dialogUsers.addAll(newDialogUsers);
        messageBubbleList = dialogMessages.stream()
                .map(msg -> createMessageWrapper(new MessageItem(msg, new Color(0, 120, 255)),
                        new Random().nextBoolean(), false))
                .toList();
        bodyContainer.removeAll();
        messageBubbleList.forEach(bodyContainer::add);
        bodyContainer.revalidate();
        bodyContainer.repaint();
    }

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
        // this.parent.setLayout(null);
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
