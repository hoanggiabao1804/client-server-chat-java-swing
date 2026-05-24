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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.EmptyBorder;

import component.AppContext;
import component.AppFrame;

public class HomePage implements AppContext {
    private final Container parent;
    private Dimension size;

    // Font & Color
    private final Font titleFont;
    private final Font introductionFont;
    private final Font labelFont;
    private final Font textFont;

    // Root section
    private JPanel rootContainer;
    private JPanel scrollPanel;
    private JScrollPane rootScrollPane;

    // Header section
    private JPanel headerRootContainer;
    private JLabel headerTitle;

    // Body section
    private JPanel bodyRootContainer;
    private JLabel introductionLabel;
    private String[] functionList = {
            "1. Tạo tài khoản thủ thư",
            "2. Đăng nhập, đăng xuất",
            "3. Quản lý độc giả",
            "4. Quản lý sách",
            "5. Lập phiếu mượn sách",
            "6. Lập phiếu trả sách",
            "7. Các thống kê cơ bản"
    };

    private GridBagConstraints gbc;
    private GridBagConstraints gbc1;

    private Map<String, List<String>> subFunctionMap = new HashMap<>();

    public HomePage(Container parent, Dimension size) {
        this.parent = parent;
        this.size = size;

        // Font & Color
        this.titleFont = new Font("Consolas", Font.BOLD, 30);
        this.introductionFont = new Font("Consolas", Font.ITALIC, 20);
        this.labelFont = new Font("Consolas", Font.BOLD, 20);
        this.textFont = new Font("Consolas", Font.PLAIN, 15);

        // Root initialization
        rootContainer = new JPanel();
        scrollPanel = new JPanel();
        rootScrollPane = new JScrollPane(scrollPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Header intialization
        headerRootContainer = new JPanel();
        headerTitle = new JLabel();

        // Body intialization
        bodyRootContainer = new JPanel();
        introductionLabel = new JLabel();

        subFunctionMap.put(functionList[2], new ArrayList<>(Arrays.asList(
                "3.1. Xem danh sách độc giả trong thư viện",
                "3.2. Thêm độc giả",
                "3.3. Chỉnh sửa thông tin một độc giả",
                "3.4. Xóa thông tin một độc giả",
                "3.5. Tìm kiếm độc giả theo CMND/CCCD",
                "3.6. Tìm kiếm độc giả theo họ tên")));

        subFunctionMap.put(functionList[3], new ArrayList<>(Arrays.asList(
                "4.1. Xem danh sách các sách trong thư viện",
                "4.2. Thêm sách",
                "4.3. Chỉnh sửa thông tin một quyển sách",
                "4.4. Xóa thông tin sách",
                "4.5. Tìm kiếm sách theo ISBN",
                "4.6. Tìm kiếm sách theo tên sách")));

        subFunctionMap.put(functionList[6], new ArrayList<>(Arrays.asList(
                "7.1. Thống kê số lượng sách trong thư viện",
                "7.2. Thống kê số lượng sách theo thể loại",
                "7.3. Thống kê số lượng độc giả",
                "7.4. Thống kê số lượng độc giả theo giới tính",
                "7.5. Thống kê số sách đang được mượn",
                "7.6. Thống kê danh sách độc giả bị trễ hạn")));

        gbc = new GridBagConstraints();
        // gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc1 = new GridBagConstraints();
        // gbc1.insets = new Insets(2, 0, 2, 0);
        gbc1.anchor = GridBagConstraints.WEST;
        gbc1.gridwidth = GridBagConstraints.REMAINDER;

        // Root section
        rootContainer.setLayout(new BorderLayout());
        rootContainer.add(rootScrollPane);

        scrollPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        scrollPanel.setPreferredSize(new Dimension(size.width - 10, 800));
        scrollPanel.setBackground(Color.white);
        scrollPanel.add(headerRootContainer);
        scrollPanel.add(bodyRootContainer);

        rootScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        rootScrollPane.setLayout(new ScrollPaneLayout());
        rootScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Header section
        headerRootContainer.setLayout(new BorderLayout());
        headerRootContainer.setPreferredSize(new Dimension(size.width - 40, 35));
        headerRootContainer.setBackground(Color.white);
        headerRootContainer.setForeground(Color.black);
        headerRootContainer.add(headerTitle);

        headerTitle.setText("<html>Chào mừng đến với ứng dụng quản lý thư viện</html>");
        headerTitle.setFont(titleFont);
        headerTitle.setBackground(Color.white);
        headerTitle.setForeground(Color.black);

        // Body section
        bodyRootContainer.setLayout(new GridBagLayout());
        bodyRootContainer.setBackground(Color.white);
        bodyRootContainer.add(introductionLabel, gbc1);

        introductionLabel.setText(
                "<html>Đây là ứng dụng giúp thủ thư có thể quản lý sách, độc giả của thư viện. Đồng thời cho phép thủ thư tạo phiếu mượn và trả sách. Đi kèm với đó là các thống kê cơ bản cho dữ liệu của thư viện. Các chức năng chính bao gồm: </html>");
        introductionLabel.setPreferredSize(new Dimension(size.width - 30, 100));
        introductionLabel.setFont(introductionFont);
        introductionLabel.setBackground(Color.white);
        introductionLabel.setForeground(Color.black);
        introductionLabel.setBorder(new EmptyBorder(0, 0, 0, 0));

        for (int i = 0; i < functionList.length; ++i) {
            bodyRootContainer.add(this.createFunctionBlockPanel(i), gbc1);
        }
    }

    private JPanel createFunctionBlockPanel(int index) {
        String functionLabel = functionList[index];
        List<String> subFunctionLabels = subFunctionMap.get(functionLabel);

        JPanel functionRootPanel = new JPanel();
        JLabel functionTextLabel = new JLabel();

        functionRootPanel.setLayout(new GridBagLayout());
        functionRootPanel.setBackground(Color.white);
        functionRootPanel.add(functionTextLabel, gbc1);

        functionTextLabel.setText(functionLabel);
        functionTextLabel.setFont(labelFont);
        functionTextLabel.setBackground(Color.white);
        functionTextLabel.setForeground(Color.black);
        functionTextLabel.setBorder(new EmptyBorder(0, 40, 0, 0));

        if (subFunctionLabels != null) {
            subFunctionLabels.forEach(item -> {
                JLabel subLabel = new JLabel();
                subLabel.setText(item);
                subLabel.setFont(textFont);
                subLabel.setBackground(Color.white);
                subLabel.setForeground(Color.black);
                subLabel.setBorder(new EmptyBorder(0, 80, 0, 0));

                functionRootPanel.add(subLabel, gbc1);
            });
        }

        return functionRootPanel;
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
