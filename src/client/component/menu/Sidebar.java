package component.menu;

import java.awt.BorderLayout;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import component.AppContext;
import component.AppFrame;
import component.PopupWindow;
// import server.repository.DialogRepository;
import domain.Dialog;
import domain.Message;
import domain.User;
import domain.UserMetadata;
import domain.dto.CreateGroupRequest;
import domain.dto.CreateGroupResponse;
import domain.dto.DeleteMessageResponse;
import domain.dto.DialogContentResponse;
import domain.dto.SearchUserRequest;
import domain.dto.SearchUserResponse;
import domain.dto.SendMessageResponse;
import domain.dto.UserDialogResponse;
import util.LocalStorage;
import util.PacketService;

public class Sidebar implements AppContext {

    private final Container parent;
    private final Dimension size;
    private final Map<String, Dialog> storage = new HashMap<>();
    private final Map<String, UserMetadata> userStorage;
    private final Locale locale = Locale.of("vi");

    private User userLogin;

    // Font & color
    private final Font headerFont;
    private final Font tabFont;
    private final Font textFont;
    private final Color sidebarColor;
    private final Color clickedTabColor;

    // Containers section
    private JPanel sidebarContainer;
    private JPanel headerContainer;
    private JPanel bodyContainer;

    // Header section
    private JPanel labelContainer;
    private JButton createGroupButton;
    private ImageIcon createGroupIcon;
    private JLabel headerLabel;
    private JPanel searchContainer;
    private JTextField searchTextField;
    private JButton searchButton;
    private ImageIcon searchIcon;

    // Body section
    private JScrollPane scrollPane;
    private List<JPanel> dialogTabList;
    private Map<String, JPanel> dialogTabMap = new ConcurrentHashMap<>();
    private Dialog selectedDialog;
    private JPanel selectedDialogTab;

    private Dimension createGroupWindowSize;
    private PopupWindow createGroupPopupWindow;
    private CreateGroupDialog createGroupDialog;

    // private CountDownLatch countDownLatch;
    private final Map<String, CountDownLatch> pendingObjects = new ConcurrentHashMap<>();

