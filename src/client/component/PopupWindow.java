package component;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;

public class PopupWindow implements AppContext {

    private Dimension size;
    private String title;
    private JFrame frame;

    public PopupWindow(Dimension size, String title) {
        this.size = size;
        this.title = title;

        frame = new JFrame();
        frame.setTitle(this.title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(size);
        frame.setMinimumSize(size);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
    }

    public void close() {
        this.frame.dispose();
    }

    @Override
    public void draw() {
        this.frame.setVisible(true);
    }

    @Override
    public void switchContext(String newContext) {
        AppFrame appFrame = AppFrame.getInstance();
        AppContext context = appFrame.getContextPools().getContext(newContext);
        this.frame.setSize(context.getSize());
        this.frame.add(context.getRootComponent());
        this.frame.revalidate();
        this.frame.repaint();
    }

    @Override
    public Container getRootComponent() {
        return this.frame;
    }

    @Override
    public Dimension getSize() {
        return this.size;
    }

}
