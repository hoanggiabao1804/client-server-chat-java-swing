package component.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class FileMessageItem extends JPanel {
    private final int arc = 24;
    private final Color bubbleColor;

    public FileMessageItem(String fileName, String fileSize, Icon fileIcon) {
        this.bubbleColor = new Color(240, 240, 240);

        setOpaque(false);
        setLayout(new BorderLayout(10, 0));
        setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel iconLabel = new JLabel(fileIcon);

        JLabel nameLabel = new JLabel(fileName);
        nameLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLACK);

        JLabel sizeLabel = new JLabel(fileSize);
        sizeLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        sizeLabel.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(nameLabel);
        textPanel.add(sizeLabel);

        add(iconLabel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension preferred = super.getPreferredSize();

        int maxWidth = 500;
        int minWidth = 220;

        int width = Math.min(Math.max(preferred.width, minWidth), maxWidth);
        int height = Math.max(preferred.height, 54);

        return new Dimension(width, height);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension preferred = getPreferredSize();
        return new Dimension(preferred.width, preferred.height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(bubbleColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        g2.dispose();

        super.paintComponent(g);
    }

}
