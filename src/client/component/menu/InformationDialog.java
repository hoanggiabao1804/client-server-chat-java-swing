package component.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.EmptyBorder;

import domain.Dialog;
import domain.UserMetadata;

public class InformationDialog {
    private Container parent;
    private Dimension size;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Font & Color
    private Font headerFont;
    private Font labelFont;
    private Font textFont;
    private Font buttonFont;

    // Containers section
    private JPanel rootContainer;
    private JPanel headerContainer;
    private JPanel bodyContainer;
    private JPanel footerContainer;

    // Header section
    private JLabel headerLabel;

    // Body section
    private JScrollPane scrollPane;

    // Footer section
    private JButton closeButton;

    public InformationDialog(Container parent) {
        this.parent = parent;
        this.size = parent.getSize();

        // Font & Color
        this.headerFont = new Font("Consolas", Font.BOLD, 30);
        this.labelFont = new Font("Consolas", Font.BOLD, 20);
        this.textFont = new Font("Consolas", Font.PLAIN, 15);
        this.buttonFont = new Font("Consolas", Font.BOLD, 20);

        // Container initialization
        rootContainer = new JPanel();
        headerContainer = new JPanel();
        bodyContainer = new JPanel();
        footerContainer = new JPanel();

        // Header initialization
        headerLabel = new JLabel();

        // Body initialization;
        scrollPane = new JScrollPane(bodyContainer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Footer initialization
        closeButton = new JButton();

        // Root section
        rootContainer.setLayout(new BorderLayout());
        rootContainer.setPreferredSize(size);
        rootContainer.setBackground(Color.white);
        rootContainer.add(headerContainer, BorderLayout.NORTH);
        rootContainer.add(scrollPane, BorderLayout.CENTER);
        rootContainer.add(footerContainer, BorderLayout.SOUTH);

        // Header section
        headerContainer.setLayout(new FlowLayout(FlowLayout.LEADING));
        headerContainer.setPreferredSize(new Dimension(size.width - 10, 50));
        headerContainer.setBackground(Color.white);
        headerContainer.add(headerLabel);

        headerLabel.setText("Thông tin thành viên đoạn hội thoại");
        headerLabel.setFont(headerFont);

        // Body section
        bodyContainer.setLayout(new BoxLayout(bodyContainer, BoxLayout.Y_AXIS));
        bodyContainer.setBackground(Color.white);

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setLayout(new ScrollPaneLayout());
        scrollPane.setPreferredSize(new Dimension(size.width - 10, size.height - 130));
        scrollPane.setBorder(new EmptyBorder(5, 10, 5, 10));
        scrollPane.setBackground(Color.white);

        // Footer section
        footerContainer.setLayout(new FlowLayout(FlowLayout.TRAILING));
        footerContainer.setPreferredSize(new Dimension(size.width - 10, 50));
        footerContainer.setBackground(Color.white);
        footerContainer.add(closeButton);

        closeButton.setText("Đóng");
        closeButton.setFocusable(false);
        closeButton.setFont(buttonFont);
        closeButton.setPreferredSize(new Dimension(100, 40));
    }

    private JPanel createUserInfoPanel(UserMetadata userMetadata) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(5, 10, 5, 10));

        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(Color.white);
        userInfoPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY),
                        new EmptyBorder(8, 10, 8, 10)));

        JLabel nameLabel = new JLabel(userMetadata.getName());
        JLabel emailLabel = new JLabel("Email: " + userMetadata.getEmail());
        JLabel birthdayLabel = new JLabel("Ngày sinh: " + userMetadata.getDob().format(dateTimeFormatter));
        JLabel genderLabel = new JLabel("Giới tính: " + userMetadata.getGender());

        nameLabel.setFont(labelFont);
        emailLabel.setFont(textFont);
        birthdayLabel.setFont(textFont);
        genderLabel.setFont(textFont);

        userInfoPanel.add(nameLabel);
        userInfoPanel.add(emailLabel);
        userInfoPanel.add(birthdayLabel);
        userInfoPanel.add(genderLabel);

        userInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        wrapper.add(userInfoPanel, BorderLayout.CENTER);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        return wrapper;
    }

    public void setCloseAction(ActionListener l) {
        closeButton.addActionListener(l);
    }

    public Container getRootContainer() {
        return this.rootContainer;
    }

    public void loadDialog(Dialog dialog) {
        bodyContainer.removeAll();

        for (UserMetadata userMetadata : dialog.getParticipants()) {
            bodyContainer.add(createUserInfoPanel(userMetadata));
        }

        bodyContainer.revalidate();
        bodyContainer.repaint();
    }

    public void draw() {
        this.parent.add(this.rootContainer);
    }
}
