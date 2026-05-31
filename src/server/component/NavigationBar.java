package component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NavigationBar implements AppContext {
    private final Container parent;
    private final Dimension size;

    // Root
    private JPanel rootContainer;

    // Title
    private JPanel titlePanel;
    private JLabel titleLabel;

    public NavigationBar(Container parent, Dimension size) {
        this.parent = parent;
        this.size = size;
        Color bgColor = new Color(0, 129, 138);

        // Root initialization
        rootContainer = new JPanel();

        // Title initialization
        titlePanel = new JPanel();
        titleLabel = new JLabel();

        // Root section
        rootContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        rootContainer.setPreferredSize(size);
        rootContainer.setBackground(bgColor);
        rootContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)); // Đường kẻ dưới
        rootContainer.add(titlePanel);

        // Title section
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(bgColor);
        titlePanel.add(titleLabel);

        titleLabel.setText("ZChat Server");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 30));
        titleLabel.setForeground(Color.white);
        // titleLabel.setBorder(new EmptyBorder(0, 30, 0, 0));
    }

    @Override
    public void draw() {
        this.parent.setSize(this.size);
        this.parent.add(this.rootContainer, BorderLayout.NORTH);
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
