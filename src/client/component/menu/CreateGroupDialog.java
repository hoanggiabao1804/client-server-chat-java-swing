package component.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import constant.GenderEnum;
import domain.User;
import domain.UserMetadata;
import domain.dto.CreateGroupRequest;
import util.LocalStorage;

public class CreateGroupDialog {
	private Container parent;
	private Dimension size;
	private final Map<String, UserMetadata> userStorage;
	private final Locale locale = Locale.of("vi");
	private User userLogin;

	private UserMetadata user1Metadata = new UserMetadata("019d7869-8821-7da1-9f04-53ff53d972dd",
			"adminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadminadmin",
			"admin@example.com", LocalDate.of(2000, 1, 1), GenderEnum.MALE);
	private UserMetadata user2Metadata = new UserMetadata("727dfee5-0591-447d-bef0-5ee0f56089f5", "Hoàng Bảo",
			"bao123@gmail.com", LocalDate.of(2000, 1, 2), GenderEnum.MALE);

	// Font & Color
	private Font headerFont;
	private Font labelFont;
	private Font textFont;
	private Font buttonFont;
	private Font errorFont;

	// Containers section
	private JPanel rootContainer;
	private JPanel headerContainer;
	private JPanel bodyContainer;
	private JPanel footerContainer;

	// Header section
	private JLabel createLabel;

	// Body section
	private JPanel formPanel;
	private JPanel groupNamePanel;
	private JLabel groupNameLabel;
	private JTextField groupNameTextField;
	private JLabel groupNameErrorLabel;
	private JPanel participantsPanel;
	private JLabel participantsLabel;
	private JTextField participantsTextField;
	private JLabel participantsErrorLabel;
	private JLabel searchLabel;
	private JPanel searchContainer;
	private JTextField searchTextField;
	private JButton searchButton;
	private ImageIcon searchIcon;
	private JPanel usersPanel;
	private JScrollPane usersScrollPane;
	private List<UserMetadata> userList;
	private List<JPanel> checkBoxList;

	// Footer section
	private JButton submitButton;
	private JButton cancelButton;

	public CreateGroupDialog() {
		this.userStorage = LocalStorage.getUsers();
		this.userStorage.put(user1Metadata.getId(), user1Metadata);
		this.userStorage.put(user2Metadata.getId(), user2Metadata);
	}