    public Sidebar(Container parent, Dimension size) {
        this.parent = parent;
        this.size = size;
        this.userStorage = LocalStorage.getUsers();

        // Font & Color
        this.headerFont = new Font("Consolas", Font.BOLD, 30);
        this.tabFont = new Font("Consolas", Font.BOLD, 20);
        this.textFont = new Font("Consolas", Font.PLAIN, 15);
        this.sidebarColor = new Color(0, 129, 138);
        this.clickedTabColor = new Color(114, 210, 219);

        // Container initialization
        sidebarContainer = new JPanel();
        headerContainer = new JPanel();
        bodyContainer = new JPanel();

        // Header initialization
        labelContainer = new JPanel();
        headerLabel = new JLabel();
        createGroupButton = new JButton();
        createGroupIcon = new ImageIcon("assets/plus.png");
        searchContainer = new JPanel();
        searchTextField = new JTextField();
        searchButton = new JButton();
        searchIcon = new ImageIcon("assets/search.png");

        // Body initialization
        scrollPane = new JScrollPane(bodyContainer);
        dialogTabList = null;
        // dialogTabList = this.storage.values().stream().map(item -> {
        // return createDialogTabPanel(item);
        // }).collect(Collectors.toList());
        selectedDialog = null;
        selectedDialogTab = null;

        createGroupWindowSize = new Dimension(600, 960);
        createGroupPopupWindow = new PopupWindow(createGroupWindowSize, "Tạo nhóm");

        createGroupDialog = new CreateGroupDialog();
        createGroupDialog.init(createGroupPopupWindow.getRootComponent());
        createGroupDialog.setSubmitAction(l -> {
            CreateGroupRequest createGroupRequest = createGroupDialog.submit();
            if (createGroupRequest != null) {
                List<String> participants = createGroupRequest.getParticipantIds();
                Dialog newDialog = new Dialog("temp", createGroupRequest.getGroupName(), participants,
                        new ArrayList<>(), "group",
                        createGroupRequest.getCreatorId());

                this.storage.put("temp", newDialog);
                JPanel newDialogTabPanel = createDialogTabPanel(newDialog);
                dialogTabList.add(0, newDialogTabPanel);
                bodyContainer.add(newDialogTabPanel, 0);
                bodyContainer.revalidate();
                bodyContainer.repaint();

                CountDownLatch countDownLatch = new CountDownLatch(1);
                pendingObjects.put("create-group" + userLogin.getId(), countDownLatch);

                PacketService.createGroup(createGroupRequest);

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
                                        "Tạo nhóm thất bại",
                                        "Server không phản hồi!",
                                        JOptionPane.ERROR_MESSAGE);
                            });
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

                createGroupPopupWindow.close();
            }
        });

        createGroupDialog.setCancelAction(l -> {
            createGroupPopupWindow.close();
        });

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
        headerContainer.add(labelContainer, gbc);
        headerContainer.add(searchContainer, gbc);

        // labelContainer.setLayout(new FlowLayout(FlowLayout.LEFT));
        // labelContainer.setPreferredSize(new Dimension(size.width, 60));
        // labelContainer.setBackground(Color.white);
        // labelContainer.add(headerLabel);
        // labelContainer.add(createGroupButton);

        labelContainer.setLayout(new BorderLayout());
        labelContainer.setPreferredSize(new Dimension(size.width, 75));
        labelContainer.setBackground(Color.white);
        labelContainer.setBorder(new EmptyBorder(0, 10, 0, 10));

        // JPanel createGroupButtonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT,
        // 0, 10));
        // createGroupButtonWrapper.setOpaque(false);
        // createGroupButtonWrapper.add(createGroupButton);
        JPanel createGroupButtonWrapper = new JPanel(new GridBagLayout());
        createGroupButtonWrapper.setOpaque(false);
        createGroupButtonWrapper.add(createGroupButton);

        labelContainer.add(headerLabel, BorderLayout.WEST);
        labelContainer.add(createGroupButtonWrapper, BorderLayout.EAST);

        headerLabel.setText("Đoạn chat");
        headerLabel.setFont(this.headerFont);
        headerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        createGroupButton.setText("Tạo nhóm");
        createGroupButton.setFocusable(false);
        createGroupButton.setIcon(createGroupIcon);
        createGroupButton.setPreferredSize(new Dimension(130, 35));
        createGroupButton.setMinimumSize(new Dimension(130, 35));
        createGroupButton.setMaximumSize(new Dimension(130, 35));
        createGroupButton.addActionListener(l -> {
            createGroupDialog.reset();
            createGroupDialog.draw();
            createGroupPopupWindow.draw();
        });

        searchContainer.setLayout(new BorderLayout(8, 0));
        searchContainer.setPreferredSize(new Dimension(size.width - 20, 45));
        searchContainer.setBorder(new EmptyBorder(5, 10, 5, 10));
        searchContainer.setBackground(Color.white);

        searchContainer.add(searchTextField, BorderLayout.CENTER);
        searchContainer.add(searchButton, BorderLayout.EAST);

        searchTextField.setPreferredSize(new Dimension(size.width - 90, 35));
        searchTextField.setFont(this.textFont);
        searchTextField.setBackground(Color.white);
        searchTextField.setForeground(Color.black);
        searchTextField.addActionListener(e -> {
            String keyword = searchTextField.getText().strip();

            this.search(keyword.toLowerCase(locale));
        });

        searchButton.setIcon(searchIcon);
        searchButton.setPreferredSize(new Dimension(50, 35));
        searchButton.setFocusable(false);
        searchButton.addActionListener(l -> {
            String keyword = searchTextField.getText().strip();

            this.search(keyword.toLowerCase(locale));
        });

        // Body section
        bodyContainer.setLayout(new BoxLayout(bodyContainer, BoxLayout.Y_AXIS));
        // bodyContainer.setPreferredSize(new Dimension(size.width - 20,
        // dialogTabList.size() * 100));
        bodyContainer.setBackground(Color.white);

        // dialogTabList.forEach(item -> bodyContainer.add(item));

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setLayout(new ScrollPaneLayout());
        scrollPane.setPreferredSize(new Dimension(size.width - 10, size.height - 130));
        scrollPane.setBorder(new EmptyBorder(5, 0, 5, 0));
        scrollPane.setBackground(Color.lightGray);

    }

    private JPanel createDialogTabPanel(Dialog dialog) {
        JPanel rootPanel = new JPanel();
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        RoundButton avatarButton = new RoundButton();
        ImageIcon avatarIcon = null;

        if (selectedDialog != null && selectedDialog.getId().equals(dialog.getId())) {
            selectedDialog = dialog;
            selectedDialogTab = rootPanel;
        }

        if (dialog.getType().equals("private")) {
            avatarIcon = new ImageIcon("assets/user-lock.png");
        } else if (dialog.getType().equals("direct")) {
            avatarIcon = new ImageIcon("assets/user-round.png");
        } else {
            avatarIcon = new ImageIcon("assets/users-round.png");
        }

        JPanel contentPanel = new JPanel();
        JLabel nameLabel = new JLabel();
        JLabel lastMessageLabel = new JLabel();

        int tabHeight = 80;
        int tabWidth = this.size.width - 20;

        // Root section
        rootPanel.setLayout(new BorderLayout(10, 0));
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rootPanel.setBackground((selectedDialog == dialog) ? clickedTabColor : Color.white);
        rootPanel.setPreferredSize(new Dimension(tabWidth, tabHeight));
        rootPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, tabHeight));
        rootPanel.setMinimumSize(new Dimension(tabWidth, tabHeight));

        MouseAdapter mouseAdapter = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (selectedDialog != null) {
                    if (selectedDialog != dialog) {
                        selectedDialogTab.setBackground(Color.white);
                        selectedDialogTab = rootPanel;
                        selectedDialogTab.setBackground(clickedTabColor);

                        selectedDialog = dialog;
                    }
                } else {
                    selectedDialog = dialog;
                    selectedDialogTab = rootPanel;
                    selectedDialogTab.setBackground(clickedTabColor);
                }

                MainMenu mainMenu = (MainMenu) AppFrame.getInstance().getContextPools().getContext("mainMenu");
                mainMenu.loadDialogDetail(dialog);
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
        };

        rootPanel.addMouseListener(mouseAdapter);

        avatarPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(60, 50));

        // Content section
        avatarButton.setIcon(avatarIcon);
        avatarButton.setPreferredSize(new Dimension(50, 50));
        avatarButton.setMinimumSize(new Dimension(50, 50));
        avatarButton.setMaximumSize(new Dimension(50, 50));
        avatarButton.setFocusable(false);
        avatarButton.addMouseListener(mouseAdapter);

        avatarPanel.add(avatarButton);

        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        String dialogName = dialog.getName();
        if (dialog.getType().equals("direct")) {
            for (String userId : dialog.getParticipants()) {
                if (!userId.equals(userLogin.getId())) {
                    dialogName = userStorage.get(userId).getName();
                    break;
                }
            }
        } else if (dialog.getType().equals("private")) {
            dialogName = userStorage.get(dialog.getParticipants().get(0)).getName();
        }

        nameLabel.setText(dialogName);
        nameLabel.setFont(this.tabFont);

        if (dialog.getType().equals("private")) {
            nameLabel.setFont(new Font("Consolas", Font.ITALIC, 20));
            nameLabel.setForeground(new Color(0, 129, 138));
        }

        // lastMessageLabel.setPreferredSize(labelSize);
        Message lastMessage = dialog.getMessages() == null || dialog.getMessages().isEmpty() ? null
                : dialog.getMessages().getLast();

        if (lastMessage != null) {
            String lastMessageContent = lastMessage.getContent();

            boolean isSender = lastMessage.getSenderId().equals(userLogin.getId());

            if (lastMessage.getType().equals("file")) {
                lastMessageContent = (isSender) ? "Bạn đã gửi một file đính kèm" : "Đã gửi một file đính kèm";
            } else {
                lastMessageContent = (isSender) ? "Bạn: " + lastMessageContent : lastMessageContent;
            }

            lastMessageLabel.setText(lastMessageContent);
        }

        lastMessageLabel.setFont(this.textFont);

        contentPanel.add(nameLabel);
        contentPanel.add(lastMessageLabel);

        rootPanel.add(avatarPanel, BorderLayout.WEST);
        rootPanel.add(contentPanel, BorderLayout.CENTER);

        return rootPanel;
    }

    private JPanel createUserSearchTabPanel(UserMetadata userMetadata) {
        JPanel rootPanel = new JPanel(new BorderLayout(10, 0));
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        RoundButton avatarButton = new RoundButton();
        ImageIcon avatarIcon = new ImageIcon("assets/user-round.png");
        ImageIcon plusIcon = new ImageIcon("assets/plus.png");

        JLabel nameLabel = new JLabel(userMetadata.getName());

        JButton addButton = new JButton();

        int tabHeight = 80;
        int tabWidth = this.size.width - 20;

        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rootPanel.setBackground(Color.white);
        rootPanel.setPreferredSize(new Dimension(tabWidth, tabHeight));
        rootPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, tabHeight));
        rootPanel.setMinimumSize(new Dimension(tabWidth, tabHeight));

        avatarPanel.setOpaque(false);
        avatarPanel.setPreferredSize(new Dimension(60, 50));

        avatarButton.setIcon(avatarIcon);
        avatarButton.setPreferredSize(new Dimension(50, 50));
        avatarButton.setMinimumSize(new Dimension(50, 50));
        avatarButton.setMaximumSize(new Dimension(50, 50));
        avatarButton.setFocusable(false);

        avatarPanel.add(avatarButton);

        nameLabel.setFont(this.tabFont);

        addButton.setIcon(plusIcon);
        addButton.setFont(this.tabFont);
        addButton.setFocusable(false);
        addButton.setBackground(null);
        addButton.setPreferredSize(new Dimension(50, 50));

        addButton.addActionListener(e -> {
            createDirectDialogWithUser(userMetadata);
            bodyContainer.remove(rootPanel);
            bodyContainer.revalidate();
            bodyContainer.repaint();
        });

        rootPanel.add(avatarPanel, BorderLayout.WEST);
        rootPanel.add(nameLabel, BorderLayout.CENTER);
        rootPanel.add(addButton, BorderLayout.EAST);

        rootPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                rootPanel.setBackground(Color.lightGray);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                rootPanel.setBackground(Color.white);
            }
        });

        return rootPanel;
    }

    private void createDirectDialogWithUser(UserMetadata targetUser) {
        List<String> participantIds = List.of(
                userLogin.getId(),
                targetUser.getId());

        CreateGroupRequest request = new CreateGroupRequest(
                targetUser.getName(),
                userLogin.getId(),
                "direct",
                participantIds);

        PacketService.createGroup(request);
    }

    public void revalidateData(Dialog dialog) {
        this.selectedDialog = dialog;

        dialogTabList = this.storage.values().stream().map(item -> {
            JPanel tabPanel = createDialogTabPanel(item);
            dialogTabMap.put(item.getId(), tabPanel);
            return tabPanel;
        }).collect(Collectors.toList());

        bodyContainer.removeAll();
        dialogTabList.forEach(item -> bodyContainer.add(item));
    }

    public void setDialogPool(List<Dialog> dialogPool) {
        this.storage.clear();
        dialogPool.forEach(item -> {
            this.storage.put(item.getId(), item);
        });

        dialogTabList = this.storage.values().stream().map(item -> {
            JPanel tabPanel = createDialogTabPanel(item);
            dialogTabMap.put(item.getId(), tabPanel);
            return tabPanel;
        }).collect(Collectors.toList());

        bodyContainer.removeAll();
        dialogTabList.forEach(item -> bodyContainer.add(item));
    }

    private void search(String keyword) {
        bodyContainer.removeAll();

        if (keyword.isBlank()) {
            dialogTabList = storage.values()
                    .stream()
                    .map(item -> {
                        JPanel tabPanel = createDialogTabPanel(item);
                        dialogTabMap.put(item.getId(), tabPanel);
                        return tabPanel;
                    })
                    .collect(Collectors.toList());

            dialogTabList.forEach(bodyContainer::add);

            bodyContainer.revalidate();
            bodyContainer.repaint();
            return;
        }

        List<JPanel> resultTabs = new ArrayList<>();

        // 1. Search user's dialogs
        List<Dialog> matchedDialogs = storage.values()
                .stream()
                .filter(dialog -> getDisplayDialogName(dialog)
                        .toLowerCase(locale)
                        .contains(keyword))
                .toList();

        for (Dialog dialog : matchedDialogs) {
            resultTabs.add(createDialogTabPanel(dialog));
        }

        // 2. Search user
        List<UserMetadata> matchedUsers = userStorage.values()
                .stream()
                .filter(user -> !user.getId().equals(userLogin.getId()))
                .filter(user -> user.getName().toLowerCase(locale).contains(keyword)
                        || user.getEmail().toLowerCase(locale).contains(keyword))
                .toList();

        for (UserMetadata user : matchedUsers) {
            Dialog directDialog = LocalStorage.findDirectDialog(userLogin.getId(), user.getId());

            if (directDialog != null) {
                boolean alreadyAdded = matchedDialogs.stream()
                        .anyMatch(dialog -> dialog.getId().equals(directDialog.getId()));

                if (!alreadyAdded) {
                    resultTabs.add(createDialogTabPanel(directDialog));
                }
            } else {
                resultTabs.add(createUserSearchTabPanel(user));
            }
        }

        resultTabs.forEach(bodyContainer::add);

        bodyContainer.revalidate();
        bodyContainer.repaint();
    }

    private String getDisplayDialogName(Dialog dialog) {
        String dialogName = dialog.getName();

        if ("direct".equals(dialog.getType())) {
            for (String userId : dialog.getParticipants()) {
                if (!userId.equals(userLogin.getId())) {
                    return userStorage.get(userId).getName();
                }
            }
        }

        return dialogName;
    }

    public void reset() {
        storage.clear();
        userLogin = null;
        searchTextField.setText("");
        dialogTabList.clear();
        dialogTabMap.clear();
        selectedDialog = null;
        selectedDialogTab = null;
        pendingObjects.clear();
    }

    public void loadUser() {
        userLogin = LocalStorage.getUserLogin();
        createGroupDialog.loadUser();
        String userId = userLogin.getId();

        CountDownLatch countDownLatch1 = new CountDownLatch(1);
        CountDownLatch countDownLatch2 = new CountDownLatch(1);
        pendingObjects.put("load-dialogs" + userId, countDownLatch1);
        pendingObjects.put("fetch-users" + userId, countDownLatch2);

        PacketService.loadUserDialogs(userLogin.getId());

        PacketService.fetchUsers(new SearchUserRequest("", userId, "all"));

        new Thread(() -> {
            try {
                boolean success = countDownLatch1.await(5, TimeUnit.SECONDS);

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
                                "Tải danh sách hội thoại không thành công",
                                "Server không phản hồi!",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                boolean success = countDownLatch2.await(5, TimeUnit.SECONDS);

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
                                "Tải danh sách người dùng không thành công",
                                "Server không phản hồi!",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public synchronized void getResponse(UserDialogResponse userDialogResponse) {
        String userId = userLogin.getId();
        CountDownLatch countDownLatch = pendingObjects.remove("load-dialogs" + userId);

        if (countDownLatch != null) {
            countDownLatch.countDown();
        }

        if (userDialogResponse.getStatus().equals("success")) {
            List<Dialog> dialogList = LocalStorage.getUserDialogs();

            dialogList.forEach(item -> {
                this.storage.put(item.getId(), item);
            });

            dialogTabList = this.storage.values().stream().map(item -> {
                JPanel tabPanel = createDialogTabPanel(item);
                dialogTabMap.put(item.getId(), tabPanel);
                return tabPanel;
            }).collect(Collectors.toList());

            bodyContainer.removeAll();
            dialogTabList.forEach(item -> bodyContainer.add(item));
            this.bodyContainer.revalidate();
            this.bodyContainer.repaint();
        } else {
            JOptionPane.showMessageDialog(null, userDialogResponse.getMessage(),
                    "Lấy danh sách đoạn hội thoạt thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public synchronized void getResponse(CreateGroupResponse createGroupResponse) {
        String userId = userLogin.getId();
        CountDownLatch countDownLatch = pendingObjects.remove("create-group" + userId);

        if (countDownLatch != null) {
            countDownLatch.countDown();
        }

        if (createGroupResponse.getStatus().equals("success")) {
            Dialog newDialog = createGroupResponse.getDialog();

            this.storage.put(newDialog.getId(), newDialog);
            JPanel newDialogTabPanel = createDialogTabPanel(newDialog);

            if (this.storage.containsKey("temp")) {
                this.storage.remove("temp");
                JPanel tempDialog = dialogTabList.remove(0);
                bodyContainer.remove(tempDialog);
            }

            dialogTabList.addFirst(newDialogTabPanel);
            dialogTabMap.put(newDialog.getId(), newDialogTabPanel);
            bodyContainer.add(newDialogTabPanel, 0);
            this.bodyContainer.revalidate();
            this.bodyContainer.repaint();
        } else {
            JOptionPane.showMessageDialog(null, createGroupResponse.getMessage(),
                    "Tạo đoạn hội thoạt thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public synchronized void getResponse(SearchUserResponse searchUserResponse) {
        String userId = userLogin.getId();
        CountDownLatch countDownLatch = pendingObjects.remove("fetch-users" + userId);

        if (countDownLatch != null) {
            countDownLatch.countDown();
        }

        if (searchUserResponse.getStatus().equals("success")) {
            searchUserResponse.getFoundUsers().forEach(item -> userStorage.put(item.getId(), item));
        } else {
            JOptionPane.showMessageDialog(null, searchUserResponse.getMessage(),
                    "Tải danh sách người dùng thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public synchronized void getResponse(DialogContentResponse dialogContentResponse) {
        if (dialogContentResponse.getStatus().equals("success")) {
            String dialogId = dialogContentResponse.getDialogId();
            String lastMessage = dialogContentResponse.getMessageList().isEmpty() ? null
                    : dialogContentResponse.getMessageList().getLast().getContent();

            if (lastMessage != null) {
                updateDialogTabLastMessage(dialogId, dialogContentResponse.getMessageList().getLast());
            }
        }
    }

    public synchronized void getResponse(SendMessageResponse sendMessageResponse) {
        if (sendMessageResponse.getStatus().equals("success")) {
            String dialogId = sendMessageResponse.getDialogId();
            Message messagePersisted = sendMessageResponse.getMessagePersisted();

            if (messagePersisted != null) {
                updateDialogTabLastMessage(dialogId, messagePersisted);
            }
        }
    }

    public synchronized void getResponse(DeleteMessageResponse deleteMessageRequest) {
        if (deleteMessageRequest.getStatus().equals("success")) {
            String dialogId = deleteMessageRequest.getDialogId();
            // Message messageDeleted = deleteMessageRequest.getMessageDeleted();

            Dialog dialog = storage.get(dialogId);
            if (dialog != null) {
                Message lastMessage = dialog.getMessages().isEmpty() ? null : dialog.getMessages().getLast();

                updateDialogTabLastMessage(dialogId, lastMessage);
            }
        }
    }

    // public synchronized void getResponse(FetchNewUserResponse
    // fetchNewUserResponse) {
    // if (fetchNewUserResponse.getStatus().equals("success")) {

    // }
    // }

    public synchronized void updateDialogTabLastMessage(String dialogId, Message lastMessage) {
        JPanel dialogTab = dialogTabMap.get(dialogId);

        if (dialogTab != null) {
            JPanel contentPanel = (JPanel) dialogTab.getComponent(1);
            JLabel lastMessageLabel = (JLabel) contentPanel.getComponent(1);
            String lastMessageContent = lastMessage != null ? lastMessage.getContent() : null;
            if (lastMessageContent != null) {
                boolean isSender = lastMessage.getSenderId().equals(userLogin.getId());

                if (lastMessage.getType().equals("file")) {
                    lastMessageContent = (isSender) ? "Bạn đã gửi một file đính kèm" : "Đã gửi một file đính kèm";
                } else {
                    lastMessageContent = (isSender) ? "Bạn: " + lastMessageContent : lastMessageContent;
                }
            }

            lastMessageLabel.setText(lastMessageContent);
        }
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
        this.parent.revalidate();
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

        // // Background
        // g2.setColor(this.getBackground());

        // // Round corners
        // g2.fillOval(0, 0, getWidth(), getHeight());

        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2.setColor(getBackground());
        g2.fillOval(x, y, size, size);

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // g2.setColor(Color.BLACK);
        // g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

        int size = Math.min(getWidth(), getHeight());
        int x = (getWidth() - size) / 2;
        int y = (getHeight() - size) / 2;

        g2.setColor(Color.BLACK);
        g2.drawOval(x, y, size - 1, size - 1);

        g2.dispose();
    }
}
