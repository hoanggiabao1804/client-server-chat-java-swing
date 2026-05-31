package component.picker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.EmptyBorder;

import component.AppContext;
import component.AppFrame;

public class EmojiPicker implements AppContext {
    private Container parent;
    private Dimension size = new Dimension(500, 600);

    // Font & Color
    private final Font headerFont;
    private final Font buttonFont;
    private final Color clickedColor;

    private JPanel rootContainer;

    // Header section
    private JPanel headerContainer;
    private JLabel headerLabel;

    // Body section
    private JPanel bodyContainer;
    private JScrollPane scrollPane;
    private List<JPanel> emojiList;
    private List<ImageIcon> emojIcons;
    private JPanel selectedPanel;
    private ImageIcon selectedEmoji;

    // Footer section
    private JPanel footerContainer;
    private JButton submitButton;
    private JButton cancelButton;

    public EmojiPicker(Container parent) {
        this.parent = parent;

        this.headerFont = new Font("Consolas", Font.BOLD, 30);
        this.buttonFont = new Font("Consolas", Font.PLAIN, 20);
        this.clickedColor = new Color(114, 210, 219);

        this.rootContainer = new JPanel();

        // Header initialization
        this.headerContainer = new JPanel();
        this.headerLabel = new JLabel();

        // Body initialization
        this.bodyContainer = new JPanel();
        this.scrollPane = new JScrollPane(bodyContainer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        selectedPanel = null;
        selectedEmoji = null;
        loadEmojis();

        // Footer intialization
        footerContainer = new JPanel();
        cancelButton = new JButton();
        submitButton = new JButton();

        // Root section
        rootContainer.setLayout(new BorderLayout());
        rootContainer.setPreferredSize(size);
        rootContainer.setBackground(Color.white);

        rootContainer.add(headerContainer, BorderLayout.NORTH);
        rootContainer.add(scrollPane, BorderLayout.CENTER);
        rootContainer.add(footerContainer, BorderLayout.SOUTH);

        // Header section
        headerContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerContainer.setPreferredSize(new Dimension(size.width - 10, 50));
        headerContainer.setBackground(Color.white);
        headerContainer.add(headerLabel);

        headerLabel.setText("Chọn emoji");
        headerLabel.setFont(headerFont);

        // Body section
        bodyContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        bodyContainer.setPreferredSize(
                new Dimension(size.width - 10, 54 * Math.ceilDiv(emojIcons.size(), Math.floorDiv(size.width, 54))));

        bodyContainer.setBackground(Color.white);
        emojiList.forEach(bodyContainer::add);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setLayout(new ScrollPaneLayout());
        scrollPane.setPreferredSize(new Dimension(size.width - 10, size.height - 130));
        scrollPane.setBorder(new EmptyBorder(5, 0, 5, 0));
        scrollPane.setBackground(Color.lightGray);

        // Footer section
        footerContainer.setLayout(new FlowLayout(FlowLayout.TRAILING));
        footerContainer.setPreferredSize(new Dimension(size.width - 10, 50));
        footerContainer.setBackground(Color.white);
        footerContainer.add(cancelButton);
        footerContainer.add(submitButton);

        cancelButton.setText("Hủy");
        cancelButton.setFocusable(false);
        cancelButton.setFont(buttonFont);
        cancelButton.setPreferredSize(new Dimension(100, 40));

        submitButton.setText("Gửi");
        submitButton.setFocusable(false);
        submitButton.setFont(buttonFont);
        submitButton.setPreferredSize(new Dimension(100, 40));
        submitButton.setEnabled(false);

    }

    private JPanel createEmojiItem(ImageIcon emoji) {
        JPanel rootPanel = new JPanel();
        JButton emojiButton = new JButton();

        rootPanel.setLayout(new BorderLayout());
        rootPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        rootPanel.setBackground(Color.white);
        rootPanel.setPreferredSize(new Dimension(50, 50));
        rootPanel.setMaximumSize(new Dimension(50, 50));
        rootPanel.setMinimumSize(new Dimension(50, 50));

        emojiButton.setIcon(emoji);
        emojiButton.setFocusable(false);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectedEmoji = emoji;
                if (selectedPanel != null) {
                    selectedPanel.setBackground(Color.white);

                    if (selectedPanel == rootPanel) {
                        selectedPanel = null;
                        selectedEmoji = null;
                        submitButton.setEnabled(false);

                        return;
                    }
                }

                selectedPanel = rootPanel;
                selectedPanel.setBackground(clickedColor);
                submitButton.setEnabled(true);
            }

            public void mouseEntered(MouseEvent e) {
                if (selectedPanel != rootPanel) {
                    rootPanel.setBackground(Color.lightGray);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (selectedPanel != rootPanel) {
                    rootPanel.setBackground(Color.white);
                }
            }
        };

        emojiButton.addMouseListener(mouseAdapter);
        rootPanel.addMouseListener(mouseAdapter);

        rootPanel.add(emojiButton, BorderLayout.CENTER);

        return rootPanel;
    }

    private void loadEmojis() {
        try {
            String path = "assets/emojis/";
            List<String> emojiPathStrings = Files.list(Paths.get(path)).filter(Files::isRegularFile)
                    .map(p -> path + p.getFileName().toString()).filter(name -> name.endsWith(".png"))
                    .collect(Collectors.toList());

            emojIcons = emojiPathStrings.stream().map(item -> {
                ImageIcon rawIcon = new ImageIcon(item);
                Image scaledImage = rawIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                rawIcon.setImage(scaledImage);

                return rawIcon;
            }).collect(Collectors.toList());
            emojiList = emojIcons.stream().map(item -> createEmojiItem(item)).collect(Collectors.toList());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setSubmitAction(ActionListener l) {
        submitButton.addActionListener(l);
    }

    public void setCancelAction(ActionListener l) {
        cancelButton.addActionListener(l);
    }

    public void reset() {
        this.selectedEmoji = null;
        this.selectedPanel = null;
        this.submitButton.setEnabled(false);
    }

    public ImageIcon submit() {
        return selectedEmoji;
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