	public void init(Container parent) {
		this.parent = parent;
		this.size = parent.getSize();

		// Font & Color
		this.headerFont = new Font("Consolas", Font.BOLD, 30);
		this.labelFont = new Font("Consolas", Font.BOLD, 25);
		this.textFont = new Font("Consolas", Font.PLAIN, 15);
		this.buttonFont = new Font("Consolas", Font.PLAIN, 20);
		this.errorFont = new Font("Consolas", Font.ITALIC, 15);

		// Container initialization
		rootContainer = new JPanel();
		headerContainer = new JPanel();
		bodyContainer = new JPanel();
		footerContainer = new JPanel();

		// Header initialization
		createLabel = new JLabel();

		// Body initialization
		formPanel = new JPanel();
		groupNamePanel = new JPanel();
		groupNameLabel = new JLabel();
		groupNameTextField = new JTextField();
		groupNameErrorLabel = new JLabel();
		participantsPanel = new JPanel();
		participantsLabel = new JLabel();
		participantsTextField = new JTextField();
		participantsErrorLabel = new JLabel();
		searchLabel = new JLabel();
		searchContainer = new JPanel();
		searchTextField = new JTextField();
		searchButton = new JButton();
		searchIcon = new ImageIcon("assets/search.png");
		usersPanel = new JPanel();
		usersScrollPane = new JScrollPane(usersPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		userList = new ArrayList<>();
		checkBoxList = this.userStorage.values().stream().map(item -> createUserOptionTabPanel(item))
				.collect(Collectors.toList());

		// Footer initialization
		submitButton = new JButton();
		cancelButton = new JButton();

		// Root section
		rootContainer.setLayout(new BorderLayout());
		rootContainer.setPreferredSize(size);
		rootContainer.setBackground(Color.white);

		rootContainer.add(headerContainer, BorderLayout.NORTH);
		rootContainer.add(bodyContainer, BorderLayout.CENTER);
		rootContainer.add(footerContainer, BorderLayout.SOUTH);

		// Header section
		headerContainer.setLayout(new FlowLayout(FlowLayout.LEADING));
		headerContainer.setPreferredSize(new Dimension(size.width - 10, 50));
		headerContainer.setBackground(Color.white);
		headerContainer.add(createLabel);

		createLabel.setText("Tạo nhóm");
		createLabel.setFont(headerFont);

		// Body section

		bodyContainer.setLayout(new BorderLayout());
		bodyContainer.setBackground(Color.white);
		bodyContainer.add(formPanel, BorderLayout.NORTH);
		bodyContainer.add(usersScrollPane, BorderLayout.CENTER);

		formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
		formPanel.setBackground(Color.white);
		formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		formPanel.add(groupNamePanel);
		formPanel.add(participantsPanel);
		formPanel.add(searchLabel);
		formPanel.add(searchContainer);

		groupNamePanel.setLayout(new BoxLayout(groupNamePanel, BoxLayout.Y_AXIS));
		groupNamePanel.setBackground(Color.white);
		groupNamePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		groupNamePanel.setBorder(new EmptyBorder(5, 0, 5, 0));
		groupNamePanel.add(groupNameLabel);
		groupNamePanel.add(groupNameTextField);
		groupNamePanel.add(groupNameErrorLabel);

		groupNameLabel.setText("Tên nhóm");
		groupNameLabel.setFont(labelFont);
		groupNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		fixFullWidth(groupNameTextField, 30);
		groupNameTextField.setFont(textFont);
		groupNameTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (groupNameErrorLabel.isVisible()) {
					groupNameErrorLabel.setVisible(false);
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (groupNameErrorLabel.isVisible()) {
					groupNameErrorLabel.setVisible(false);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (groupNameErrorLabel.isVisible()) {
					groupNameErrorLabel.setVisible(false);
				}
			}
		});

		groupNameErrorLabel.setFont(errorFont);
		groupNameErrorLabel.setForeground(Color.red);
		groupNameErrorLabel.setVisible(false);

		participantsPanel.setLayout(new BoxLayout(participantsPanel, BoxLayout.Y_AXIS));
		participantsPanel.setBackground(Color.white);
		participantsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		participantsPanel.setBorder(new EmptyBorder(5, 0, 10, 0));
		participantsPanel.add(participantsLabel);
		participantsPanel.add(participantsTextField);
		participantsPanel.add(participantsErrorLabel);

		participantsLabel.setText("Thành viên");
		participantsLabel.setFont(labelFont);
		participantsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		fixFullWidth(participantsTextField, 30);
		participantsTextField.setFont(textFont);
		participantsTextField.setEditable(false);
		participantsTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				if (participantsErrorLabel.isVisible()) {
					participantsErrorLabel.setVisible(false);
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (participantsErrorLabel.isVisible()) {
					participantsErrorLabel.setVisible(false);
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (participantsErrorLabel.isVisible()) {
					participantsErrorLabel.setVisible(false);
				}
			}
		});

		participantsErrorLabel.setFont(errorFont);
		participantsErrorLabel.setForeground(Color.red);
		participantsErrorLabel.setVisible(false);

