package component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultFormatter;

import domain.Packet;
import domain.User;
import domain.dto.NetworkConfig;
import main.TCPServer;
import repository.UserRepository;
import util.FieldValidator;

public class ContentPage implements AppContext {
    private final Container parent;
    private final Dimension size;
    // private final DateTimeFormatter dateTimeFormatter =
    // DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final NetworkConfig networkConfig;

    // Font & Color
    private final Font headerFont;
    private final Font tableHeaderFont;
    private final Font textFont;

    // Data list
    private List<User> onlineUsers;

    // Root section
    private JPanel rootContainer;
    private JPanel contentPanel;

    // Config section
    private JPanel configContainer;
    private JLabel configLabel;
    private JPanel ipPanel;
    private JPanel portPanel;
    private JLabel ipLabel;
    private JTextField ipTextField;
    private JLabel portLabel;
    // private JTextField portTextField;
    private JSpinner portSpinner;
    private JPanel updatePanel;
    private JButton updateButton;
    private JButton cancelButton;
    private JPanel setStatusPanel;
    private JButton openCloseButton;
    private boolean isOpen;

    // Login Users section
    private JPanel tableContainer;
    private JLabel tableLabel;
    private Vector<Object> clientColumnNames;
    private Vector<Vector<Object>> clientRowData;
    private DefaultTableModel clientTableModel;
    private JTable clientTable;
    // private TableRowSorter<TableModel> clientTableRowSorter;
    private JScrollPane clientTableScrollPane;

