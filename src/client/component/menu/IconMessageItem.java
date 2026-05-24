package component.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class IconMessageItem extends JPanel {
    private final int arc = 24;
    private final Color bubbleColor;
    private final int iconSize;

    public IconMessageItem(String iconPath, int iconSize, Color bubbleColor) {
        this.iconSize = iconSize;
        this.bubbleColor = bubbleColor;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(8, 8, 8, 8));

        ImageIcon rawIcon = new ImageIcon(iconPath);
        Image scaledImage = rawIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);

        JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setVerticalAlignment(SwingConstants.CENTER);

        add(iconLabel, BorderLayout.CENTER);
    }

    @Override
    public Dimension getPreferredSize() {
        int padding = 16;
        return new Dimension(iconSize + padding, iconSize + padding);
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (bubbleColor != null) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bubbleColor);
            g2.fillRoundRect(
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    arc,
                    arc);

            g2.dispose();
        }

        super.paintComponent(g);
    }
}