		searchLabel.setText("Tìm thành viên");
		searchLabel.setFont(labelFont);
		searchLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		searchLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		searchContainer.setLayout(new BorderLayout(5, 0));
		searchContainer.setBackground(Color.white);
		searchContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		// searchContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
		searchContainer.setPreferredSize(new Dimension(size.width - 60, 35));
		searchContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
		searchContainer.setMinimumSize(new Dimension(size.width - 60, 35));
		searchContainer.add(searchTextField, BorderLayout.CENTER);
		searchContainer.add(searchButton, BorderLayout.EAST);

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
		});

		usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
		usersPanel.setBackground(Color.lightGray);

		usersScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		usersScrollPane.setLayout(new ScrollPaneLayout());
		usersScrollPane.setPreferredSize(new Dimension(size.width - 60, size.height - 130));
		usersScrollPane.setBorder(new EmptyBorder(5, 10, 5, 10));
		usersScrollPane.setBackground(Color.white);

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

		submitButton.setText("Chọn");
		submitButton.setFocusable(false);
		submitButton.setFont(buttonFont);
		submitButton.setPreferredSize(new Dimension(100, 40));
	}

	private JPanel createUserOptionTabPanel(UserMetadata userMetadata) {
		JPanel wrapper = new JPanel();
		JPanel rootPanel = new JPanel();
		JCheckBox checkBox = new JCheckBox();
		JPanel contentPanel = new JPanel();
		JLabel nameLabel = new JLabel();

		int rowHeight = 60;

		wrapper.setLayout(new BorderLayout());
		wrapper.setOpaque(false);
		wrapper.setBorder(
				new EmptyBorder(5, 5, 5, 5));
		wrapper.setPreferredSize(new Dimension(size.width - 50, rowHeight));
		wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowHeight));
		wrapper.setMinimumSize(new Dimension(size.width - 50, rowHeight));

		wrapper.add(rootPanel, BorderLayout.CENTER);

		rootPanel.setLayout(new BorderLayout(10, 0));
		rootPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
		rootPanel.setPreferredSize(new Dimension(size.width - 50, rowHeight));
		rootPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowHeight));
		rootPanel.setMinimumSize(new Dimension(size.width - 50, rowHeight));
		rootPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		rootPanel.setBackground(Color.white);
		rootPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (userList.contains(userMetadata)) {
					userList.remove(userMetadata);
					checkBox.setSelected(false);
				} else {
					userList.add(userMetadata);
					checkBox.setSelected(true);
				}
			}
		});

		checkBox.setBackground(Color.white);
		checkBox.setFocusable(false);
		if (this.userList.contains(userMetadata)) {
			checkBox.setSelected(true);
		}
		checkBox.addActionListener(l -> {
			if (this.userList.contains(userMetadata)) {
				this.userList.remove(userMetadata);
			} else {
				this.userList.add(userMetadata);
			}
		});

		contentPanel.setLayout(new BorderLayout());
		contentPanel.setOpaque(false);
		contentPanel.add(nameLabel, BorderLayout.CENTER);

		nameLabel.setText(userMetadata.getName());
		nameLabel.setFont(labelFont);
		nameLabel.setHorizontalAlignment(JLabel.LEFT);

		rootPanel.add(checkBox, BorderLayout.WEST);
		rootPanel.add(contentPanel, BorderLayout.CENTER);

		return wrapper;
	}

	private void search(String keyword) {
		List<UserMetadata> searchedUsers = this.userStorage.values().stream().filter(item -> {
			if (keyword.isBlank()) {
				return true;
			}

			return item.getName().toLowerCase(locale).contains(keyword);
		}).collect(Collectors.toList());

		checkBoxList = searchedUsers.stream().map(item -> createUserOptionTabPanel(item)).collect(Collectors.toList());

		usersPanel.removeAll();
		checkBoxList.forEach(usersPanel::add);
		usersPanel.revalidate();
		usersPanel.repaint();
	}

	public void setSubmitAction(ActionListener l) {
		submitButton.addActionListener(l);
	}

	public void setCancelAction(ActionListener l) {
		cancelButton.addActionListener(l);
	}

	public Container getRootContainer() {
		return this.rootContainer;
	}

	private void fixFullWidth(JComponent comp, int height) {
		comp.setAlignmentX(Component.LEFT_ALIGNMENT);
		comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		comp.setPreferredSize(new Dimension(size.width - 40, height));
		comp.setMinimumSize(new Dimension(size.width - 60, height));
	}

	public void reset() {
		groupNameTextField.setText("");
		groupNameErrorLabel.setVisible(false);

		participantsTextField.setText("");
		participantsErrorLabel.setVisible(false);

		userList.clear();
		checkBoxList = this.userStorage.values().stream().map(item -> createUserOptionTabPanel(item))
				.collect(Collectors.toList());
		usersPanel.removeAll();
		// for (int i = 0; i < 10; ++i) {
		// usersPanel.add(createUserOptionTabPanel(user1Metadata));
		// }
		checkBoxList.forEach(usersPanel::add);
		usersPanel.revalidate();
		usersPanel.repaint();
		searchTextField.setText("");
	}

	public void loadUser() {
		userLogin = LocalStorage.getUserLogin();
	}

	public void draw() {
		this.parent.add(this.rootContainer);
	}

	public CreateGroupRequest submit() {
		boolean hasError = false;
		if (groupNameTextField.getText().strip().isBlank()) {
			groupNameErrorLabel.setText("* Tên nhóm không được trống.");
			groupNameErrorLabel.setVisible(true);
			hasError = true;
		}

		if (userList.size() < 2) {
			participantsErrorLabel.setText("* Nhóm cần ít nhất 3 thành viên (gồm cả bạn).");
			participantsErrorLabel.setVisible(true);
			hasError = true;
		}

		if (hasError) {
			return null;
		}

		return new CreateGroupRequest(groupNameTextField.getText().strip(), userLogin.getId().toString(), "group",
				userList.stream().map(item -> item.getId()).collect(Collectors.toList()));
	}
}