    public ContentPage(Container parent, Dimension size) {
        this.parent = parent;
        this.size = size;
        this.networkConfig = TCPServer.getConfig();

        // Font & Color
        this.headerFont = new Font("Consolas", Font.BOLD, 25);
        this.tableHeaderFont = new Font("Consolas", Font.BOLD, 20);
        this.textFont = new Font("Consolas", Font.PLAIN, 20);

        // Data List
        this.onlineUsers = TCPServer.getOnlineUsers().stream().map(UserRepository.getInstance()::findById)
                .collect(Collectors.toList());

        // Root initialization
        rootContainer = new JPanel();
        contentPanel = new JPanel();

        // Config initialization
        configContainer = new JPanel();
        configLabel = new JLabel();
        ipPanel = new JPanel();
        portPanel = new JPanel();
        ipLabel = new JLabel();
        portLabel = new JLabel();
        ipTextField = new JTextField();
        // portTextField = new JTextField();
        // spinnerNumberModel = new SpinnerNumberModel(page, 1, totalPages, 1);
        // pageOffsetSpinner = new JSpinner(new SpinnerNumberModel(page, 1, totalPages,
        // 1));
        portSpinner = new JSpinner(new SpinnerNumberModel(1024, 1024, 49151, 1));
        updatePanel = new JPanel();
        updateButton = new JButton();
        cancelButton = new JButton();
        setStatusPanel = new JPanel();
        openCloseButton = new JButton();
        isOpen = true;

        // Login users initialization
        tableContainer = new JPanel();
        tableLabel = new JLabel();
        clientColumnNames = new Vector<>(Arrays.asList("Mã client", "Tên đăng nhập", "Trực tuyến lúc", ""));
        clientRowData = new Vector<>();
        onlineUsers.forEach(item -> {
            JButton closeButton = new JButton("Close");
            closeButton.setFocusable(false);
            closeButton.setPreferredSize(new Dimension(50, 40));
            closeButton.addActionListener(closeAction(item.getId()));

            Vector<Object> row = new Vector<>(Arrays.asList(
                    item.getId(),
                    item.getUsername(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    closeButton));

            clientRowData.add(row);
        });
        clientTableModel = new DefaultTableModel(clientRowData, clientColumnNames) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                    case 1:
                    case 2:
                        return String.class;
                    case 3:
                        return JButton.class;
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        clientTable = new JTable(clientTableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width + 20;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                int preferredWidth = Math.max(rendererWidth + getIntercellSpacing().width,
                        tableColumn.getPreferredWidth());
                tableColumn.setPreferredWidth(preferredWidth);
                return component;
            }
        };
        clientTable.setRowHeight(35);
        clientTable.setFont(textFont);
        clientTable.getTableHeader().setFont(tableHeaderFont);
        clientTable.setDefaultRenderer(JButton.class, new JTableButtonRenderer());
        clientTable.setDefaultEditor(JButton.class, new JTableButtonEditor());
        // clientTableRowSorter = new TableRowSorter<>(clientTableModel);
        clientTableScrollPane = new JScrollPane(clientTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints gbc = new GridBagConstraints();
        // gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        GridBagConstraints gbc1 = new GridBagConstraints();
        // gbc1.insets = new Insets(2, 0, 2, 0);
        gbc1.anchor = GridBagConstraints.WEST;
        gbc1.gridwidth = GridBagConstraints.REMAINDER;
        // gbc1.gridwidth = GridBagConstraints.NONE;

        // Root section
        rootContainer.setLayout(new BorderLayout());
        rootContainer.add(contentPanel, BorderLayout.CENTER);

        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.white);
        contentPanel.add(configContainer);
        contentPanel.add(tableContainer);

        // Config section
        configContainer.setLayout(new BoxLayout(configContainer, BoxLayout.Y_AXIS));
        configContainer.setBackground(Color.white);
        configContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        configContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        configContainer.add(configLabel, gbc1);
        configContainer.add(ipPanel, gbc1);
        configContainer.add(portPanel, gbc1);
        configContainer.add(updatePanel, gbc1);
        configContainer.add(setStatusPanel, gbc1);

        configLabel.setText("1. Thông tin cấu hình");
        configLabel.setFont(headerFont);
        configLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ipPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        ipPanel.setBackground(Color.white);
        ipPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        ipPanel.add(ipLabel);
        ipPanel.add(ipTextField);

        ipLabel.setPreferredSize(new Dimension(170, 30));
        ipLabel.setText("Địa chỉ IPv4");
        ipLabel.setFont(textFont);

        ipTextField.setPreferredSize(new Dimension(150, 30));
        ipTextField.setText(networkConfig.getIpAddress());
        ipTextField.setFont(textFont);
        ipTextField.setBackground(Color.white);
        ipTextField.setForeground(Color.black);
        ipTextField.setEditable(false);

        portPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        portPanel.setBackground(Color.white);
        portPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        portPanel.add(portLabel);
        portPanel.add(portSpinner);

        portLabel.setPreferredSize(new Dimension(170, 30));
        portLabel.setText("Port");
        portLabel.setFont(textFont);

        portSpinner.setPreferredSize(new Dimension(150, 30));
        portSpinner.setValue(networkConfig.getPort());
        portSpinner.setFont(textFont);
        portSpinner.setBackground(Color.white);
        portSpinner.setForeground(Color.black);
        portSpinner.setEnabled(false);
        JFormattedTextField portTextField = ((DefaultEditor) portSpinner.getEditor()).getTextField();
        portTextField.setHorizontalAlignment(JTextField.LEFT);

        DefaultFormatter formatter = (DefaultFormatter) portTextField.getFormatter();
        formatter.setCommitsOnValidEdit(true);

        updatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        updatePanel.setBackground(Color.white);
        updatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel buttonGap = new JPanel();
        buttonGap.setPreferredSize(new Dimension(5, 0));
        buttonGap.setBackground(Color.white);
        updatePanel.add(updateButton);
        updatePanel.add(buttonGap);
        updatePanel.add(cancelButton);

        updateButton.setText("Cập nhật");
        updateButton.setFocusable(false);
        updateButton.addActionListener(l -> {

            if (!ipTextField.isEditable()) {
                updateButton.setText("Lưu");
                cancelButton.setEnabled(true);
                ipTextField.setEditable(true);
                portSpinner.setEnabled(true);
            } else {
                String ipValidateResult = FieldValidator.validateIPv4Address(ipTextField.getText().strip());
                if (!ipValidateResult.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            null,
                            ipValidateResult,
                            "Lỗi cấu hình IPv4",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (JOptionPane.showConfirmDialog(null,
                        "Bạn có chắc muốn cập nhật cấu hình như trên?\nServer sẽ được khởi động lại sau đó. Các client sẽ tạm thời mất kết nối tới server.",
                        "Cập nhật cấu hình?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                    updateButton.setText("Cập nhật");
                    cancelButton.setEnabled(false);
                    ipTextField.setEditable(false);
                    portSpinner.setEnabled(false);

                    networkConfig.setIpAddress(ipTextField.getText().strip());
                    networkConfig.setPort((Integer) portSpinner.getValue());

                    onlineUsers.clear();
                    for (int i = 0; i < clientTableModel.getRowCount(); i++) {
                        clientTableModel.removeRow(i);
                    }

                    TCPServer.updateNetWorkConfig(networkConfig);

                    TCPServer.openNewConnection();
                }
            }
        });

        cancelButton.setText("Hủy");
        cancelButton.setFocusable(false);
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(l -> {
            ipTextField.setText(networkConfig.getIpAddress());
            portSpinner.setValue(networkConfig.getPort());
            ipTextField.setEditable(false);
            portSpinner.setEnabled(false);
            updateButton.setText("Cập nhật");
            cancelButton.setEnabled(false);
        });

        setStatusPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 10));
        setStatusPanel.setBackground(Color.white);
        setStatusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        setStatusPanel.add(openCloseButton);

        openCloseButton.setText("Đóng Server");
        openCloseButton.setFocusable(false);
        openCloseButton.addActionListener(l -> {
            if (isOpen) {
                if (JOptionPane.showConfirmDialog(null,
                        "Bạn có chắc muốn đóng server?", "Đóng Server?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                    TCPServer.setOpen(false);
                    onlineUsers.clear();
                    for (int i = 0; i < clientTableModel.getRowCount(); i++) {
                        clientTableModel.removeRow(i);
                    }
                    openCloseButton.setText("Mở Server");
                    isOpen = false;
                }
            } else {
                if (JOptionPane.showConfirmDialog(null,
                        "Bạn có chắc muốn mở lại server?", "Mở Server?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {

                    TCPServer.setOpen(true);
                    openCloseButton.setText("Đóng Server");
                    isOpen = true;
                }
            }
        });

        // Login users section
        tableContainer.setLayout(new BorderLayout(0, 15));
        tableContainer.setBackground(Color.white);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        tableContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableContainer.add(tableLabel, BorderLayout.NORTH);
        tableContainer.add(clientTableScrollPane, BorderLayout.CENTER);

        tableLabel.setText("2. Danh sách client đang trực tuyến");
        tableLabel.setFont(headerFont);
    }

    public void addClient(String userId) {
        User newUser = UserRepository.getInstance().findById(userId);
        onlineUsers.add(0, newUser);

        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(new Font("Consolas", Font.BOLD, 20));
        closeButton.setFocusable(false);
        closeButton.setPreferredSize(new Dimension(50, 40));
        closeButton.addActionListener(closeAction(userId));
        clientTableModel.insertRow(0, new Object[] {
                newUser.getId(),
                newUser.getUsername(),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                closeButton
        });
    }

    public void removeClient(String userId) {
        onlineUsers.removeIf(user -> user.getId().equals(userId));

        for (int i = 0; i < clientTableModel.getRowCount(); i++) {

            String currentId = (String) clientTableModel.getValueAt(i, 0);

            if (clientTable.isEditing()) {
                clientTable.getCellEditor().cancelCellEditing();
            }

            if (currentId.equals(userId)) {
                clientTableModel.removeRow(i);
                break;
            }
        }
    }

    private ActionListener closeAction(String userId) {
        return l -> {
            if (JOptionPane.showConfirmDialog(null,
                    "Bạn có chắc muốn đóng kết nối tới client này không?", "Đóng kết nối tới client?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                Packet packet = new Packet(networkConfig.getIpAddress(), networkConfig.getPort(),
                        "Server đã đóng kết nối tới bạn.",
                        "String", "connection/close");

                removeClient(userId);

                TCPServer.unicast(packet, userId);
                TCPServer.removeClient(userId);
            }
        };
    }

    @Override
    public void draw() {
        this.parent.setSize(this.size);
        this.parent.add(this.rootContainer, BorderLayout.CENTER);
        this.parent.revalidate();
        this.parent.repaint();
    }

    @Override
    public void switchContext(String newContext) {
        this.parent.remove(this.rootContainer);
        Menu menu = Menu.getInstance();
        AppContext context = menu.getContext(newContext);
        this.parent.setMinimumSize(context.getSize());
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

class JTableButtonRenderer implements TableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (value instanceof JButton) {
            return (JButton) value;
        }
        return new JLabel();
    }
}

class JTableButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private JButton button;

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value instanceof JButton) {
            this.button = (JButton) value;
            return this.button;
        }
        return null;
    }

    @Override
    public Object getCellEditorValue() {
        return this.button;
    }
}