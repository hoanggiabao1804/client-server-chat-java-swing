package component.menu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class MessageItem extends JTextArea {
    private final int arc = 24;

    public MessageItem(String text, Color bubbleColor) {
        super(text);
        setBackground(bubbleColor);

        setLineWrap(true);
        setWrapStyleWord(true);
        setEditable(false);
        setFocusable(false);
        setOpaque(false);

        setForeground(Color.WHITE);
        setFont(new Font("Consolas", Font.PLAIN, 15));
        setBorder(new EmptyBorder(10, 14, 10, 14));
    }

    @Override
    public Dimension getPreferredSize() {
        // int maxWidth = 300;
        // setSize(new Dimension(maxWidth, Short.MAX_VALUE));

        // Dimension preferred = super.getPreferredSize();

        // int width = Math.min(preferred.width, maxWidth);
        // int height = preferred.height;

        // return new Dimension(width, height);

        int maxWidth = 500;

        FontMetrics fm = getFontMetrics(getFont());
        int textWidth = fm.stringWidth(getText()) + getInsets().left + getInsets().right;

        int width = Math.min(textWidth + 10, maxWidth);

        setSize(new Dimension(width, Short.MAX_VALUE));

        Dimension preferred = super.getPreferredSize();

        return new Dimension(width, preferred.height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        g2.dispose();

        super.paintComponent(g);
    }
}
